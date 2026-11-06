package com.shishir.aiubportal.notices

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkerParameters
import com.shishir.aiubportal.settings.SettingsStore
import com.shishir.aiubportal.util.Http
import kotlinx.coroutines.flow.first
import org.jsoup.Jsoup
import java.security.MessageDigest

/**
 * Polls the portal's own notification center (faculty "new notes uploaded"
 * alerts and similar) using the WebView session cookies. Runs only while the
 * user is logged in — when the portal session expires this worker quietly
 * skips until the next successful app login.
 *
 * NOTE: the endpoint below was reverse-engineered from the portal's pages.
 * If AIUB changes their routes, adjust NOTIFICATIONS_URL.
 */
class FacultyAlertsWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val store = SettingsStore(applicationContext)
        val settings = store.settings.first()
        if (!settings.facultyAlerts || !settings.saveLogin) return Result.success()

        val result = runCatching {
            Http.get(FacultyAlerts.NOTIFICATIONS_URL)
        }.getOrNull() ?: return Result.success()

        /* A session redirect lands on the login page — skip silently. */
        if (SessionFingerprint.isLoginPage(result.finalUrl, result.body)) return Result.success()

        val doc = Jsoup.parse(result.body, "https://portal.aiub.edu")
        val items = doc.select("div[ng-repeat], .notification, .alert").mapNotNull { el ->
            val text = el.text().trim()
            if (text.length in 12..220 && (
                text.contains("uploaded", true) || text.contains("notice", true) ||
                    text.contains("posted", true) || text.contains("result", true)
                )
            ) text to el
            else null
        }

        val seen = store.seenFacultyHashes.first()
        val fresh = items.filter { (text, _) ->
            val hash = FacultyAlerts.hash(text)
            hash !in seen
        }.take(3)

        fresh.forEach { (text, el) ->
            val link = el.closest("a[href]")?.attr("abs:href")
            NoticeNotifications.notify(
                applicationContext, NoticeNotifications.FACULTY_CHANNEL_ID,
                FacultyAlerts.hash(text).and(0x7fffffff),
                "Portal update", text, link
            )
        }

        store.markSeenFacultyHashes(items.map { FacultyAlerts.hash(it.first) })
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "aiub_faculty_alerts_poll"

        fun schedule(context: Context) {
            val request = androidx.work.PeriodicWorkRequestBuilder<FacultyAlertsWorker>(
                java.time.Duration.ofMinutes(30)
            ).build()
            androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request
            )
        }
    }
}

object FacultyAlerts {
    const val NOTIFICATIONS_URL = "https://portal.aiub.edu/Student/Notifications"

    fun hash(text: String): String =
        MessageDigest.getInstance("MD5").digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
}

object SessionFingerprint {
    fun isLoginPage(url: String, body: String): Boolean {
        val path = runCatching { java.net.URL(url).path }.getOrDefault(url)
        if (path == "/" || path.startsWith("/Login") || path.startsWith("/Account/Login")) return true
        return body.contains("Sign in with your organizational")
    }
}
