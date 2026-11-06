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

/**
 * Plain WebView of the official AIUB website — with the same powers as the
 * Portal tab: download capture, system file picker, window.open support.
 */
@Composable
fun WebsiteScreen(controller: com.shishir.aiubportal.portal.PortalController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val webView = remember {
        android.webkit.WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
            setBackgroundColor(android.graphics.Color.parseColor("#0e131b"))

            setDownloadListener { url, _, contentDisposition, mimetype, _ ->
                controller.onDownloadRequested(url, contentDisposition, mimetype)
            }

            webChromeClient = object : android.webkit.WebChromeClient() {
                override fun onCreateWindow(
                    view: android.webkit.WebView,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: android.os.Message
                ): Boolean {
                    val temp = android.webkit.WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = object : android.webkit.WebViewClient() {
                            override fun onPageFinished(v: android.webkit.WebView, url: String) {
                                view.loadUrl(url)
                                v.destroy()
                            }

                            override fun shouldOverrideUrlLoading(
                                v: android.webkit.WebView,
                                request: android.webkit.WebResourceRequest
                            ): Boolean {
                                view.loadUrl(request.url.toString())
                                v.destroy()
                                return true
                            }
                        }
                    }
                    (resultMsg.obj as android.webkit.WebView.WebViewTransport).webView = temp
                    resultMsg.sendToTarget()
                    return true
                }

                override fun onShowFileChooser(
                    webView: android.webkit.WebView?,
                    callback: android.webkit.ValueCallback<Array<android.net.Uri>>,
                    params: android.webkit.WebChromeClient.FileChooserParams
                ): Boolean {
                    val launcher = controller.fileChooserLauncher ?: return false
                    controller.setPendingFileChooser(callback)
                    launcher.launch(params.createIntent())
                    return true
                }
            }

            webViewClient = object : android.webkit.WebViewClient() {
                override fun doUpdateVisitedHistory(
                    view: android.webkit.WebView,
                    url: String?,
                    isReload: Boolean
                ) {
                    /* back handling is done via BackHandler below */
                }
            }

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
