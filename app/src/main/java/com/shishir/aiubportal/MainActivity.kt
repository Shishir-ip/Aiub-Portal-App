package com.shishir.aiubportal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shishir.aiubportal.downloads.DownloadsScreen
import com.shishir.aiubportal.exam.ExamScreen
import com.shishir.aiubportal.notices.NoticesScreen
import com.shishir.aiubportal.notices.NoticesWorker
import com.shishir.aiubportal.portal.PortalController
import com.shishir.aiubportal.portal.PortalScreen
import com.shishir.aiubportal.settings.SettingsScreen
import com.shishir.aiubportal.settings.SettingsStore
import com.shishir.aiubportal.ui.AiubTheme

class MainActivity : ComponentActivity() {

    private lateinit var controller: PortalController
    private lateinit var settingsStore: SettingsStore

    private val notifPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        controller = PortalController(this)
        settingsStore = SettingsStore(applicationContext)

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        setContent {
            val settings by settingsStore.settings.collectAsState(
                initial = com.shishir.aiubportal.settings.AppSettings()
            )
            val dark = when (settings.theme) {
                "night" -> true
                "auto" -> isSystemInDarkTheme()
                else -> false
            }
            /* theme + captcha changes apply live to the open portal page */
            LaunchedEffect(settings) { controller.updateSettings(settings) }
            AiubTheme(dark = dark) {
                AppShell(
                    controller = controller,
                    onNoticesChanged = { on, interval ->
                        if (on) NoticesWorker.schedule(this, interval)
                        else NoticesWorker.cancel(this)
                    }
                )
            }
        }
    }

    @Composable
    private fun AppShell(controller: PortalController, onNoticesChanged: (Boolean, Int) -> Unit) {
        val navController = rememberNavController()
        val tabs = listOf(
            Triple("portal", "Portal", Icons.Filled.Home),
            Triple("notices", "Notices", Icons.Filled.Notifications),
            Triple("downloads", "Downloads", Icons.Filled.CloudDownload),
            Triple("website", "AIUB", Icons.Filled.Info),
            Triple("settings", "Settings", Icons.Filled.Settings)
        )
        val backStack by navController.currentBackStackEntryAsState()
        val current = backStack?.destination?.route ?: "portal"

        Scaffold(
            bottomBar = {
                NavigationBar {
                    tabs.forEach { (route, label, icon) ->
                        NavigationBarItem(
                            selected = current == route,
                            onClick = {
                                /* re-tapping a tab always opens that section's main page */
                                if (current == route) {
                                    when (route) {
                                        "portal" -> controller.reload()
                                        else -> navController.navigate(route) { launchSingleTop = true }
                                    }
                                } else {
                                    navController.navigate(route) {
                                        popUpTo("portal") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(icon, label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "portal",
                modifier = Modifier.padding(padding)
            ) {
                composable("portal") { PortalScreen(
                    controller = controller,
                    onOpenExam = { navController.navigate("exam") }
                ) }
                composable("notices") { NoticesScreen() }
                composable("downloads") { DownloadsScreen() }
                composable("website") { WebsiteScreen(controller = controller) }
                composable("settings") {
                    SettingsScreen(store = settingsStore, onNoticesChanged = onNoticesChanged)
                }
                composable("exam") { ExamScreen(onBack = { navController.popBackStack() }) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::controller.isInitialized) controller.destroy()
    }
}
