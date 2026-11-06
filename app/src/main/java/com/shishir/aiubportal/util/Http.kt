package com.shishir.aiubportal.util

import android.webkit.CookieManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class HttpResult(val body: String, val finalUrl: String, val code: Int)

/**
 * Cookie-aware HTTP GET used by the native screens (notices, GPA, exam,
 * faculty alerts). Cookies come from the WebView cookie jar so authenticated
 * portal pages work with the app's saved session.
 */
object Http {

    fun get(url: String, timeoutMs: Int = 20000): HttpResult {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.connectTimeout = timeoutMs
            conn.readTimeout = timeoutMs
            conn.instanceFollowRedirects = true
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android) AiubPortalApp/1.0")
            CookieManager.getInstance().getCookie(url)?.let { conn.setRequestProperty("Cookie", it) }

            val code = conn.responseCode
            val finalUrl = conn.url.toString()
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val body = BufferedReader(InputStreamReader(stream ?: return HttpResult("", finalUrl, code))).use { r ->
                r.readText()
            }
            return HttpResult(body, finalUrl, code)
        } finally {
            conn.disconnect()
        }
    }

    /** Cookies string for a URL — used by the download handler. */
    fun cookiesFor(url: String): String? = CookieManager.getInstance().getCookie(url)
}
