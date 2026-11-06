package com.shishir.aiubportal.portal

import android.content.Context
import android.webkit.WebView
import com.shishir.aiubportal.settings.AppSettings
import com.shishir.aiubportal.settings.SettingsStore

/**
 * Detects the portal's login page (including the silent redirects that happen
 * when the server session expires) and automatically logs back in with the
 * saved credentials. The captcha itself is solved by the bundled AIUB+
 * captcha solver running inside the WebView — this class fills the
 * credentials, waits for the solved answer to appear, then submits the form.
 */
class SessionKeeper(private val context: Context) {

    private var attempts = 0
    private var busy = false

    companion object {
        private const val MAX_ATTEMPTS = 3
        private const val CAPTCHA_WAIT_MS = 45000
        private const val POLL_MS = 600L

        fun isLoginPage(url: String): Boolean {
            val path = runCatching { java.net.URL(url).path }.getOrDefault(url)
            return path == "/" || path.isBlank() ||
                path.startsWith("/Login") || path.startsWith("/Account/Login")
        }
    }

    /** Called from WebViewClient.onPageFinished. */
    fun onPageFinished(webView: WebView, url: String, settings: AppSettings, captchaOn: Boolean) {
        if (!isLoginPage(url)) {
            attempts = 0
            return
        }
        if (busy || !settings.autoLogin || !settings.saveLogin) return
        if (settings.username.isBlank() || settings.password.isBlank()) return
        if (attempts >= MAX_ATTEMPTS) return

        busy = true
        attempts++
        autoLogin(webView, settings.username, settings.password) { busy = false }
    }

    private fun autoLogin(webView: WebView, user: String, pass: String, done: () -> Unit) {
        val probe = """
            (function(){
                var u = document.getElementById('username');
                var p = document.getElementById('password');
                var img = document.getElementById('CaptchaImage');
                return JSON.stringify({ hasForm: !!u && !!p, hasCaptcha: !!img });
            })();
        """.trimIndent()

        webView.evaluateJavascript(probe) { result ->
            val json = result.removeSurrounding("\"")
            if (!json.contains("\"hasForm\":true")) { done(); return@evaluateJavascript }

            webView.post {
                /* 1. fill credentials */
                val fill = "var u=document.getElementById('username');var p=document.getElementById('password');" +
                    "var uv=" + jsonStr(user) + ";var pv=" + jsonStr(pass) + ";" +
                    "if(u){u.value=uv;u.dispatchEvent(new Event('input',{bubbles:true}));u.dispatchEvent(new Event('blur',{bubbles:true}));}" +
                    "if(p){p.value=pv;p.dispatchEvent(new Event('input',{bubbles:true}));}"
                webView.evaluateJavascript(fill, null)

                /* 2. wait for the in-page captcha solver to fill the answer,
                      then 3. submit the form */
                waitForCaptchaAndSubmit(webView, 0, done)
            }
        }
    }

    private fun waitForCaptchaAndSubmit(webView: WebView, waitedMs: Long, done: () -> Unit) {
        webView.evaluateJavascript(
            "(function(){var i=document.getElementById('CaptchaInputText');return i?i.value:'';})()"
        ) { value ->
            val answer = value?.removeSurrounding("\"") ?: ""
            if (answer.isNotBlank()) {
                /* the in-page solver filled it — submit */
                webView.evaluateJavascript(
                    "(function(){var f=document.getElementById('loginForm');if(f)f.submit();})();", null
                )
                done()
            } else if (waitedMs >= CAPTCHA_WAIT_MS) {
                /* solver didn't produce an answer in time — let the user do it */
                done()
            } else {
                webView.postDelayed({ waitForCaptchaAndSubmit(webView, waitedMs + POLL_MS, done) }, POLL_MS)
            }
        }
    }

    private fun jsonStr(s: String): String = "\"" +
        s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\n") + "\""
}
