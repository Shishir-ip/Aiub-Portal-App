package com.shishir.aiubportal

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.shishir.aiubportal.NoticeNav

/**
 * Plain WebView of the official AIUB website. When a notification is tapped,
 * the tapped notice URL is loaded here instead (NoticeNav.pendingUrl).
 */
@Composable
fun WebsiteScreen(controller: com.shishir.aiubportal.portal.PortalController) {
    val pending by NoticeNav.pendingUrl.collectAsState()
    val webView = remember {
        android.webkit.WebView(com.shishir.aiubportal.util.AppContextHolder.get()).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = android.webkit.WebViewClient()
            loadUrl("https://www.aiub.edu/")
        }
    }
    var canGoBack by remember { mutableStateOf(false) }
    var loadedPending by remember { mutableStateOf<String?>(null) }
    BackHandler(enabled = canGoBack) { if (webView.canGoBack()) webView.goBack() }
    webView.webViewClient = object : android.webkit.WebViewClient() {
        override fun doUpdateVisitedHistory(view: android.webkit.WebView, url: String?, isReload: Boolean) {
            canGoBack = view.canGoBack()
        }
    }

    LaunchedEffect(pending) {
        val url = pending
        if (url != null && url != loadedPending) {
            webView.loadUrl(url)
            loadedPending = url
            NoticeNav.pendingUrl.value = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { webView }, modifier = Modifier.fillMaxSize())
    }
}
