package com.shishir.aiubportal.gpa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaScreen(onBack: () -> Unit) {
    var report by remember { mutableStateOf<GpaReport?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try { report = GpaRepository.fetch() }
        catch (e: NotLoggedInException) { error = "Portal session expired — open the Portal tab, log in, then come back." }
        catch (e: Exception) { error = "Could not load grade report: ${e.message}" }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("GPA Dashboard", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            when {
                report == null && error == null -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                error != null -> Text(
                    error!!, color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> report?.let { rep ->
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text("CGPA", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Text(rep.cgpa, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${rep.semesters.sumOf { it.rows.size }} courses completed",
                                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                        item {
                            Text(
                                "Average grade points per semester",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.height(8.dp))
                            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    rep.semesters.forEach { sem ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                sem.name.takeLast(11),
                                                fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.width(78.dp)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(10.dp)
                                                    .clip(RoundedCornerShape(5.dp))
                                                    .background(MaterialTheme.colorScheme.surface)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth((sem.averagePoints / 4.0).toFloat())
                                                        .height(10.dp)
                                                        .clip(RoundedCornerShape(5.dp))
                                                        .background(Color(0xFF38c1f2))
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                "%.2f".format(sem.averagePoints),
                                                fontSize = 11.sp, fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        rep.semesters.forEach { sem ->
                            item {
                                Text(
                                    sem.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(sem.rows) { row ->
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
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(row.code, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                            Text(row.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        }
                                        Text(
                                            row.grade,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = gradeColor(row.grade)
                                        )
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

private fun gradeColor(grade: String): Color = when (grade.trim().uppercase()) {
    "A+", "A", "A-" -> Color(0xFF4cd49a)
    "B+", "B", "B-" -> Color(0xFF8cc7ff)
    "C+", "C", "C-" -> Color(0xFFe5c558)
    "D+", "D" -> Color(0xFFedc258)
    "F" -> Color(0xFFf28b8b)
    else -> Color(0xFF8b9aad)
}
