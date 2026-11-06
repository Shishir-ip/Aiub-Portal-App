package com.shishir.aiubportal.downloads

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen() {
    val context = LocalContext.current
    var files by remember { mutableStateOf(DownloadsRepository.list(context)) }
    var loading by remember { mutableStateOf(true) }
    var details by remember { mutableStateOf<DownloadedFile?>(null) }
    var renaming by remember { mutableStateOf<DownloadedFile?>(null) }
    var renameText by remember { mutableStateOf("") }

    fun refresh() { files = DownloadsRepository.list(context); loading = false }
    LaunchedEffect(Unit) { refresh() }
    val downloadedEvent by com.shishir.aiubportal.portal.DownloadEvents.downloaded.collectAsState()
    LaunchedEffect(downloadedEvent) { if (downloadedEvent != null) refresh() }

    fun open(file: DownloadedFile) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            setDataAndType(file.uri, file.mimeType)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    fun share(file: DownloadedFile) {
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = file.mimeType
            putExtra(android.content.Intent.EXTRA_STREAM, file.uri)
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Share file"))
    }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Folder, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Downloads · Documents/Aiub Portal", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
        }
        Spacer(Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (files.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.Description, null, modifier = Modifier.size(44.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(10.dp))
                    Text("No downloads yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Files you download from the portal appear here",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(files, key = { it.uri.toString() }) { file ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth().clickable { details = file }
                        ) {
                            ListItem(
                                headlineContent = { Text(file.name, fontSize = 14.sp) },
                                supportingContent = { Text("${file.formattedSize} · ${file.formattedDate}", fontSize = 11.sp) },
                                leadingContent = {
                                    Icon(Icons.Filled.Description, null, tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingContent = {
                                    IconButton(onClick = { share(file) }) {
                                        Icon(Icons.Filled.Share, "Share", modifier = Modifier.size(18.dp))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // details dialog
    details?.let { file ->
        AlertDialog(
            onDismissRequest = { details = null },
            title = { Text(file.name, fontSize = 15.sp) },
            text = {
                Column {
                    Text("Size: ${file.formattedSize}", fontSize = 13.sp)
                    Text("Saved: ${file.formattedDate}", fontSize = 13.sp)
                    Text("Type: ${file.mimeType}", fontSize = 13.sp)
                    Text("Location: Documents/Aiub Portal", fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(onClick = { open(file); details = null }) { Text("Open") }
            },
            dismissButton = {
                Column {
                    FilledTonalButton(onClick = {
                        renaming = file
                        renameText = file.name
                        details = null
                    }) { Text("Rename") }
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(onClick = {
                        DownloadsRepository.delete(context, file)
                        refresh()
                        details = null
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                }
            }
        )
    }

    // rename dialog
    renaming?.let { file ->
        AlertDialog(
            onDismissRequest = { renaming = null },
            title = { Text("Rename file") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    label = { Text("File name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (renameText.isNotBlank()) {
                        DownloadsRepository.rename(context, file, renameText.trim())
                        refresh()
                    }
                    renaming = null
                }) { Text("Save") }
            },
            dismissButton = {
                OutlinedButton(onClick = { renaming = null }) { Text("Cancel") }
            }
        )
    }
}
