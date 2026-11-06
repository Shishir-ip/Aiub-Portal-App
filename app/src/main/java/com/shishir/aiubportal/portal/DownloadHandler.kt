package com.shishir.aiubportal.portal

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.CookieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Saves portal downloads (notes, forms, admit cards…) into the public
 * Documents/Aiub Portal folder so they are visible in the phone's Files app
 * and listed in the app's Downloads screen.
 */
object DownloadHandler {

    const val FOLDER = "Aiub Portal"

    fun fileNameFrom(url: String, contentDisposition: String?, mimetype: String?): String {
        contentDisposition?.let { cd ->
            Regex("filename\\*?=\"?([^\";]+)\"?", RegexOption.IGNORE_CASE).find(cd)?.let {
                return it.groupValues[1].trim()
            }
        }
        val last = url.substringBefore('?').substringAfterLast('/')
        return if (last.isNotBlank() && '.' in last) last
        else "download_${System.currentTimeMillis()}.${guessExtension(mimetype)}"
    }

    private fun guessExtension(mimetype: String?): String = when {
        mimetype?.contains("pdf") == true -> "pdf"
        mimetype?.contains("word") == true -> "docx"
        mimetype?.contains("sheet") == true -> "xlsx"
        mimetype?.contains("presentation") == true -> "pptx"
        mimetype?.contains("image") == true -> "jpg"
        mimetype?.contains("zip") == true -> "zip"
        else -> "bin"
    }

    /** Downloads [url] into Documents/Aiub Portal; returns the MediaStore uri. */
    suspend fun download(context: Context, url: String, contentDisposition: String?, mimetype: String?): Uri =
        withContext(Dispatchers.IO) {
            val name = uniqueName(context, fileNameFrom(url, contentDisposition, mimetype))

            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, name)
                put(MediaStore.Downloads.MIME_TYPE, mimetype ?: "application/octet-stream")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" + FOLDER)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Could not create download entry")

            try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.connectTimeout = 20000
                conn.readTimeout = 60000
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android) AiubPortalApp/1.0")
                CookieManager.getInstance().getCookie(url)?.let { conn.setRequestProperty("Cookie", it) }
                conn.instanceFollowRedirects = true
                conn.inputStream.use { input ->
                    resolver.openOutputStream(uri)?.use { out -> input.copyTo(out) }
                        ?: throw IOException("Could not open output stream")
                }
            } catch (e: Exception) {
                resolver.delete(uri, null, null)
                throw e
            }
            uri
        }

    private fun uniqueName(context: Context, desired: String): String {
        val resolver = context.contentResolver
        var candidate = desired
        var n = 1
        while (true) {
            val cursor = resolver.query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Downloads._ID),
                "${MediaStore.Downloads.DISPLAY_NAME}=? AND ${MediaStore.Downloads.RELATIVE_PATH}=?",
                arrayOf(candidate, Environment.DIRECTORY_DOCUMENTS + "/" + FOLDER),
                null
            )
            val exists = cursor?.use { it.count > 0 } ?: false
            cursor?.close()
            if (!exists) return candidate
            val dot = desired.lastIndexOf('.')
            candidate = if (dot > 0) {
                desired.substring(0, dot) + " ($n)" + desired.substring(dot)
            } else {
                "$desired ($n)"
            }
            n++
        }
    }
}
