package com.deviceintelligence.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deviceintelligence.utils.getCurrentStrings
import com.deviceintelligence.viewmodel.AdvancedDefenseViewModel

@Composable
fun AlertsAndRecommendationsScreen(
    viewModel: AdvancedDefenseViewModel = hiltViewModel()
) {
    val strings = getCurrentStrings()
    val criticalAlerts = viewModel.criticalAlerts.value ?: emptyList()
    val autoRecommendations = viewModel.autoRecommendations.value ?: emptyList()

    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.generateCriticalAlerts(
            threats = viewModel.aiToolThreats.value ?: emptyList(),
            zeroDay = viewModel.zeroDayThreats.value ?: emptyList()
        )
        viewModel.generateAutoRecommendations()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("알림 및 권장사항") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 탭 선택
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp)
                            )
                            Text("긴급 알림 (${criticalAlerts.size})")
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp)
                            )
                            Text("권장사항 (${autoRecommendations.size})")
                        }
                    }
                )
            }

            // 탭 콘텐츠
            when (selectedTab) {
                0 -> AlertsContent(alerts = criticalAlerts)
                1 -> RecommendationsContent(
                    recommendations = autoRecommendations,
                    onApply = { viewModel.applyAutoRecommendation(it) }
                )
            }
        }
    }
}

@Composable
private fun AlertsContent(alerts: List<com.deviceintelligence.data.model.CriticalAlert>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (alerts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF66BB6A),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "긴급 알림이 없습니다",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            items(alerts.size) { index ->
                val alert = alerts[index]
                AlertCard(alert = alert)
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RecommendationsContent(
    recommendations: List<com.deviceintelligence.data.model.AutoRecommendation>,
    onApply: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (recommendations.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF42A5F5),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "권장사항이 없습니다",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            items(recommendations.size) { index ->
                val rec = recommendations[index]
                RecommendationCard(
                    recommendation = rec,
                    onApply = { onApply(rec.id) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AlertCard(alert: com.deviceintelligence.data.model.CriticalAlert) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEF5350).copy(alpha = 0.15f)
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier
                            .size(28.dp)
                            .padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            alert.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFFEF5350)
                        )
                        Text(
                            alert.severity,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (alert.requiresImmediateAction) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFEF5350))
                            .padding(6.dp)
                    ) {
                        Text(
                            "긴급",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                alert.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "권장 조치: ${alert.suggestedAction}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF66BB6A),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF66BB6A).copy(alpha = 0.2f))
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: com.deviceintelligence.data.model.AutoRecommendation,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (recommendation.priority) {
                "CRITICAL" -> Color(0xFFEF5350).copy(alpha = 0.15f)
                "HIGH" -> Color(0xFFFFA726).copy(alpha = 0.15f)
                else -> Color(0xFF42A5F5).copy(alpha = 0.15f)
            }
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
                        recommendation.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = when (recommendation.priority) {
                            "CRITICAL" -> Color(0xFFEF5350)
                            "HIGH" -> Color(0xFFFFA726)
                            else -> Color(0xFF42A5F5)
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        recommendation.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (recommendation.priority) {
                                "CRITICAL" -> Color(0xFFEF5350)
                                "HIGH" -> Color(0xFFFFA726)
                                else -> Color(0xFF42A5F5)
                            }
                        )
                        .padding(6.dp)
                ) {
                    Text(
                        recommendation.priority.take(3),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "예상 효과: ${recommendation.estimatedImpact}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (recommendation.autoApplyable && !recommendation.applied) {
                    Button(
                        onClick = onApply,
                        modifier = Modifier
                            .height(28.dp)
                            .padding(start = 8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            "적용",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else if (recommendation.applied) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF66BB6A).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF66BB6A),
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )
                        Text(
                            "적용됨",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF66BB6A)
                        )
                    }
                }
            }
        }
    }
}
