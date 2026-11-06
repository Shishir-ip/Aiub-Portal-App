package com.shishir.aiubportal.portal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.shishir.aiubportal.settings.AppSettings
import com.shishir.aiubportal.settings.SettingsStore
import org.json.JSONObject

/**
 * Owns the portal WebView. Loads portal.aiub.edu and injects the full
 * AIUB Portal+ Enhanced extension (chrome-API shim + content-script bundles +
 * theme CSS) so the app gets the exact same redesigned look as the browser
 * extension — light and night — plus the in-page captcha solver.
 */
class PortalController(context: Context) {

    private val appContext = context.applicationContext
    val settings = SettingsStore(appContext)
    val sessionKeeper = SessionKeeper(appContext)

    var currentSettings: AppSettings = AppSettings()
        private set

    /** Set by MainActivity — launches the system file picker for uploads. */
    var fileChooserLauncher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>? = null
    private var pendingFileChooser: android.webkit.ValueCallback<Array<Uri>>? = null

    /** Called by MainActivity when the picker returns. */
    fun onFileChooserResult(uris: Array<Uri>?) {
        pendingFileChooser?.onReceiveValue(uris ?: arrayOf())
        pendingFileChooser = null
    }

    private val assetLoader = WebViewAssetLoader.Builder()
        .setDomain("appassets.androidplatform.net")
        .addPathHandler("/assets/ext/", ExtAssetsHandler(appContext))
        .build()

    private val darkCss: String by lazy {
        runCatching { appContext.assets.open("ext/src/theme.css").bufferedReader().use { it.readText() } }
            .getOrDefault("")
    }

    @SuppressLint("SetJavaScriptEnabled")
    val webView: WebView = WebView(context).apply {
        setBackgroundColor(android.graphics.Color.parseColor("#0e131b"))
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.userAgentString = settings.userAgentString
            .replace("; wv", "") /* look like a normal browser */
        android.webkit.CookieManager.getInstance().setAcceptCookie(true)
        android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

        webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                callback: android.webkit.ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {
                val launcher = fileChooserLauncher ?: return false
                pendingFileChooser = callback
                launcher.launch(params.createIntent())
                return true
            }
        }

        setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
            this@PortalController.onDownloadRequested(url, contentDisposition, mimetype)
        }

        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url.startsWith("https://portal.aiub.edu")) {
                    injectCore(view)
                }
            }

            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                if (request.url.host == "appassets.androidplatform.net") {
                    return assetLoader.shouldInterceptRequest(request.url)
                }
                return null
            }

            override fun onPageFinished(view: WebView, url: String) {
                val s = currentSettings
                sessionKeeper.onPageFinished(view, url, s, s.captchaEnabled)
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean = false
        }

        loadUrl("https://portal.aiub.edu/")
    }

    fun reload() { webView.reload() }

    fun destroy() { webView.destroy() }

    fun onDownloadRequested(url: String, contentDisposition: String?, mimetype: String?) {
        val appContext = appContext
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val uri = DownloadHandler.download(appContext, url, contentDisposition, mimetype)
                android.widget.Toast.makeText(
                    appContext,
                    "Downloaded to Documents/Aiub Portal ✓",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                DownloadEvents.downloaded.value = uri
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    appContext,
                    "Download failed: ${e.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /** Injected at page start: settings for the shim, then the shim itself. */
    private fun injectCore(view: WebView) {
        val s = currentSettings
        val androidSettings = JSONObject().apply {
            put("theme", s.theme)
            put("captchaEnabled", s.captchaEnabled)
            put("extensionEnabled", true)
        }
        view.evaluateJavascript("window.ANDROID_SETTINGS = ${androidSettings};", null)
        view.evaluateJavascript(
            "if (window.APXShim && APXShim.setAndroidSettings) APXShim.setAndroidSettings(${androidSettings});", null
        )
        view.evaluateJavascript(
            "if (window.APXShim && APXShim.injectForPath) APXShim.injectForPath(location.pathname);", null
        )
    }

    /** Live-apply new settings (theme, captcha toggle) to the open page. */
    fun updateSettings(s: AppSettings) {
        val themeChanged = currentSettings.theme != s.theme
        val captchaChanged = currentSettings.captchaEnabled != s.captchaEnabled
        currentSettings = s

        if (webView.url != null && (themeChanged || captchaChanged)) {
            val androidSettings = JSONObject().apply {
                put("theme", s.theme)
                put("captchaEnabled", s.captchaEnabled)
                put("extensionEnabled", true)
            }
            webView.evaluateJavascript(
                "if (window.APXShim && APXShim.setAndroidSettings) APXShim.setAndroidSettings(${androidSettings});",
                null
            )
        }
    }

    private class ExtAssetsHandler(private val context: Context) :
        WebViewAssetLoader.PathHandler {

        override fun handle(path: String): WebResourceResponse? {
            val rel = path.removePrefix("/")
            if (rel.isBlank() || rel.contains("..")) return null
            return try {
                val stream = context.assets.open("ext/$rel")
                val mime = when (rel.substringAfterLast('.', "").lowercase()) {
                    "js" -> "application/javascript"
                    "css" -> "text/css"
                    "html" -> "text/html"
                    "json" -> "application/json"
                    "png" -> "image/png"
                    "svg" -> "image/svg+xml"
                    "gz" -> "application/gzip"
                    "wasm" -> "application/wasm"
                    else -> "application/octet-stream"
                }
                val response = WebResourceResponse(mime, null, stream)
                response.responseHeaders = mapOf(
                    "Access-Control-Allow-Origin" to "*",
                    "Cache-Control" to "public, max-age=86400"
                )
                response
            } catch (e: Exception) {
                null
            }
        }
    }
}
