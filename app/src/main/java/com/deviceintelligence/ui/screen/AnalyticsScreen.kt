package com.deviceintelligence.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.deviceintelligence.data.model.ThreatTrendPoint
import com.deviceintelligence.utils.getCurrentStrings
import com.deviceintelligence.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val strings = getCurrentStrings()
    val threatTrends = viewModel.threatTrends.value ?: emptyList()
    val correlatedThreats = viewModel.correlatedThreats.value ?: emptyList()
    val isLoading = viewModel.isLoading.value ?: false

    LaunchedEffect(Unit) {
        viewModel.generateThreatTrends()
    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 제목
            item {
                Text(
                    "위협 분석",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // 위협 추이 그래프 섹션
            item {
                Text(
                    "위협 수준 추이 (24시간)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 간단한 그래프 표현
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(150.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator()
                        } else if (threatTrends.isNotEmpty()) {
                            SimpleThreatChart(trends = threatTrends)
                        } else {
                            Text(
                                "데이터 로딩 중...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 위협 통계
            item {
                Text(
                    "주요 지표",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "최고 위협",
                        value = "${(threatTrends.maxOfOrNull { it.threatScore } ?: 0f).toInt()}",
                        color = Color(0xFFEF5350),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "평균 위협",
                        value = "${(threatTrends.map { it.threatScore }.average()).toInt()}",
                        color = Color(0xFFFFA726),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "최근 위협",
                        value = "${(threatTrends.lastOrNull()?.threatScore ?: 0f).toInt()}",
                        color = Color(0xFF66BB6A),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 연관 위협
            if (correlatedThreats.isNotEmpty()) {
                item {
                    Text(
                        "연관 위협 분석",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(correlatedThreats.size) { index ->
                    val threat = correlatedThreats[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                threat.description,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                threat.suggestedAction,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = threat.correlationScore,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SimpleThreatChart(trends: List<ThreatTrendPoint>) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        trends.takeLast(12).forEach { point ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
                    .padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(((point.threatScore / 100f) * 100).dp.coerceAtLeast(2.dp))
                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                        .background(
                            when {
                                point.threatScore >= 70 -> Color(0xFFEF5350)
                                point.threatScore >= 50 -> Color(0xFFFFA726)
                                else -> Color(0xFF66BB6A)
                            }
                        )
                )
            }
        }
    }
}
