package com.shishir.aiubportal.portal

import android.content.Context

/**
 * Theme injection for the portal WebView. dark.css is the same stylesheet the
 * AIUB Portal Dark Mode extension uses — scoped under `html.apdm-dark`, so
 * adding that class flips the portal to the navy dark theme.
 */
object ThemeInjector {

    private var darkCss: String? = null

    fun darkCss(context: Context): String {
        if (darkCss == null) {
            darkCss = context.assets.open("dark.css").bufferedReader().use { it.readText() }
        }
        return darkCss!!
    }

    /** Injects (or removes) the dark theme style sheet on the current page. */
    fun apply(webView: android.webkit.WebView, context: Context, dark: Boolean) {
        val css = darkCss(context)
        val js = if (dark) {
            """
            (function(){
                document.documentElement.classList.add('apdm-dark');
                var s = document.getElementById('aiub-app-dark-style');
                if (!s) {
                    s = document.createElement('style');
                    s.id = 'aiub-app-dark-style';
                    document.head.appendChild(s);
                }
                s.textContent = ${jsonString(css)};
            })();
            """.trimIndent()
        } else {
            """
            (function(){
                document.documentElement.classList.remove('apdm-dark');
                var s = document.getElementById('aiub-app-dark-style');
                if (s) s.remove();
            })();
            """.trimIndent()
        }
        webView.evaluateJavascript(js, null)
    }

    fun jsonString(raw: String): String =
        "\"" + raw.replace("\\", "\\\\").replace("\"", "\\\"")
            .replace("\n", "\\n").replace("\r", "").replace("\t", "\\t") + "\""
}
