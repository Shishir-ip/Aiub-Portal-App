package com.shishir.aiubportal.portal

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.shishir.aiubportal.settings.AppSettings
import com.shishir.aiubportal.settings.SettingsStore

/**
 * Owns the portal WebView: applies the selected theme on every page, runs the
 * session keeper (auto re-login), hands downloads to DownloadHandler and keeps
 * the WebView alive while the user switches app tabs.
 */
class PortalController(context: Context) {

    private val appContext = context.applicationContext
    val settings = SettingsStore(appContext)
    val sessionKeeper = SessionKeeper(appContext)

    var currentSettings: AppSettings = AppSettings()
        private set

    @SuppressLint("SetJavaScriptEnabled")
    val webView: WebView = WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.userAgentString = settings.userAgentString
            .replace("; wv", "") /* look like a normal browser */
        android.webkit.CookieManager.getInstance().setAcceptCookie(true)

        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val s = currentSettings
                val dark = when (s.theme) {
                    "night" -> true
                    "light", "default" -> false
                    else -> /* auto */ resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                        android.content.res.Configuration.UI_MODE_NIGHT_YES
                }
                ThemeInjector.apply(view, appContext, dark)
                sessionKeeper.onPageFinished(view, url, s, s.captchaEnabled)
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                /* Keep external AIUB site links inside the in-app website tab? No —
                   leave them to the WebView; aiub.edu renders fine in it. */
                return false
            }
        }
    }

    fun updateSettings(s: AppSettings) {
        val themeChanged = currentSettings.theme != s.theme
        currentSettings = s
        if (themeChanged && webView.url != null) {
            val dark = when (s.theme) {
                "night" -> true
                "light", "default" -> false
                else -> webView.resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
            ThemeInjector.apply(webView, appContext, dark)
        }
        /* cookie persistence */
        android.webkit.CookieManager.getInstance().flush()
    }

    fun reload() = webView.reload()

    fun destroy() {
        webView.destroy()
    }
}
