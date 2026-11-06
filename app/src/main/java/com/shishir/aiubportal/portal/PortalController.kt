package com.shishir.aiubportal.portal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.shishir.aiubportal.settings.AppSettings
import com.shishir.aiubportal.settings.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Owns the portal WebView. Loads portal.aiub.edu, applies the Day/Night theme
 * (dark.css injected inline at navigation start — no flash), solves the login
 * captcha natively, and captures downloads/uploads.
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

    /** Used by the AIUB website tab's file chooser. */
    fun setPendingFileChooser(callback: android.webkit.ValueCallback<Array<Uri>>) {
        pendingFileChooser = callback
    }

    /** Called by MainActivity when the system picker returns. */
    fun onFileChooserResult(uris: Array<Uri>?) {
        pendingFileChooser?.onReceiveValue(uris ?: arrayOf())
        pendingFileChooser = null
    }

    private var captchaSolving = false

    private val nightCss: String by lazy {
        readAsset("dark.css") + "\n" + """
            /* AIUB Portal App — phone layout */
            html body #navigation-bar { display: none !important; }
            html body #main-content { width: 100% !important; max-width: 100% !important; float: none !important; }
            html body .portal-body { width: 100% !important; }
            html body { overflow-x: hidden !important; }
        """.trimIndent()
    }

    @SuppressLint("SetJavaScriptEnabled")
    val webView: WebView = WebView(context).apply {
        setBackgroundColor(Color.parseColor("#0e131b"))
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setSupportMultipleWindows(true)
        settings.javaScriptCanOpenWindowsAutomatically = true
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
            onDownloadRequested(url, contentDisposition, mimetype)
        }

        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url.startsWith("https://portal.aiub.edu")) {
                    applyTheme(view)
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                applyTheme(view)
                val s = currentSettings
                if (s.captchaEnabled && SessionKeeper.isLoginPage(url)) {
                    startCaptchaLoop(view)
                } else {
                    captchaSolving = false
                }
                sessionKeeper.onPageFinished(view, url, s, s.captchaEnabled)
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
            while (attempts < 15) {
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
                kotlinx.coroutines.delay(1800)
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

    /** Day = original portal. Night = dark.css (the standalone dark theme). */
    fun applyTheme(view: WebView) {
        val s = currentSettings
        val night = when (s.theme) {
            "night" -> true
            "auto" -> isSystemNight()
            else -> false /* day */
        }
        val css = if (night) nightCss else ""
        val js = """
            (function(){
                var el = document.documentElement;
                if (${if (night) "true" else "false"}) { el.classList.add('apdm-dark'); }
                else { el.classList.remove('apdm-dark'); }
                var st = document.getElementById('aiub-app-style');
                if (!st) { st = document.createElement('style'); st.id = 'aiub-app-style';
                    (document.head || document.documentElement).appendChild(st); }
                st.textContent = ${jsString(css)};
            })();
        """.trimIndent()
        view.evaluateJavascript(js, null)
    }

    /** Live-apply new settings to the open page. */
    fun updateSettings(s: AppSettings) {
        val themeChanged = currentSettings.theme != s.theme
        val captchaChanged = currentSettings.captchaEnabled != s.captchaEnabled
        currentSettings = s
        if (webView.url != null && (themeChanged || captchaChanged)) {
            applyTheme(webView)
        }
    }

    private fun readAsset(path: String): String =
        runCatching { appContext.assets.open(path).bufferedReader().use { it.readText() } }.getOrDefault("")

    private fun jsString(s: String): String =
        "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""

    fun reload() { webView.reload() }

    fun destroy() {
        scope.cancel()
        webView.destroy()
    }
}
