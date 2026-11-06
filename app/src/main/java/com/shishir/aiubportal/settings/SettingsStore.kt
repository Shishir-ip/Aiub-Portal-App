package com.shishir.aiubportal.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "aiub_settings")

data class AppSettings(
    val theme: String = "night",            // default | night | light | auto
    val captchaEnabled: Boolean = true,
    val saveLogin: Boolean = false,
    val username: String = "",
    val password: String = "",
    val autoLogin: Boolean = true,
    val noticesEnabled: Boolean = true,
    val noticesIntervalMin: Int = 30,
)

class SettingsStore(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val CAPTCHA = booleanPreferencesKey("captcha_enabled")
        val SAVE_LOGIN = booleanPreferencesKey("save_login")
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
        val AUTO_LOGIN = booleanPreferencesKey("auto_login")
        val NOTICES = booleanPreferencesKey("notices_enabled")
        val NOTICES_INTERVAL = intPreferencesKey("notices_interval_min")
        val SEEN_NOTICE_URLS = stringSetPreferencesKey("seen_notice_urls")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        AppSettings(
            theme = p[Keys.THEME] ?: "night",
            captchaEnabled = p[Keys.CAPTCHA] ?: true,
            saveLogin = p[Keys.SAVE_LOGIN] ?: false,
            username = p[Keys.USERNAME] ?: "",
            password = p[Keys.PASSWORD] ?: "",
            autoLogin = p[Keys.AUTO_LOGIN] ?: true,
            noticesEnabled = p[Keys.NOTICES] ?: true,
            noticesIntervalMin = p[Keys.NOTICES_INTERVAL] ?: 30,
        )
    }

    val seenNoticeUrls: Flow<Set<String>> = context.dataStore.data.map { p ->
        p[Keys.SEEN_NOTICE_URLS]?.toSet() ?: emptySet()
    }

    suspend fun markSeenNoticeUrls(urls: List<String>) {
        if (urls.isEmpty()) return
        context.dataStore.edit { p ->
            val old = p[Keys.SEEN_NOTICE_URLS]?.toMutableSet() ?: mutableSetOf()
            old.addAll(urls)
            /* keep only the newest 500 so the set never grows forever */
            if (old.size > 500) p[Keys.SEEN_NOTICE_URLS] = old.toList().takeLast(500).toSet()
            else p[Keys.SEEN_NOTICE_URLS] = old
        }
    }

    suspend fun current(): AppSettings = settings.first()

    suspend fun setTheme(value: String) = context.dataStore.edit { it[Keys.THEME] = value }
    suspend fun setCaptcha(on: Boolean) = context.dataStore.edit { it[Keys.CAPTCHA] = on }
    suspend fun setAutoLogin(on: Boolean) = context.dataStore.edit { it[Keys.AUTO_LOGIN] = on }
    suspend fun setSaveLogin(on: Boolean) = context.dataStore.edit { it[Keys.SAVE_LOGIN] = on }
    suspend fun setNotices(on: Boolean) = context.dataStore.edit { it[Keys.NOTICES] = on }
    suspend fun setNoticesInterval(min: Int) = context.dataStore.edit { it[Keys.NOTICES_INTERVAL] = min }

    suspend fun saveCredentials(username: String, password: String) =
        context.dataStore.edit {
            it[Keys.SAVE_LOGIN] = username.isNotBlank()
            it[Keys.USERNAME] = username
            it[Keys.PASSWORD] = password
        }

    suspend fun clearCredentials() = context.dataStore.edit {
        it[Keys.SAVE_LOGIN] = false
        it[Keys.USERNAME] = ""
        it[Keys.PASSWORD] = ""
    }
}
