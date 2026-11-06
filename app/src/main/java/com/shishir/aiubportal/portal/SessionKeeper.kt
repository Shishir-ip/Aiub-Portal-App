package com.shishir.aiubportal.portal

import android.content.Context
import android.graphics.BitmapFactory
import android.webkit.CookieManager
import android.webkit.WebView
import com.shishir.aiubportal.settings.AppSettings
import com.shishir.aiubportal.settings.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Detects the portal's login page (including the silent redirects that happen
 * when the server session expires) and automatically logs back in with the
 * saved credentials: fills the fields, solves the captcha on-device, submits
 * the form, and reloads the page the user was on.
 */
class SessionKeeper(private val context: Context) {

    private var attempts = 0
    private var busy = false
    private val settings = SettingsStore(context)

    companion object {
        private const val MAX_ATTEMPTS = 4
        private const val MAX_CAPTCHA_TRIES = 10

        fun isLoginPage(url: String): Boolean {
            val path = runCatching { java.net.URL(url).path }.getOrDefault(url)
            return path == "/" || path.isBlank() ||
                path.startsWith("/Login") || path.startsWith("/Account/Login")
        }
    }

    /** Called from WebViewClient.onPageFinished. */
    fun onPageFinished(webView: WebView, url: String, settings: AppSettings, captchaOn: Boolean) {
        if (!SessionKeeper.isLoginPage(url)) {
            attempts = 0
            return
        }
        if (busy || !settings.autoLogin || !settings.saveLogin) return
        if (settings.username.isBlank() || settings.password.isBlank()) return
        if (attempts >= MAX_ATTEMPTS) return

        busy = true
        attempts++
        autoLogin(webView, settings.username, settings.password, captchaOn) { busy = false }
    }

    private fun autoLogin(webView: WebView, user: String, pass: String, captchaOn: Boolean, done: () -> Unit) {
        val js = """
            (function(){
                var u = document.getElementById('username');
                var p = document.getElementById('password');
                var img = document.getElementById('CaptchaImage');
                return JSON.stringify({
                    hasForm: !!u && !!p,
                    user: u ? u.value : '',
                    captchaSrc: img ? img.src : ''
                });
            })();
        """.trimIndent()
        webView.evaluateJavascript(js) { result ->
            val json = result.removeSurrounding("\"")
                .replace("\\\"", "\"").replace("\\\\", "\\")
            if (!json.contains("\"hasForm\":true")) { done(); return@evaluateJavascript }

            val src = Regex("\"captchaSrc\":\"([^\"]*)\"").find(json)?.groupValues?.get(1) ?: ""

            fun continueWithCaptcha() {
                webView.post {
                    // fill credentials
                    webView.evaluateJavascript(fillJs(user, pass), null)
                    webView.postDelayed({
                        if (!captchaOn) {
                            submit(webView)
                            done()
                        } else {
                            // let the solver fill the answer, then submit
                            solveAndSubmit(webView, src, done)
                        }
                    }, 250)
                }
            }

            // If the captcha image was not reloaded yet, force the refresh button
            if (src.isBlank()) { done(); return@evaluateJavascript }
            continueWithCaptcha()
        }
    }

    private fun fillJs(user: String, pass: String): String {
        val u = ThemeInjector.jsonString(user)
        val p = ThemeInjector.jsonString(pass)
        return """
            (function(){
                var u = document.getElementById('username');
                var p = document.getElementById('password');
                if (u) { u.value = $u; u.dispatchEvent(new Event('input', {bubbles:true})); u.dispatchEvent(new Event('blur', {bubbles:true})); }
                if (p) { p.value = $p; p.dispatchEvent(new Event('input', {bubbles:true})); }
            })();
        """.trimIndent()
    }

    private fun submit(webView: WebView) {
        webView.evaluateJavascript(
            "(function(){ var f = document.getElementById('loginForm'); if (f) f.submit(); })();", null
        )
    }

    private fun solveAndSubmit(webView: WebView, captchaUrl: String, done: () -> Unit) {
        webView.post {
            CoroutineScope(Dispatchers.Main).launch {
                var filled: Int? = null
                for (tryIdx in 1..MAX_CAPTCHA_TRIES) {
                    val answer = withContext(Dispatchers.IO) {
                        runCatching { fetchAndSolve(captchaUrl) }.getOrNull()
                    }
                    if (answer != null) { filled = answer; break }

                    // unreadable → click the refresh button, wait for the new image
                    webView.evaluateJavascript(
                        "(function(){var b=document.querySelector('#captcha a.btn, a.btn-sm.btn-primary'); if(b) b.click();})();", null
                    )
                    kotlinx.coroutines.delay(1200)
                }
                if (filled != null) {
                    webView.evaluateJavascript(
                        "(function(){var i=document.getElementById('CaptchaInputText'); if(i){i.value='" + filled + "'; i.dispatchEvent(new Event('input',{bubbles:true}));}})();", null
                    )
                    webView.postDelayed({ submit(webView); done() }, 600)
                } else {
                    done() // give up silently; user logs in manually
                }
            }
        }
    }

    private fun fetchAndSolve(captchaUrl: String): Int? {
        val conn = URL(captchaUrl).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = 15000
            conn.readTimeout = 15000
            CookieManager.getInstance().getCookie(captchaUrl)?.let { conn.setRequestProperty("Cookie", it) }
            val bytes = ByteArrayOutputStream().use { out ->
                conn.inputStream.copyTo(out)
                out.toByteArray()
            }
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
            return CaptchaSolver.solve(context, bmp)
        } finally {
            conn.disconnect()
        }
    }
}
