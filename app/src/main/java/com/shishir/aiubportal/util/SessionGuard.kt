package com.shishir.aiubportal.util

import java.net.URL

/** Detects the portal's login page from a fetched response. */
object SessionGuard {
    fun isLoginPage(url: String, body: String): Boolean {
        val path = runCatching { URL(url).path }.getOrDefault(url)
        if (path == "/" || path.startsWith("/Login") || path.startsWith("/Account/Login")) return true
        return body.contains("Sign in with your organizational")
    }
}
