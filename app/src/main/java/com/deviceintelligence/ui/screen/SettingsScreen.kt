package com.deviceintelligence.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Language
import com.deviceintelligence.utils.currentLanguage
import com.deviceintelligence.utils.getCurrentStrings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val strings = getCurrentStrings()
    val showLanguageDialog = remember { mutableStateOf(false) }

    // Settings states
    val notificationsEnabled = remember { mutableStateOf(true) }
    val darkModeEnabled = remember { mutableStateOf(false) }
    val storageOptimization = remember { mutableStateOf(true) }
    val securityChecks = remember { mutableStateOf(true) }
    val backgroundMonitoring = remember { mutableStateOf(true) }
    val threatAlerts = remember { mutableStateOf(true) }
    val permissionMonitoring = remember { mutableStateOf(true) }
    val cloudSync = remember { mutableStateOf(true) }
    val autoOptimization = remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.settings) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Notifications
            SettingCard(
                icon = Icons.Default.Notifications,
                title = strings.notifications,
                description = strings.enableSystemNotifications,
                isToggled = notificationsEnabled.value,
                onToggle = {
                    notificationsEnabled.value = it
                    Toast.makeText(
                        context,
                        if (it) strings.notificationsEnabled else strings.notificationsDisabled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            // Dark Mode (시스템 설정 사용)
            SettingCard(
                icon = Icons.Default.Brightness4,
                title = strings.darkMode,
                description = strings.followSystemSettings,
                isToggled = darkModeEnabled.value,
                onToggle = {
                    darkModeEnabled.value = it
                    Toast.makeText(
                        context,
                        strings.darkModeEnabled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            // Storage Optimization
            SettingCard(
                icon = Icons.Default.Storage,
                title = strings.storageOptimization,
                description = strings.autoCleanCache,
                isToggled = storageOptimization.value,
                onToggle = {
                    storageOptimization.value = it
                    Toast.makeText(
                        context,
                        if (it) strings.storageOptimizationEnabled else strings.storageOptimizationDisabled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            // Security
            SettingCard(
                icon = Icons.Default.Security,
                title = strings.securityChecks,
                description = strings.regularSecurityScanning,
                isToggled = securityChecks.value,
                onToggle = {
                    securityChecks.value = it
                    Toast.makeText(
                        context,
                        if (it) strings.securityChecksEnabled else strings.securityChecksDisabled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Language Selection
            SettingCard(
                icon = Icons.Default.Language,
                title = strings.language,
                description = strings.selectLanguage,
                isToggled = false,
                onToggle = {
                    showLanguageDialog.value = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Monitoring Section
            Text(
                text = strings.monitoring,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Background Monitoring
            SettingCard(
                icon = Icons.Default.Speed,
                title = strings.backgroundMonitoring,
                description = strings.realtimeSystemMonitoring,
                isToggled = backgroundMonitoring.value,
                onToggle = {
                    backgroundMonitoring.value = it
                    Toast.makeText(
                        context,
                        if (it) strings.backgroundMonitoringEnabled else strings.backgroundMonitoringDisabled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Security & AI Section
            Text(
                text = strings.securityAi,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Threat Alerts
            SettingCard(
                icon = Icons.Default.Notifications,
                title = strings.threatAlerts,
                description = strings.alertOnDetectedThreats,
                isToggled = threatAlerts.value,
                onToggle = {
                    threatAlerts.value = it
                    Toast.makeText(
                        context,
                        if (it) strings.threatAlertsEnabled else strings.threatAlertsDisabled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            // Permission Monitoring
            SettingCard(
                icon = Icons.Default.Security,
                title = strings.permissionMonitoring,
                description = strings.monitorAppPermissions,
                isToggled = permissionMonitoring.value,
                onToggle = {
                    permissionMonitoring.value = it
                    Toast.makeText(
                        context,
                        if (it) strings.permissionMonitoringEnabled else strings.permissionMonitoringDisabled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Automation & Blockchain Section
            Text(
                text = strings.automationBlockchain,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Auto Optimization
            SettingCard(
                icon = Icons.Default.Tune,
                title = strings.autoOptimization,
                description = strings.automaticDeviceOptimization,
                isToggled = autoOptimization.value,
                onToggle = {
                    autoOptimization.value = it
                    Toast.makeText(
                        context,
                        if (it) strings.autoOptimizationEnabled else strings.autoOptimizationDisabled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            // Cloud Sync for Blockchain
            SettingCard(
                icon = Icons.Default.Cloud,
                title = strings.cloudSync,
                description = strings.syncSecurityRecords,
                isToggled = cloudSync.value,
                onToggle = {
                    cloudSync.value = it
                    Toast.makeText(
                        context,
                        if (it) strings.cloudSyncEnabled else strings.cloudSyncDisabled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Info Section
            Text(
                text = strings.about,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Toast.makeText(context, "Optira v1.0.0", Toast.LENGTH_SHORT).show()
                    },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = strings.appVersion,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.appVersion,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.adaptiveDeviceIntelligence,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "v1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Check for Updates
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Toast.makeText(context, strings.checkingForUpdates, Toast.LENGTH_SHORT).show()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.checkForUpdates,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.latest,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        }
    )

    // Language Selection Dialog
    if (showLanguageDialog.value) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog.value = false },
            title = { Text(strings.selectLanguage) },
            text = {
                Column {
                    Text(
                        strings.english,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentLanguage.value = "en"
                                showLanguageDialog.value = false
                                Toast.makeText(context, strings.languageChanged, Toast.LENGTH_SHORT).show()
                            }
                            .padding(16.dp)
                    )
                    Text(
                        strings.korean,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentLanguage.value = "ko"
                                showLanguageDialog.value = false
                                Toast.makeText(context, strings.languageChanged, Toast.LENGTH_SHORT).show()
                            }
                            .padding(16.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showLanguageDialog.value = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingCard(
    icon: ImageVector,
    title: String,
    description: String,
    isToggled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isToggled) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = isToggled,
                onCheckedChange = onToggle
            )
        }
    }
}
