package com.shishir.aiubportal.portal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import com.shishir.aiubportal.settings.AppSettings
import com.shishir.aiubportal.settings.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Owns the portal WebView. Loads portal.aiub.edu and injects the full
 * AIUB Portal+ Enhanced extension (chrome-API shim + content-script bundles +
 * theme CSS) so the app gets the exact same redesigned look as the browser
 * extension — light and night — plus phone-optimized layout.
 */
class PortalController(context: Context) {

    private val appContext = context.applicationContext
    val settings = SettingsStore(appContext)
    val sessionKeeper = SessionKeeper(appContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    var currentSettings: AppSettings = AppSettings()
        private set

    /** Set by MainActivity — launches the system file picker for uploads. */
    var fileChooserLauncher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>? = null
    private var pendingFileChooser: android.webkit.ValueCallback<Array<Uri>>? = null

    /** Called by MainActivity when the system picker returns. */
    fun onFileChooserResult(uris: Array<Uri>?) {
        pendingFileChooser?.onReceiveValue(uris ?: arrayOf())
        pendingFileChooser = null
    }

    private var captchaSolving = false

    private val darkCss: String by lazy { readAsset("ext/src/theme.css") }
    private val contentCss: String by lazy { readAsset("ext/assets/content-ozNVyx55.css") }

    private val phoneCss = """
        /* AIUB Portal App — phone layout */
        html body #navigation-bar { display: none !important; }
        html body #main-content { width: 100% !important; max-width: 100% !important; float: none !important; }
        html body .portal-body { width: 100% !important; }
        html body { overflow-x: hidden !important; }
    """.trimIndent()

    private val assetLoader = WebViewAssetLoader.Builder()
        .setDomain("appassets.androidplatform.net")
        .addPathHandler("/assets/ext/", ExtAssetsHandler(appContext))
        .build()

    /** JS → Kotlin bridge for blob/data downloads that the page generates. */
    inner class DownloadBridge {
        @JavascriptInterface
        fun onBase64(base64: String, name: String) {
            if (base64 == "ERROR") return
            scope.launch {
                try {
                    val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                    DownloadHandler.saveBytes(appContext, bytes, name, null)
                    android.widget.Toast.makeText(
                        appContext, "Saved to Documents/Aiub Portal ✓", android.widget.Toast.LENGTH_SHORT
                    ).show()
                    DownloadEvents.downloaded.value = Uri.EMPTY
                } catch (e: Exception) {
                    android.widget.Toast.makeText(
                        appContext, "Download failed: ${e.message}", android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    val webView: WebView = WebView(context).apply {
        setBackgroundColor(Color.parseColor("#0e131b"))
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setSupportMultipleWindows(true)
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.userAgentString = settings.userAgentString
            .replace("; wv", "") /* look like a normal browser */
        android.webkit.CookieManager.getInstance().setAcceptCookie(true)
        android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

        addJavascriptInterface(DownloadBridge(), "ACDownload")

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

            /* window.open / target=_blank — load in this WebView instead of dying silently */
            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: android.os.Message
            ): Boolean {
                val temp = WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(v: WebView, url: String) {
                            view.loadUrl(url)
                            v.destroy()
                        }

                        override fun shouldOverrideUrlLoading(v: WebView, request: WebResourceRequest): Boolean {
                            view.loadUrl(request.url.toString())
                            v.destroy()
                            return true
                        }
                    }
                }
                (resultMsg.obj as WebView.WebViewTransport).webView = temp
                resultMsg.sendToTarget()
                return true
            }
        }

        setDownloadListener { url, _, contentDisposition, mimetype, _ ->
            if (url.startsWith("http")) {
                onDownloadRequested(url, contentDisposition, mimetype)
            } else {
                /* blob:/data: — fetch the bytes inside the page and bridge them over */
                val fetchJs = "(function(){fetch(" + jsString(url) + ",{credentials:'include'})" +
                    ".then(function(r){return r.blob()}).then(function(b){" +
                    "var r=new FileReader();r.onload=function(){ACDownload.onBase64(String(this.result).split(',').pop()||'','download')};r.readAsDataURL(b)})" +
                    ".catch(function(e){ACDownload.onBase64('ERROR',String(e))});})();"
                evaluateJavascript(fetchJs, null)
            }
        }

        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url.startsWith("https://portal.aiub.edu")) {
                    /* inline injection — applies before first paint, no flash */
                    injectCore(view, url)
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
                if (url.startsWith("https://portal.aiub.edu")) {
                    /* belt & braces: make sure the shim ran for this page */
                    view.evaluateJavascript(
                        "if (window.APXShim && APXShim.injectForPath) APXShim.injectForPath(location.pathname);", null
                    )
                    val s = currentSettings
                    if (s.captchaEnabled && SessionKeeper.isLoginPage(url)) {
                        startCaptchaLoop(view)
                    } else {
                        captchaSolving = false
                    }
                    sessionKeeper.onPageFinished(view, url, s, s.captchaEnabled)
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean = false
        }

        loadUrl("https://portal.aiub.edu/")
    }

    fun onDownloadRequested(url: String, contentDisposition: String?, mimetype: String?) {
        scope.launch {
            try {
                val uri = DownloadHandler.download(appContext, url, contentDisposition, mimetype)
                android.widget.Toast.makeText(
                    appContext, "Saved to Documents/Aiub Portal ✓", android.widget.Toast.LENGTH_SHORT
                ).show()
                DownloadEvents.downloaded.value = Uri.EMPTY
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    appContext, "Download failed: ${e.message}", android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /* ------------------------- captcha (native OCR) ------------------------ */

    private fun startCaptchaLoop(view: WebView) {
        if (captchaSolving) return
        captchaSolving = true
        scope.launch {
            var attempts = 0
            while (attempts < 12) {
                val src = jsStringResult(view, "var i=document.getElementById('CaptchaImage');i?i.src:'';")
                var answer: Int? = null
                if (src.isNotBlank()) {
                    val bmp = withContext(Dispatchers.IO) { fetchBitmap(src) }
                    if (bmp != null) {
                        answer = withContext(Dispatchers.Default) {
                            runCatching { CaptchaSolver.solve(appContext, bmp) }.getOrNull()
                        }
                    }
                }
                if (answer != null) {
                    withContext(Dispatchers.Main) {
                        view.evaluateJavascript(
                            "var i=document.getElementById('CaptchaInputText');" +
                                "if(i){i.value='$answer';i.dispatchEvent(new Event('input',{bubbles:true}));}",
                            null
                        )
                        android.widget.Toast.makeText(
                            appContext, "Captcha solved: $answer ✓", android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                    captchaSolving = false
                    return@launch
                }
                attempts++
                /* unreadable → click the page's own refresh button, new equation loads */
                withContext(Dispatchers.Main) {
                    view.evaluateJavascript(
                        "(function(){var b=document.querySelector('#captcha a.btn, a.btn-sm.btn-primary');" +
                            "if(b)b.click();})();", null
                    )
                }
                delay(1800)
            }
            captchaSolving = false
        }
    }

    private suspend fun jsStringResult(view: WebView, js: String): String {
        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            view.evaluateJavascript(js) { value ->
                cont.resume(value?.removeSurrounding("\"") ?: "") { }
            }
        }
    }

    private fun fetchBitmap(url: String): Bitmap? {
        return runCatching {
            val conn = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            CookieManager.getInstance().getCookie(url)?.let { conn.setRequestProperty("Cookie", it) }
            conn.inputStream.use { input -> android.graphics.BitmapFactory.decodeStream(input) }
        }.getOrNull()
    }

    /* --------------------------- theme injection --------------------------- */

    private fun isSystemNight(): Boolean =
        (appContext.resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES

    /**
     * Injected at page start, before first paint:
     *  1. app settings for the shim
     *  2. AIUB+ theme CSS inlined as a <style> (no flash)
     *  3. apx-dark class for night/auto
     *  4. the shim script (chrome API + page bundles)
     * The "default" theme skips the extension look entirely.
     */
    private fun injectCore(view: WebView, url: String) {
        val s = currentSettings
        val dark = when (s.theme) {
            "night" -> true
            "auto" -> isSystemNight()
            else -> false /* default + light */
        }
        val useExt = s.theme != "default"

        val androidSettings = JSONObject().apply {
            put("theme", s.theme)
            put("captchaEnabled", s.captchaEnabled)
            put("extensionEnabled", useExt)
        }

        val css = if (useExt) darkCss + "\n" + contentCss + "\n" + phoneCss else ""
        val js = """
            (function(){
                window.ANDROID_SETTINGS = ${androidSettings};
                var el = document.documentElement;
                if ($dark) { el.classList.add('apx-dark'); } else { el.classList.remove('apx-dark'); }
                var st = document.getElementById('aiub-app-style');
                if (!st) { st = document.createElement('style'); st.id = 'aiub-app-style';
                    (document.head || document.documentElement).appendChild(st); }
                st.textContent = ${ThemeInjector.jsonString(css)};
                if ($useExt && !window.__apxShimLoaded) {
                    window.__apxShimLoaded = true;
                    var sc = document.createElement('script');
                    sc.src = 'https://appassets.androidplatform.net/assets/ext/shim.js';
                    sc.onerror = function(){ console.warn('[AIUB] shim failed to load'); };
                    (document.head || document.documentElement).appendChild(sc);
                }
            })();
        """.trimIndent()
        view.evaluateJavascript(js, null)
    }

    /** Live-apply new settings (theme, captcha toggle) to the open page. */
    fun updateSettings(s: AppSettings) {
        val themeChanged = currentSettings.theme != s.theme
        val captchaChanged = currentSettings.captchaEnabled != s.captchaEnabled
        currentSettings = s

        if (webView.url != null && (themeChanged || captchaChanged)) {
            val dark = when (s.theme) {
                "night" -> true
                "auto" -> isSystemNight()
                else -> false
            }
            val useExt = s.theme != "default"
            val css = if (useExt) darkCss + "\n" + contentCss + "\n" + phoneCss else ""
            val androidSettings = JSONObject().apply {
                put("theme", s.theme)
                put("captchaEnabled", s.captchaEnabled)
                put("extensionEnabled", useExt)
            }
            webView.evaluateJavascript(
                """
                (function(){
                    window.ANDROID_SETTINGS = ${androidSettings};
                    var el = document.documentElement;
                    if ($dark) { el.classList.add('apx-dark'); } else { el.classList.remove('apx-dark'); }
                    var st = document.getElementById('aiub-app-style');
                    if (!st) { st = document.createElement('style'); st.id = 'aiub-app-style';
                        (document.head || document.documentElement).appendChild(st); }
                    st.textContent = ${ThemeInjector.jsonString(css)};
                    if (window.APXShim && APXShim.setAndroidSettings) {
                        APXShim.setAndroidSettings(${androidSettings});
                    }
                })();
                """.trimIndent(), null
            )
        }
    }

    private fun readAsset(path: String): String =
        runCatching { appContext.assets.open(path).bufferedReader().use { it.readText() } }.getOrDefault("")

    private fun jsString(s: String): String =
        "\"" + s.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"") + "\""

    fun reload() { webView.reload() }

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

    fun destroy() {
        scope.cancel()
        webView.destroy()
    }
}
