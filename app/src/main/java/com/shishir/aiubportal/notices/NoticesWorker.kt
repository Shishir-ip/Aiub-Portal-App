package com.shishir.aiubportal.notices

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkerParameters
import com.shishir.aiubportal.MainActivity
import com.shishir.aiubportal.R
import com.shishir.aiubportal.settings.SettingsStore
import kotlinx.coroutines.flow.first

object NoticeNotifications {
    const val CHANNEL_ID = "aiub_notices"
    const val FACULTY_CHANNEL_ID = "aiub_faculty_alerts"

    fun ensureChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "AIUB Notices", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "New notices posted on www.aiub.edu"
            }
        )
        nm.createNotificationChannel(
            NotificationChannel(FACULTY_CHANNEL_ID, "Portal alerts", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Faculty uploads and other portal notifications"
            }
        )
    }

    fun notify(context: Context, channelId: String, id: Int, title: String, text: String, url: String?) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT >= 33
        ) return

        val intent = Intent(context, MainActivity::class.java).apply {
            data = Uri.parse(url ?: "https://www.aiub.edu/category/notices")
        }
        val pending = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_app_foreground)
            .setContentTitle(title.take(80))
            .setContentText(text.take(150))
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
        NotificationManagerCompat.from(context).notify(id, notification)
    }
}

/**
 * Periodic poller for new notices on www.aiub.edu. New notices (URLs never
 * seen before) are posted as phone notifications and marked seen.
 */
class NoticesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val settings = SettingsStore(applicationContext).settings.first()
        if (!settings.noticesEnabled) return Result.success()

        NoticeNotifications.ensureChannels(applicationContext)

        val seen = SettingsStore(applicationContext).seenNoticeUrls.first()
        val notices = runCatching { NoticesRepository.fetch() }.getOrDefault(emptyList())
        val fresh = notices.filter { it.url !in seen }.take(5)

        fresh.forEachIndexed { index, n ->
            NoticeNotifications.notify(
                applicationContext, NoticeNotifications.CHANNEL_ID,
                (n.url.hashCode() and 0x7fffffff),
                n.title, "${n.date} · ${n.desc}", n.url
            )
            // small stagger so the group reads well
            if (fresh.size > 1) kotlinx.coroutines.delay(400 * (index + 1))
        }

        SettingsStore(applicationContext).markSeenNoticeUrls(notices.map { it.url })
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "aiub_notices_poll"

        fun schedule(context: Context, intervalMinutes: Int) {
            val request = androidx.work.PeriodicWorkRequestBuilder<NoticesWorker>(
                java.time.Duration.ofMinutes(intervalMinutes.toLong().coerceAtLeast(15))
            ).build()
            androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request
            )
        }

        fun cancel(context: Context) {
            androidx.work.WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
