package com.shishir.aiubportal.util

import android.content.Context

/** Global application context for places without DI. */
object AppContextHolder {
    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) { appContext = context.applicationContext }
    fun get(): Context = appContext ?: throw IllegalStateException("AppContextHolder not initialized")
}
