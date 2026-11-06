package com.shishir.aiubportal.portal

import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun PortalScreen(
    controller: PortalController,
    onOpenGpa: () -> Unit,
    onOpenExam: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SmallTopAppBar(
            title = { Text("AIUB Portal", style = MaterialTheme.typography.titleMedium) },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            actions = {
                IconButton(onClick = onOpenGpa) {
                    Icon(Icons.Filled.BarChart, contentDescription = "GPA dashboard")
                }
                IconButton(onClick = onOpenExam) {
                    Icon(Icons.Filled.Event, contentDescription = "Exam routine")
                }
                IconButton(onClick = { controller.reload() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Reload page")
                }
            }
        )
        AndroidView(
            factory = { controller.webView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
