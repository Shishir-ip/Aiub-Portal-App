package com.shishir.aiubportal.notices

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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NoticesScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var notices by remember { mutableStateOf<List<Notice>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun loadNotices() {
        loading = true
        val (list, err) = withContext(Dispatchers.IO) {
            runCatching { NoticesRepository.fetch() }
                .map { it to (if (it.isEmpty()) "No notices found — check your connection" else null) }
                .getOrElse { emptyList<Notice>() to "Failed to load: ${it.message}" }
        }
        notices = list
        error = err
        loading = false
    }

    LaunchedEffect(Unit) { loadNotices() }

    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (notices.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(error ?: "No notices", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Retry",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { scope.launch { loadNotices() } }
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            Icons.Filled.NotificationsActive, null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Latest notices — www.aiub.edu", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                items(notices, key = { it.url }) { notice ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                context.startActivity(
                                    android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse(notice.url)
                                    )
                                )
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(notice.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Icon(
                                    Icons.Filled.OpenInNew, null,
                                    tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(notice.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (notice.desc.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(notice.desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}
