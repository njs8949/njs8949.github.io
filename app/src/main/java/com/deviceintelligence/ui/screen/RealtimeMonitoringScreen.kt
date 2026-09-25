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
import com.deviceintelligence.viewmodel.MonitoringViewModel

@Composable
fun RealtimeMonitoringScreen(
    viewModel: MonitoringViewModel = hiltViewModel()
) {
    val strings = getCurrentStrings()
    val realtimeData = viewModel.realtimeData.value
    val isMonitoring = viewModel.isMonitoring.value ?: false
    val averageThreatScore = viewModel.averageThreatScore.value ?: 0f

    LaunchedEffect(Unit) {
        viewModel.startContinuousMonitoring(intervalMs = 3000)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopContinuousMonitoring()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("실시간 모니터링") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 모니터링 상태
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isMonitoring) Color(0xFF66BB6A).copy(alpha = 0.2f)
                            else Color(0xFFFF6B6B).copy(alpha = 0.2f)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "모니터링 상태",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (isMonitoring) "실시간 모니터링 중..." else "모니터링 중지",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMonitoring) Color(0xFF66BB6A) else Color(0xFFFF6B6B)
                                )
                            )
                        }
                        Icon(
                            if (isMonitoring) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isMonitoring) Color(0xFF66BB6A) else Color(0xFFFF6B6B),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // 주요 위협 지표
            if (realtimeData != null) {
                item {
                    Text(
                        "시스템 위협 점수",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                realtimeData.threatScore >= 70 -> Color(0xFFEF5350).copy(alpha = 0.15f)
                                realtimeData.threatScore >= 50 -> Color(0xFFFFA726).copy(alpha = 0.15f)
                                else -> Color(0xFF66BB6A).copy(alpha = 0.15f)
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "${realtimeData.threatScore.toInt()}",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 60.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        realtimeData.threatScore >= 70 -> Color(0xFFEF5350)
                                        realtimeData.threatScore >= 50 -> Color(0xFFFFA726)
                                        else -> Color(0xFF66BB6A)
                                    }
                                )
                            )
                            Text(
                                "/100",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = realtimeData.threatScore / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 상세 지표
                item {
                    Text(
                        "상세 지표",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    MonitoringMetricItem(
                        icon = Icons.Default.Speed,
                        label = "CPU 사용률",
                        value = "${realtimeData.cpuUsage.toInt()}%",
                        progress = realtimeData.cpuUsage / 100f,
                        color = Color(0xFF42A5F5)
                    )
                }

                item {
                    MonitoringMetricItem(
                        icon = Icons.Default.Storage,
                        label = "메모리 사용률",
                        value = "${realtimeData.memoryUsage.toInt()}%",
                        progress = realtimeData.memoryUsage / 100f,
                        color = Color(0xFFEF5350)
                    )
                }

                item {
                    MonitoringMetricItem(
                        icon = Icons.Default.BatteryAlert,
                        label = "배터리 소모",
                        value = "${realtimeData.batteryDrain.toInt()}%",
                        progress = realtimeData.batteryDrain / 100f,
                        color = Color(0xFFFFA726)
                    )
                }

                item {
                    MonitoringMetricItem(
                        icon = Icons.Default.CloudDownload,
                        label = "네트워크 활동",
                        value = "${realtimeData.networkActivity.toInt()}%",
                        progress = realtimeData.networkActivity / 100f,
                        color = Color(0xFF9C27B0)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
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
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MiniStat(
                                label = "실행 중인 프로세스",
                                value = realtimeData.processCount.toString()
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(32.dp)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                            )
                            MiniStat(
                                label = "의심 앱",
                                value = realtimeData.suspiciousApps.toString()
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(32.dp)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                            )
                            MiniStat(
                                label = "평균 위협점수",
                                value = "${averageThreatScore.toInt()}"
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
private fun MonitoringMetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    progress: Float,
    color: Color
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Text(
                    value,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
private fun MiniStat(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
    ) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
