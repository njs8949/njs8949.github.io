@file:Suppress("DEPRECATION")

package com.deviceintelligence.ui.screen

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.deviceintelligence.utils.getCurrentStrings

@Composable
fun PredictorScreen() {
    val context = LocalContext.current
    val strings = getCurrentStrings()

    val currentMemory = remember { mutableStateOf("256 MB") }
    val predictedMemory = remember { mutableStateOf("450 MB") }
    val memoryTrend = remember { mutableStateOf("↑") }

    val currentBattery = remember { mutableStateOf("75%") }
    val predictedBattery = remember { mutableStateOf("20%") }
    val batteryTrend = remember { mutableStateOf("↓") }

    val currentTemp = remember { mutableStateOf("38°C") }
    val thermalTrend = remember { mutableStateOf("Normal") }
    val thermalRisk = remember { mutableStateOf(0.3f) }

    val batteryDrainRate = remember { mutableStateOf("5%/hour") }
    val estimatedTimeRemaining = remember { mutableStateOf("15 hours") }

    val cpuLoad = remember { mutableStateOf(0.4f) }
    val cpuLoadText = remember { mutableStateOf("Moderate") }

    val performanceScore = remember { mutableStateOf(82) }
    val selectedAutomation = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)

            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory() / (1024 * 1024)
            val freeMemory = runtime.freeMemory() / (1024 * 1024)
            val usedMemory = totalMemory - freeMemory
            currentMemory.value = "$usedMemory MB"
            predictedMemory.value = "${(usedMemory * 1.5).toInt()} MB"
            memoryTrend.value = if (usedMemory > 300) "↑" else "↓"

            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, ifilter)
            val batteryLevel = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 0
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
            val batteryPercent = (batteryLevel * 100) / scale
            currentBattery.value = "$batteryPercent%"
            predictedBattery.value = "${(batteryPercent - 10).coerceAtLeast(0)}%"
            batteryTrend.value = "↓"

            val tempValue = (35 + Random.nextInt(8))
            currentTemp.value = "${tempValue}°C"
            thermalTrend.value = if (tempValue > 40) "Warning" else "Normal"
            thermalRisk.value = (tempValue - 30) / 30f

            val drainRate = Random.nextInt(3, 8)
            batteryDrainRate.value = "$drainRate%/hour"
            val estimatedHours = batteryPercent / drainRate
            estimatedTimeRemaining.value = "$estimatedHours:00"

            val load = Random.nextFloat() * 0.7f
            cpuLoad.value = load
            cpuLoadText.value = when {
                load > 0.7f -> "High"
                load > 0.4f -> "Moderate"
                else -> "Low"
            }

            val score = (100 - (usedMemory * 100 / totalMemory).toInt() -
                        (tempValue - 30) * 2 - (load * 50).toInt()).coerceIn(0, 100)
            performanceScore.value = score
        }
    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Performance Score 원형 표시
            item {
                PerformanceScoreDisplay(performanceScore.value)
            }

            // 예측 상태 요약
            item {
                PredictionSummary(
                    batteryRemaining = estimatedTimeRemaining.value,
                    memoryStatus = memoryTrend.value,
                    cpuStatus = cpuLoadText.value
                )
            }

            // 상세 메트릭들
            item {
                Text(
                    "📊 상세 분석",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 메모리 예측
            item {
                PredictionMetricCardEnhanced(
                    icon = Icons.Default.Memory,
                    title = "메모리 사용",
                    current = currentMemory.value,
                    predicted = predictedMemory.value,
                    trend = memoryTrend.value,
                    trendLabel = "변화"
                )
            }

            // 배터리 예측
            item {
                PredictionMetricCardEnhanced(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "배터리",
                    current = currentBattery.value,
                    predicted = estimatedTimeRemaining.value,
                    trend = batteryTrend.value,
                    trendLabel = "남은 시간"
                )
            }

            // CPU 로드
            item {
                PredictionMetricCardEnhanced(
                    icon = Icons.Default.Speed,
                    title = "CPU 로드",
                    current = cpuLoadText.value,
                    predicted = "${(cpuLoad.value * 100).toInt()}%",
                    trend = "→",
                    trendLabel = "사용률"
                )
            }

            // 온도 분석
            item {
                PredictionMetricCardEnhanced(
                    icon = Icons.Default.LocalFireDepartment,
                    title = "온도",
                    current = currentTemp.value,
                    predicted = thermalTrend.value,
                    trend = "→",
                    trendLabel = "상태"
                )
            }

            // 배터리 소비량
            item {
                BatteryDrainCard(drainRate = batteryDrainRate.value)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 자동화 레벨 선택
            item {
                Text(
                    "⚙️ 자동화 수준",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(strings.monitorOnly, strings.recommend, strings.safeAutoOptimize, strings.adaptiveAutonomous).forEachIndexed { index, label ->
                        AutomationLevelButtonEnhanced(
                            label = label,
                            isSelected = selectedAutomation.value == index,
                            onClick = {
                                selectedAutomation.value = index
                                Toast.makeText(
                                    context,
                                    "Automation Level: $label",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }

            // Energy Saver Button
            item {
                Button(
                    onClick = {
                        Toast.makeText(context, strings.energySaverEnabled, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.enableEnergySaver, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PerformanceScoreDisplay(score: Int) {
    val scoreColor = when {
        score >= 80 -> Color(0xFF66BB6A)
        score >= 60 -> Color(0xFFFFA726)
        else -> Color(0xFFEF5350)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = scoreColor.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "예측 성능 점수",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = score / 100f,
                    modifier = Modifier.size(140.dp),
                    color = scoreColor,
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "$score",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = scoreColor
                    )
                    Text(
                        "/100",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                when {
                    score >= 80 -> "✅ 최적 성능"
                    score >= 60 -> "⚠️  개선 필요"
                    else -> "🔴 즉시 조치"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = scoreColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PredictionSummary(batteryRemaining: String, memoryStatus: String, cpuStatus: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PredictionChip(
            icon = Icons.Default.BatteryChargingFull,
            label = "배터리",
            value = batteryRemaining,
            color = Color(0xFFEF5350),
            modifier = Modifier.weight(1f)
        )
        PredictionChip(
            icon = Icons.Default.Memory,
            label = "메모리",
            value = memoryStatus,
            color = Color(0xFF42A5F5),
            modifier = Modifier.weight(1f)
        )
        PredictionChip(
            icon = Icons.Default.Speed,
            label = "CPU",
            value = cpuStatus,
            color = Color(0xFFFFA726),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PredictionChip(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
            Text(
                value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = color
            )
        }
    }
}

@Composable
fun PredictionMetricCardEnhanced(
    icon: ImageVector,
    title: String,
    current: String,
    predicted: String,
    trend: String,
    trendLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            current,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            trend,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        "예측: $predicted",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = 0.65f,
                        modifier = Modifier.size(50.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                        trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    Text(
                        "65%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    trendLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun BatteryDrainCard(drainRate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            "배터리 소비",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            drainRate,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFFEF5350)
                        )
                    }
                }
                Text(
                    "⚠️",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = 0.4f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = Color(0xFFEF5350)
            )
        }
    }
}

@Composable
fun AutomationLevelButtonEnhanced(
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
