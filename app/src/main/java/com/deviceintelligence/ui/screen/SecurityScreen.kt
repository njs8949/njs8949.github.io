package com.deviceintelligence.ui.screen

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deviceintelligence.viewmodel.SecurityViewModel
import com.deviceintelligence.utils.getCurrentStrings

@Composable
fun SecurityScreen(
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val strings = getCurrentStrings()
    val threats = viewModel.threats.value ?: emptyList()
    val analysis = viewModel.analysis.value
    val isAnalyzing = viewModel.isAnalyzing.value ?: false
    val syncStatus = viewModel.syncStatus.value ?: "Ready"

    val securityScore = viewModel.getSecurityScore()
    val threatSummary = viewModel.getThreatSummary()

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                // Security Score Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = strings.securityLevel,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$securityScore",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 60.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = when {
                                securityScore >= 80 -> Color(0xFF66BB6A)
                                securityScore >= 60 -> Color(0xFFFFA726)
                                else -> Color(0xFFEF5350)
                            }
                        )
                        Text(
                            text = "/100",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            item {
                // Threat Summary Cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThreatSummaryCard(
                            title = "Total",
                            count = threatSummary["total"] ?: 0,
                            color = Color(0xFF9C27B0),
                            modifier = Modifier.weight(1f)
                        )
                        ThreatSummaryCard(
                            title = "Critical",
                            count = threatSummary["critical"] ?: 0,
                            color = Color(0xFFEF5350),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThreatSummaryCard(
                            title = "High",
                            count = threatSummary["high"] ?: 0,
                            color = Color(0xFFFFA726),
                            modifier = Modifier.weight(1f)
                        )
                        ThreatSummaryCard(
                            title = "Medium",
                            count = threatSummary["medium"] ?: 0,
                            color = Color(0xFFFFB74D),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Analysis Button
                Button(
                    onClick = { viewModel.performSecurityAnalysis() },
                    enabled = !isAnalyzing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Security, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(if (isAnalyzing) strings.analyzing else strings.scanNow)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // AI Analysis Result
                if (analysis != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = strings.aiAnalysis,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${strings.riskLevel}:", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    analysis!!.riskLevel,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = when (analysis!!.riskLevel) {
                                        "CRITICAL" -> Color(0xFFEF5350)
                                        "HIGH" -> Color(0xFFFFA726)
                                        "MEDIUM" -> Color(0xFFFFB74D)
                                        else -> Color(0xFF66BB6A)
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "${strings.behaviorScore}: ${analysis!!.behaviorScore.toInt()}/100",
                                style = MaterialTheme.typography.bodySmall
                            )
                            LinearProgressIndicator(
                                progress = analysis!!.behaviorScore / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = Color(0xFF66BB6A)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "${strings.recommendations}:",
                                style = MaterialTheme.typography.labelSmall
                            )
                            analysis!!.recommendedActions.forEach { action ->
                                Text(
                                    "• $action",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Text(
                    strings.activeThreats,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (threats.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF66BB6A),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(strings.noThreatsDetected, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                items(threats) { threat ->
                    ThreatCard(threat, strings) {
                        viewModel.resolveThreat(threat.id)
                        Toast.makeText(context, strings.markedAsResolved, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ThreatSummaryCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                count.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }
    }
}

@Composable
fun ThreatCard(
    threat: com.deviceintelligence.data.model.SecurityThreat,
    strings: com.deviceintelligence.utils.AppStrings,
    onResolve: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        threat.threatName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "From: ${threat.threatSource}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                threat.severity >= 8 -> Color(0xFFEF5350)
                                threat.severity >= 6 -> Color(0xFFFFA726)
                                else -> Color(0xFFFFB74D)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        threat.severity.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                threat.recommendation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onResolve,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(strings.resolve, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
