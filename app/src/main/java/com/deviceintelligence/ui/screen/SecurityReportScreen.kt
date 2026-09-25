package com.deviceintelligence.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
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
import com.deviceintelligence.data.model.SecurityReport
import com.deviceintelligence.utils.getCurrentStrings
import com.deviceintelligence.viewmodel.AnalyticsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SecurityReportScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val strings = getCurrentStrings()
    val report = viewModel.securityReport.value
    var selectedPeriod by remember { mutableStateOf("WEEKLY") }

    LaunchedEffect(selectedPeriod) {
        if (selectedPeriod == "WEEKLY") {
            viewModel.generateWeeklyReport(
                totalThreats = 12,
                resolvedThreats = 8,
                criticalCount = 1,
                highCount = 3,
                mediumCount = 4,
                lowCount = 4
            )
        } else {
            viewModel.generateMonthlyReport(
                totalThreats = 45,
                resolvedThreats = 38,
                criticalCount = 2,
                highCount = 8,
                mediumCount = 15,
                lowCount = 20
            )
        }
    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 헤더
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "보안 보고서",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "주간/월간 보안 분석 및 권장사항",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 기간 선택
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedPeriod == "WEEKLY",
                        onClick = { selectedPeriod = "WEEKLY" },
                        label = { Text("이번 주") }
                    )
                    FilterChip(
                        selected = selectedPeriod == "MONTHLY",
                        onClick = { selectedPeriod = "MONTHLY" },
                        label = { Text("이번 달") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 보고서 내용
            if (report != null) {
                // 요약
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "보안 상태",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatBubble(
                                    label = "종합 점수",
                                    value = report.systemHealth.toInt(),
                                    color = when {
                                        report.systemHealth >= 80 -> Color(0xFF66BB6A)
                                        report.systemHealth >= 60 -> Color(0xFFFFA726)
                                        else -> Color(0xFFEF5350)
                                    }
                                )
                                StatBubble(
                                    label = "해결율",
                                    value = if (report.totalThreatsDetected > 0) {
                                        (report.threatsResolved * 100 / report.totalThreatsDetected)
                                    } else {
                                        100
                                    },
                                    color = Color(0xFF42A5F5)
                                )
                                StatBubble(
                                    label = "Critical",
                                    value = report.criticalThreats,
                                    color = Color(0xFFEF5350)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 위협 현황
                item {
                    Text(
                        "위협 현황",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        ThreatStatRow(
                            "전체",
                            report.totalThreatsDetected,
                            Color(0xFF9E9E9E)
                        )
                        ThreatStatRow(
                            "Critical",
                            report.criticalThreats,
                            Color(0xFFEF5350)
                        )
                        ThreatStatRow(
                            "High",
                            report.highThreats,
                            Color(0xFFFFA726)
                        )
                        ThreatStatRow(
                            "Medium",
                            report.mediumThreats,
                            Color(0xFFFFB74D)
                        )
                        ThreatStatRow(
                            "Low",
                            report.lowThreats,
                            Color(0xFF66BB6A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(4.dp))
                        ThreatStatRow(
                            "해결됨",
                            report.threatsResolved,
                            Color(0xFF66BB6A)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 권장사항
                item {
                    Text(
                        "권장사항",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(report.recommendations.size) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF66BB6A).copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF66BB6A),
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp)
                            )
                            Text(
                                report.recommendations[index],
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 요약
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(end = 8.dp)
                                )
                                Text(
                                    "보고서 요약",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                report.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun StatBubble(
    label: String,
    value: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            value.toString(),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ThreatStatRow(
    label: String,
    count: Int,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            count.toString(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
    }
}
