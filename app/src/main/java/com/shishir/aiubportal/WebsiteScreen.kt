package com.shishir.aiubportal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/** Plain WebView of the official AIUB website (no theming, as requested). */
@Composable
fun WebsiteScreen() {
    val webView = remember {
        android.webkit.WebView(com.shishir.aiubportal.util.AppContextHolder.get()).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = android.webkit.WebViewClient()
            loadUrl("https://www.aiub.edu/")
        }
    }
    var canGoBack by remember { mutableStateOf(false) }
    BackHandler(enabled = canGoBack) { if (webView.canGoBack()) webView.goBack() }
    webView.webViewClient = object : android.webkit.WebViewClient() {
        override fun doUpdateVisitedHistory(view: android.webkit.WebView, url: String?, isReload: Boolean) {
            canGoBack = view.canGoBack()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { webView }, modifier = Modifier.fillMaxSize())
    }
}
