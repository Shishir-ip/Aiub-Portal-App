package com.shishir.aiubportal.portal

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.CookieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Saves portal downloads (notes, forms, admit cards…) into the public
 * Documents/Aiub Portal folder so they are visible in the phone's Files app
 * and listed in the app's Downloads screen.
 */
object DownloadEvents {
    /** Emitted after each completed download — the Downloads screen refreshes on it. */
    val downloaded = MutableStateFlow<Uri?>(null)
}

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

    /**
     * Downloads [url] into Documents/Aiub Portal; returns the MediaStore uri.
     * blob:/data:/content: sources are fetched inside the page (via the
     * ACDownload bridge) and arrive as raw bytes.
     */
    suspend fun download(context: Context, url: String, contentDisposition: String?, mimetype: String?): Uri =
        withContext(Dispatchers.IO) {
            if (!url.startsWith("http")) {
                throw IOException("Unsupported download source: " + url.take(60))
            }
            val name = uniqueName(context, fileNameFrom(url, contentDisposition, mimetype))

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, mimetype ?: "application/octet-stream")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" + FOLDER)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)
                ?: fallbackFile(context, name, mimetype)

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
                if (uri.toString().startsWith("content:")) resolver.delete(uri, null, null)
                throw e
            }
            uri
        }

    /** App-specific fallback (always writable): files/Aiub Portal */
    private fun fallbackFile(context: Context, name: String, mimetype: String?): Uri {
        val dir = java.io.File(context.getExternalFilesDir(null), "Aiub Portal").apply { mkdirs() }
        val f = java.io.File(dir, name)
        return androidx.core.content.FileProvider.getUriForFile(
            context, context.packageName + ".fileprovider", f
        ).also { /* the file itself is written by the caller via contentResolver */ }
    }

    private fun uniqueName(context: Context, desired: String): String {
        val resolver = context.contentResolver
        var candidate = desired
        var n = 1
        while (true) {
            val cursor = resolver.query(
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                arrayOf(MediaStore.MediaColumns._ID),
                "${MediaStore.MediaColumns.DISPLAY_NAME}=? AND ${MediaStore.MediaColumns.RELATIVE_PATH}=?",
                arrayOf(candidate, (Environment.DIRECTORY_DOCUMENTS + "/" + FOLDER + "/")),
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

    /** Writes raw bytes (from blob/data bridge) into Documents/Aiub Portal. */
    fun saveBytes(context: Context, bytes: ByteArray, name: String, mimetype: String?): Uri {
        val clean = if (name.isBlank() || name == "download") "download_${System.currentTimeMillis()}.bin" else name
        val values = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, uniqueName(context, clean))
            put(MediaStore.MediaColumns.MIME_TYPE, mimetype ?: "application/octet-stream")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" + FOLDER)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)
            ?: throw IOException("Could not create download entry")
        resolver.openOutputStream(uri)?.use { it.write(bytes) } ?: throw IOException("Could not open output stream")
        return uri
    }
}