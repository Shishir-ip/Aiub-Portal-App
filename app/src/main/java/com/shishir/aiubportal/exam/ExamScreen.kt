package com.shishir.aiubportal.exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(onBack: () -> Unit) {
    var report by remember { mutableStateOf<ExamReport?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1000)
        }
    }
    LaunchedEffect(Unit) {
        try { report = withContext(Dispatchers.IO) { ExamRepository.fetch() } }
        catch (e: NotLoggedInException) { error = "Portal session expired — open the Portal tab, log in, then come back." }
        catch (e: Exception) { error = "Could not load exam schedule: ${e.message}" }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Exam Routine", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            when {
                report == null && error == null -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                error != null -> Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                else -> report?.let { rep ->
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val next = rep.sections.flatMap { it.second }
                            .filter { it.dateTime != null && it.dateTime.isAfter(now) }
                            .minByOrNull { it.dateTime!! }
                        next?.let { entry ->
                            item {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Next exam in", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        CountdownText(entry.dateTime!!, now)
                                        Spacer(Modifier.height(6.dp))
                                        Text(entry.rowText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                }
                            }
                        }
                        rep.sections.forEach { (header, rows) ->
                            item {
                                Text(header, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                            }
                            items(rows) { entry ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(entry.rowText, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        if (entry.dateTime != null && entry.dateTime.isAfter(now)) {
                                            Text(
                                                "in ${Duration.between(now, entry.dateTime).toDays()}d",
                                                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item { Spacer(Modifier.height(20.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun CountdownText(target: LocalDateTime, now: LocalDateTime) {
    val d = Duration.between(now, target).coerceAtLeast(Duration.ZERO)
    val days = d.toDays()
    val hours = d.toHours() % 24
    val mins = d.toMinutes() % 60
    Text(
        "$days d $hours h $mins m",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold
    )
}
