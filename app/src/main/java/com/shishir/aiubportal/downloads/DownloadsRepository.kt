package com.shishir.aiubportal.downloads

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DownloadedFile(
    val uri: Uri,
    val name: String,
    val sizeBytes: Long,
    val dateAdded: Long,
    val mimeType: String
) {
    val formattedSize: String
        get() = when {
            sizeBytes >= 1_048_576 -> "%.1f MB".format(Locale.US, sizeBytes / 1_048_576.0)
            sizeBytes >= 1_024 -> "%.1f KB".format(Locale.US, sizeBytes / 1_024.0)
            else -> "$sizeBytes B"
        }

    val formattedDate: String
        get() = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(dateAdded * 1000))
}

object DownloadsRepository {

    val FOLDER_PATH = Environment.DIRECTORY_DOCUMENTS + "/" + "Aiub Portal"

    fun list(context: Context): List<DownloadedFile> {
        val resolver = context.contentResolver
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.MIME_TYPE
        )
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH}=?"
        val args = arrayOf("$FOLDER_PATH/")
        val sort = "${MediaStore.MediaColumns.DATE_ADDED} DESC"

        val out = ArrayList<DownloadedFile>()
        resolver.query(collection, projection, selection, args, sort)?.use { c ->
            while (c.moveToNext()) {
                val id = c.getLong(0)
                out.add(
                    DownloadedFile(
                        uri = ContentUris.withAppendedId(collection, id),
                        name = c.getString(1) ?: "file",
                        sizeBytes = c.getLong(2),
                        dateAdded = c.getLong(3),
                        mimeType = c.getString(4) ?: "application/octet-stream"
                    )
                )
            }
        }
        return out
    }

    fun rename(context: Context, file: DownloadedFile, newName: String): Boolean = try {
        val values = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, newName)
        }
        context.contentResolver.update(file.uri, values, null, null) > 0
    } catch (e: Exception) {
        false
    }

    fun delete(context: Context, file: DownloadedFile): Boolean = try {
        context.contentResolver.delete(file.uri, null, null) > 0
    } catch (e: Exception) {
        false
    }
}
