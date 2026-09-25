package com.deviceintelligence.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deviceintelligence.ui.screen.DashboardScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.deviceintelligence.utils.currentLanguage
import com.deviceintelligence.utils.getCurrentStrings
import com.deviceintelligence.ui.screen.AdvisorScreen
import com.deviceintelligence.ui.screen.PredictorScreen
import com.deviceintelligence.ui.screen.SettingsScreen
import com.deviceintelligence.ui.screen.IntegratedSecurityScreen
import com.deviceintelligence.ui.screen.AnalyticsScreen
import com.deviceintelligence.ui.screen.SecurityReportScreen
import com.deviceintelligence.ui.screen.RealtimeMonitoringScreen
import com.deviceintelligence.ui.screen.RuleManagementScreen
import com.deviceintelligence.ui.screen.AlertsAndRecommendationsScreen
import com.deviceintelligence.ui.screen.BlockchainVerificationScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    val localNavController = rememberNavController()

    NavHost(
        navController = localNavController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = localNavController)
        }
        composable("settings") {
            SettingsScreen(navController = localNavController)
        }
        composable("analytics") {
            AnalyticsScreen()
        }
        composable("security_report") {
            SecurityReportScreen()
        }
        composable("realtime_monitoring") {
            RealtimeMonitoringScreen()
        }
        composable("rule_management") {
            RuleManagementScreen()
        }
        composable("alerts_recommendations") {
            AlertsAndRecommendationsScreen()
        }
        composable("blockchain_verification") {
            BlockchainVerificationScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val selectedTab = remember { mutableIntStateOf(0) }
    val strings = getCurrentStrings()

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar with Settings
        TopAppBar(
            title = { Text("Optira") },
            actions = {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        // Tab Navigation
        TabRow(selectedTabIndex = selectedTab.value) {
            Tab(
                selected = selectedTab.value == 0,
                onClick = { selectedTab.value = 0 },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = strings.dashboard) },
                text = { Text(strings.dashboard) }
            )
            Tab(
                selected = selectedTab.value == 1,
                onClick = { selectedTab.value = 1 },
                icon = { Icon(Icons.Default.Build, contentDescription = strings.advisor) },
                text = { Text(strings.advisor) }
            )
            Tab(
                selected = selectedTab.value == 2,
                onClick = { selectedTab.value = 2 },
                icon = { Icon(Icons.Default.TrendingUp, contentDescription = strings.predictor) },
                text = { Text(strings.predictor) }
            )
            Tab(
                selected = selectedTab.value == 3,
                onClick = { selectedTab.value = 3 },
                icon = { Icon(Icons.Default.Security, contentDescription = "Advanced Security") },
                text = { Text("Advanced Security") }
            )
        }

        // Tab Content
        when (selectedTab.value) {
            0 -> DashboardScreen(navController)
            1 -> AdvisorScreen()
            2 -> PredictorScreen()
            3 -> IntegratedSecurityScreen()
        }
    }
}
