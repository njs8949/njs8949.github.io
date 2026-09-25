package com.deviceintelligence.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.deviceintelligence.utils.getCurrentStrings

@Composable
fun AdvisorScreen() {
    val context = LocalContext.current
    val strings = getCurrentStrings()
    val appliedOptimizations = remember { mutableStateOf(setOf<String>()) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Recommendation 1
            RecommendationCard(
                priority = "HIGH",
                title = strings.clearCacheMemory,
                description = "App cache is consuming 2.3GB. Clearing will free memory.",
                impact = "Save 2.3GB RAM",
                strings = strings,
                isApplied = appliedOptimizations.value.contains("cache"),
                onApply = {
                    appliedOptimizations.value = appliedOptimizations.value + "cache"
                    Toast.makeText(context, "Cache cleared! Freed 2.3GB RAM", Toast.LENGTH_SHORT).show()
                }
            )

            // Recommendation 2
            RecommendationCard(
                priority = "MEDIUM",
                title = strings.disableBackgroundSync,
                description = "Multiple apps syncing in background. Disable unnecessary ones.",
                impact = "Save 15% Battery",
                strings = strings,
                isApplied = appliedOptimizations.value.contains("sync"),
                onApply = {
                    appliedOptimizations.value = appliedOptimizations.value + "sync"
                    Toast.makeText(context, "Background sync disabled! Battery improved 15%", Toast.LENGTH_SHORT).show()
                }
            )

            // Recommendation 3
            RecommendationCard(
                priority = "LOW",
                title = strings.updateApps,
                description = "5 apps have available updates with performance improvements.",
                impact = "Improve Performance",
                strings = strings,
                isApplied = appliedOptimizations.value.contains("update"),
                onApply = {
                    appliedOptimizations.value = appliedOptimizations.value + "update"
                    Toast.makeText(context, strings.appUpdates, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun RecommendationCard(
    priority: String,
    title: String,
    description: String,
    impact: String,
    strings: com.deviceintelligence.utils.AppStrings,
    isApplied: Boolean = false,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (priority) {
                "HIGH" -> MaterialTheme.colorScheme.errorContainer
                "MEDIUM" -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "[$priority] $title",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "💡 Impact: $impact",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onApply,
                enabled = !isApplied,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = if (isApplied) strings.applied else strings.applyOptimization,
                    color = if (isApplied) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

