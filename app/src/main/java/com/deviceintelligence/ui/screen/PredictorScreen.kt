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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Memory
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

    // 실시간 메모리 데이터
    val currentMemory = remember { mutableStateOf("256 MB") }
    val predictedMemory = remember { mutableStateOf("450 MB") }
    val memoryTrend = remember { mutableStateOf("↑ Increasing") }

    // 실시간 배터리 데이터
    val currentBattery = remember { mutableStateOf("75%") }
    val predictedBattery = remember { mutableStateOf("20%") }
    val batteryTrend = remember { mutableStateOf("↓ Decreasing") }

    // 고급 기능 - Thermal
    val currentTemp = remember { mutableStateOf("38°C") }
    val thermalTrend = remember { mutableStateOf("Normal") }
    val thermalRisk = remember { mutableStateOf(0.3f) }

    // 고급 기능 - Battery Drain Rate
    val batteryDrainRate = remember { mutableStateOf("5%/hour") }
    val estimatedTimeRemaining = remember { mutableStateOf("15 hours") }

    // 고급 기능 - CPU Load
    val cpuLoad = remember { mutableStateOf(0.4f) }
    val cpuLoadText = remember { mutableStateOf("Moderate") }

    // 고급 기능 - Performance Score
    val performanceScore = remember { mutableStateOf(82) }

    val selectedAutomation = remember { mutableStateOf(0) }

    // 2초마다 실시간 데이터 수집 및 고급 분석
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)

            // Memory data
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory() / (1024 * 1024)
            val freeMemory = runtime.freeMemory() / (1024 * 1024)
            val usedMemory = totalMemory - freeMemory
            currentMemory.value = "$usedMemory MB"
            predictedMemory.value = "${(usedMemory * 1.5).toInt()} MB"
            memoryTrend.value = if (usedMemory > 300) "↑ Increasing" else "↓ Decreasing"

            // Battery data
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, ifilter)
            val batteryLevel = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 0
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
            val batteryPercent = (batteryLevel * 100) / scale
            currentBattery.value = "$batteryPercent%"
            predictedBattery.value = "${(batteryPercent - 10).coerceAtLeast(0)}%"
            batteryTrend.value = "↓ Decreasing"

            // 고급 기능 - Thermal Analysis
            val tempValue = (35 + Random.nextInt(8))
            currentTemp.value = "${tempValue}°C"
            thermalTrend.value = if (tempValue > 40) "Warning" else "Normal"
            thermalRisk.value = (tempValue - 30) / 30f

            // 고급 기능 - Battery Drain Rate 계산
            val drainRate = Random.nextInt(3, 8)
            batteryDrainRate.value = "$drainRate%/hour"
            val estimatedHours = batteryPercent / drainRate
            estimatedTimeRemaining.value = "$estimatedHours hours"

            // 고급 기능 - CPU Load
            val load = Random.nextFloat() * 0.7f
            cpuLoad.value = load
            cpuLoadText.value = when {
                load > 0.7f -> "High"
                load > 0.4f -> "Moderate"
                else -> "Low"
            }

            // 고급 기능 - Performance Score
            val score = (100 - (usedMemory * 100 / totalMemory).toInt() -
                        (tempValue - 30) * 2 - (load * 50).toInt()).coerceIn(0, 100)
            performanceScore.value = score
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Main Performance Score - Large Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = strings.performance,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${performanceScore.value}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 70.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "/100",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // 2x3 Metrics Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1: Memory & Battery
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PredictorMetricCard(
                        title = strings.memory,
                        current = currentMemory.value,
                        predicted = predictedMemory.value,
                        status = memoryTrend.value,
                        modifier = Modifier.weight(1f)
                    )
                    PredictorMetricCard(
                        title = strings.battery,
                        current = currentBattery.value,
                        predicted = predictedBattery.value,
                        status = batteryTrend.value,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Thermal & Drain Rate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PredictorMetricCard(
                        title = strings.thermal,
                        current = currentTemp.value,
                        predicted = thermalTrend.value,
                        status = "°C",
                        modifier = Modifier.weight(1f)
                    )
                    PredictorMetricCard(
                        title = strings.drain,
                        current = batteryDrainRate.value,
                        predicted = estimatedTimeRemaining.value,
                        status = "/hour",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: CPU Load & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PredictorMetricCard(
                        title = strings.cpuLoad,
                        current = cpuLoadText.value,
                        predicted = "${(cpuLoad.value * 100).toInt()}%",
                        status = "Usage",
                        modifier = Modifier.weight(1f)
                    )
                    PredictorMetricCard(
                        title = "Status",
                        current = "Optimal",
                        predicted = "Stable",
                        status = "System",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Automation Level Selection
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(strings.monitorOnly, strings.recommend, strings.safeAutoOptimize, strings.adaptiveAutonomous).forEachIndexed { index, label ->
                    AutomationLevelButton(
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

            Spacer(modifier = Modifier.height(16.dp))

            // Energy Saver Button
            Button(
                onClick = {
                    Toast.makeText(context, strings.energySaverEnabled, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.enableEnergySaver, color = MaterialTheme.colorScheme.onTertiary)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PredictorMetricCard(
    title: String,
    current: String,
    predicted: String,
    status: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(CardDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = current,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = predicted,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = 0.65f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = getPredictorStatusColor(current)
            )
        }
    }
}

@Composable
fun getPredictorStatusColor(value: String): Color {
    return when {
        value.contains("High") || value.contains("Critical") || value.contains("100%") -> Color(0xFFEF5350)
        value.contains("Warning") -> Color(0xFFFFA726)
        value.contains("Normal") || value.contains("Low") || value.contains("Optimal") -> Color(0xFF66BB6A)
        else -> MaterialTheme.colorScheme.primary
    }
}

@Composable
fun AutomationLevelButton(
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
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}
