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
 * Owns the portal WebView. Loads portal.aiub.edu, applies the Day/Night theme
 * (dark.css injected inline at navigation start — no flash), solves the login
 * captcha (extension's canvas pipeline in-page + native OCR), and captures
 * downloads/uploads.
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

    private val PHONE_CSS = """
        /* AIUB Portal App — phone layout */
        html body #navigation-bar { display: none !important; }
        html body #main-content { width: 100% !important; max-width: 100% !important; float: none !important; }
        html body .portal-body { width: 100% !important; }
        html body { overflow-x: hidden !important; }
    """.trimIndent()

    private val nightCss: String by lazy {
        readAsset("dark.css") + "\n" + PHONE_CSS
    }

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
            onDownloadRequested(url, contentDisposition, mimetype)
        }

        webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url.startsWith("https://portal.aiub.edu")) {
                    applyTheme(view)
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

    /* ----------------------------------------------------------------------------
     * Captcha solving — the extension's exact canvas pipeline runs in the page
     * (where it is proven to work), and the OCR runs natively on Android.
     * ----------------------------------------------------------------------------
     */

    private val captureJs = """
        (function(){
            var img = document.getElementById('CaptchaImage');
            if (!img || !img.width) { return ''; }
            var w = img.width * 4, h = img.height * 4;
            var canvas = document.createElement('canvas');
            canvas.width = w; canvas.height = h;
            var ctx = canvas.getContext('2d', { willReadFrequently: true });
            ctx.imageSmoothingEnabled = false;
            ctx.drawImage(img, 0, 0, w, h);
            var imageData = ctx.getImageData(0, 0, w, h);
            var data = imageData.data;
            for (var i = 0; i < data.length; i += 4) {
                var intensity = (data[i] * 0.299 + data[i+1] * 0.587 + data[i+2] * 0.114);
                var v = intensity > 150 ? 255 : 0;
                data[i] = data[i+1] = data[i+2] = v; data[i+3] = 255;
            }
            var visited = new Uint8Array(w * h);
            var minArea = 50;
            for (var yy = 0; yy < h; yy++) {
                for (var xx = 0; xx < w; xx++) {
                    var idx = yy * w + xx;
                    if (data[idx * 4] === 0 && !visited[idx]) {
                        var queue = [idx]; visited[idx] = 1; var blob = [idx]; var head = 0;
                        while (head < queue.length) {
                            var curr = queue[head++];
                            var cx = curr % w, cy = Math.floor(curr / w);
                            for (var dy = -1; dy <= 1; dy++) for (var dx = -1; dx <= 1; dx++) {
                                if (dx === 0 && dy === 0) continue;
                                var nx = cx + dx, ny = cy + dy;
                                if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                                    var nIdx = ny * w + nx;
                                    if (data[nIdx * 4] === 0 && !visited[nIdx]) {
                                        visited[nIdx] = 1; queue.push(nIdx); blob.push(nIdx);
                                    }
                                }
                            }
                        }
                        if (blob.length < minArea) {
                            for (var b = 0; b < blob.length; b++) {
                                var bi = blob[b] * 4;
                                data[bi] = data[bi+1] = data[bi+2] = 255;
                            }
                        }
                    }
                }
            }
            ctx.putImageData(imageData, 0, 0);
            var temp = document.createElement('canvas');
            temp.width = w; temp.height = h;
            temp.getContext('2d').putImageData(imageData, 0, 0);
            ctx.fillStyle = '#ffffff'; ctx.fillRect(0, 0, w, h);
            ctx.filter = 'blur(2px)';
            ctx.drawImage(temp, 0, 0, w, h);
            ctx.filter = 'none';
            imageData = ctx.getImageData(0, 0, w, h);
            data = imageData.data;
            for (var j = 0; j < data.length; j += 4) {
                var v2 = data[j] > 230 ? 255 : 0;
                data[j] = data[j+1] = data[j+2] = v2; data[j+3] = 255;
            }
            ctx.putImageData(imageData, 0, 0);
            return canvas.toDataURL('image/png').split(',')[1];
        })();
    """.trimIndent()

    private fun fillAnswerJs(answer: Int): String =
        "var i=document.getElementById('CaptchaInputText');" +
            "if(i){i.value='$answer';i.dispatchEvent(new Event('input',{bubbles:true}));" +
            "i.dispatchEvent(new Event('change',{bubbles:true}));}"

    private fun startCaptchaLoop(view: WebView) {
        if (captchaSolving) return
        captchaSolving = true
        scope.launch {
            var attempts = 0
            while (attempts < 15) {
                /* 1. capture + preprocess in-page (extension pipeline) */
                val base64 = jsStringResult(view, captureJs)
                var answer: Int? = null
                if (base64.isNotBlank() && base64 != "null") {
                    /* 2. native OCR on the captured image */
                    answer = withContext(Dispatchers.Default) {
                        runCatching {
                            val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                            val bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            bmp?.let { CaptchaSolver.solve(appContext, it) }
                        }.getOrNull()
                    }
                }
                if (answer != null) {
                    /* 3. fill the answer */
                    withContext(Dispatchers.Main) {
                        view.evaluateJavascript(fillAnswerJs(answer), null)
                        android.widget.Toast.makeText(
                            appContext, "Captcha solved: $answer ✓", android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                    captchaSolving = false
                    return@launch
                }
                attempts++
                /* 4. unreadable → click the page's own refresh button */
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
        val jsonCss = org.json.JSONObject.quote(css)
        view.evaluateJavascript(
            "(function(){" +
                "var el=document.documentElement;" +
                "if($night){el.classList.add('apdm-dark');}else{el.classList.remove('apdm-dark');}" +
                "var st=document.getElementById('aiub-app-style');" +
                "if(!st){st=document.createElement('style');st.id='aiub-app-style';" +
                "(document.head||document.documentElement).appendChild(st);}" +
                "st.textContent=$jsonCss;" +
            "})();", null
        )
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

    fun reload() { webView.reload() }

    fun destroy() {
        scope.cancel()
        webView.destroy()
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
