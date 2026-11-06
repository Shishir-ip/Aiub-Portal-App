package com.shishir.aiubportal

import android.app.Application
import com.shishir.aiubportal.notices.NoticeNotifications
import com.shishir.aiubportal.notices.NoticesWorker
import com.shishir.aiubportal.notices.FacultyAlertsWorker
import com.shishir.aiubportal.settings.SettingsStore
import com.shishir.aiubportal.util.AppContextHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextHolder.init(this)
        NoticeNotifications.ensureChannels(this)

        val store = SettingsStore(this)
        CoroutineScope(Dispatchers.IO).launch {
            val s = store.settings.first()
            /* restore the notice poller after process restarts */
            if (s.noticesEnabled) {
                NoticesWorker.schedule(this@MainApplication, s.noticesIntervalMin)
            } else {
                NoticesWorker.cancel(this@MainApplication)
            }
            /* the faculty poller is cheap and self-skips when logged out */
            if (s.facultyAlerts && s.saveLogin) {
                FacultyAlertsWorker.schedule(this@MainApplication)
            }
        }
    }
}
