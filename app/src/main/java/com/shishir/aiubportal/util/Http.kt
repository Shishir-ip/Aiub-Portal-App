package com.shishir.aiubportal.util

import android.webkit.CookieManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class HttpResult(val body: String, val finalUrl: String, val code: Int)

/**
 * Cookie-aware HTTP GET used by the native screens (notices, exam routine).
 * Follows redirects manually, re-sending the (possibly refreshed) WebView
 * cookies on every hop — HttpURLConnection's automatic following caused
 * redirect loops because cookies set mid-chain were not re-attached.
 */
object Http {

    private const val UA = "Mozilla/5.0 (Linux; Android) AiubPortalApp/1.0"
    private const val MAX_HOPS = 8

    fun get(url: String, timeoutMs: Int = 20000): HttpResult {
        var current = url

        repeat(MAX_HOPS) {
            val conn = URL(current).openConnection() as HttpURLConnection
            try {
                conn.connectTimeout = timeoutMs
                conn.readTimeout = timeoutMs
                conn.instanceFollowRedirects = false
                conn.setRequestProperty("User-Agent", UA)
                CookieManager.getInstance().getCookie(current)?.let {
                    conn.setRequestProperty("Cookie", it)
                }

                val code = conn.responseCode
                val finalUrl = conn.url.toString()

                if (code in 300..399) {
                    val location = conn.getHeaderField("Location")
                        ?: throw java.io.IOException("Redirect (${code}) without Location header")
                    conn.disconnect()
                    current = resolve(current, location)
                    return@repeat
                }

                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val body = if (stream != null) {
                    BufferedReader(InputStreamReader(stream)).use { it.readText() }
                } else ""
                return HttpResult(body, finalUrl, code)
            } finally {
                conn.disconnect()
            }
        }
        throw java.io.IOException("Too many redirects while loading $url")
    }

    private fun resolve(base: String, location: String): String =
        java.net.URI(base).resolve(location).toString()

    /** URL-encoded form of a cookie header for safe logging. */
    fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}
