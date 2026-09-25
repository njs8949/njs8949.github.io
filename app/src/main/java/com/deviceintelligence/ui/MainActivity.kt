package com.deviceintelligence.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.deviceintelligence.service.SystemMonitoringService
import com.deviceintelligence.ui.navigation.AppNavHost
import com.deviceintelligence.ui.theme.DeviceIntelligenceTheme
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Remove system bars
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )

        // Transparent status bar
        window.statusBarColor = Color.TRANSPARENT

        // Transparent navigation bar
        window.navigationBarColor = Color.TRANSPARENT

        // Set appearance for status and navigation bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController?.isAppearanceLightStatusBars = false
        insetsController?.isAppearanceLightNavigationBars = false

        // Create notification channel
        createNotificationChannel()

        setContent {
            DeviceIntelligenceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "optira_channel",
            "Optira Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Optira system monitoring service"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
