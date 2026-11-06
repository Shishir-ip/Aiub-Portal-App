package com.shishir.aiubportal.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(store: SettingsStore, onNoticesChanged: (Boolean, Int) -> Unit) {
    val settings by store.settings.collectAsState(initial = AppSettings())
    val scope = rememberCoroutineScope()
    var user by remember(settings.username) { mutableStateOf(settings.username) }
    var pass by remember(settings.password) { mutableStateOf(settings.password) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SectionTitle("Appearance")
        val options = listOf("default", "night", "light", "auto")
        val labels = mapOf(
            "default" to "Default (original portal look)",
            "night" to "Night — dark theme",
            "light" to "Light",
            "auto" to "Auto (follow system)"
        )
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = settings.theme == option) {
                        scope.launch { store.setTheme(option) }
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = settings.theme == option, onClick = null)
                Spacer(Modifier.width(6.dp))
                Text(labels[option] ?: option, fontSize = 13.sp)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Captcha solver")
        ToggleRow("Auto-solve the login captcha (on-device OCR)", settings.captchaEnabled) {
            scope.launch { store.setCaptcha(it) }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Login details")
        ToggleRow("Save my login details", settings.saveLogin) {
            scope.launch {
                if (it) store.saveCredentials(user, pass) else store.clearCredentials()
            }
        }
        if (settings.saveLogin) {
            OutlinedTextField(
                value = user,
                onValueChange = { user = it },
                label = { Text("Student ID (e.g. 24-59248-3)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Auto re-login when session expires", fontSize = 13.sp)
                Switch(checked = settings.autoLogin, onCheckedChange = {
                    scope.launch { store.setAutoLogin(it) }
                })
            }
            OutlinedButton(onClick = { scope.launch { store.clearCredentials() } }) {
                Text("Clear saved details")
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("AIUB notices (www.aiub.edu)")
        ToggleRow("Notify me about new notices", settings.noticesEnabled) {
            scope.launch {
                store.setNotices(it)
                onNoticesChanged(it, settings.noticesIntervalMin)
            }
        }
        if (settings.noticesEnabled) {
            Text("Poll interval", fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 30, 60).forEach { minutes ->
                    Row(
                        modifier = Modifier
                            .selectable(selected = settings.noticesIntervalMin == minutes) {
                                scope.launch {
                                    store.setNoticesInterval(minutes)
                                    onNoticesChanged(settings.noticesEnabled, minutes)
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = settings.noticesIntervalMin == minutes, onClick = null)
                        Spacer(Modifier.width(4.dp))
                        Text("$minutes min", fontSize = 12.sp)
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Portal alerts")
        ToggleRow(
            "Notify when a teacher uploads notes/files (uses your login session)",
            settings.facultyAlerts
        ) {
            scope.launch { store.setFacultyAlerts(it) }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("About")
        Text(
            "AIUB Portal App v1.0.0 — an unofficial student app. " +
                "The captcha solver and all credentials stay on your device. " +
                "Not affiliated with or endorsed by AIUB.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun ToggleRow(text: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, fontSize = 13.sp, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(10.dp))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
