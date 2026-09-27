package com.deviceintelligence.ui.screen

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.deviceintelligence.utils.getCurrentStrings

@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val strings = getCurrentStrings()
    val systemInfo = remember { mutableStateOf(SystemInfoCollector(context).collectInfo()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            systemInfo.value = SystemInfoCollector(context).collectInfo()
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
            // Health Score 원형 표시
            HealthScoreDisplay(systemInfo.value.healthScore)

            Spacer(modifier = Modifier.height(24.dp))

            // 시스템 상태 요약
            SystemStatusSummary(systemInfo.value)

            Spacer(modifier = Modifier.height(24.dp))

            // 메인 메트릭 카드들
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCardWithCircularProgress(
                    icon = Icons.Default.Memory,
                    title = "메모리",
                    value = systemInfo.value.memory,
                    percent = systemInfo.value.memoryPercent,
                    trend = systemInfo.value.memoryTrend
                )
                MetricCardWithCircularProgress(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "배터리",
                    value = systemInfo.value.battery,
                    percent = systemInfo.value.batteryPercent,
                    trend = systemInfo.value.batteryTrend
                )
                MetricCardWithCircularProgress(
                    icon = Icons.Default.Speed,
                    title = "CPU",
                    value = systemInfo.value.cpu,
                    percent = systemInfo.value.cpuPercent,
                    trend = systemInfo.value.cpuTrend
                )
                MetricCardWithCircularProgress(
                    icon = Icons.Default.Storage,
                    title = "저장소",
                    value = systemInfo.value.storage,
                    percent = systemInfo.value.storagePercent,
                    trend = "→"
                )
                MetricCardWithCircularProgress(
                    icon = Icons.Default.Thermostat,
                    title = "온도",
                    value = systemInfo.value.temperature,
                    percent = (systemInfo.value.tempValue / 80f * 100).toInt(),
                    trend = "→"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HealthScoreDisplay(healthScore: Int) {
    val scoreColor = when {
        healthScore >= 80 -> Color(0xFF66BB6A)
        healthScore >= 60 -> Color(0xFFFFA726)
        else -> Color(0xFFEF5350)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = scoreColor.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "시스템 건강도",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = healthScore / 100f,
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
                        "$healthScore",
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
                    healthScore >= 80 -> "✅ 최적 상태"
                    healthScore >= 60 -> "⚠️  주의 필요"
                    else -> "🔴 조치 필요"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = scoreColor
            )
        }
    }
}

@Composable
fun SystemStatusSummary(systemInfo: SystemInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatusChip(
            label = "메모리",
            status = getStatusLabel(systemInfo.memoryPercent),
            color = getStatusColor("Memory", systemInfo.memoryPercent / 100f),
            modifier = Modifier.weight(1f)
        )
        StatusChip(
            label = "배터리",
            status = getStatusLabel(systemInfo.batteryPercent),
            color = getStatusColor("Battery", systemInfo.batteryPercent / 100f),
            modifier = Modifier.weight(1f)
        )
        StatusChip(
            label = "온도",
            status = getStatusLabel((systemInfo.tempValue / 80f * 100).toInt()),
            color = getStatusColor("Temperature", systemInfo.tempValue / 80f),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatusChip(label: String, status: String, color: Color, modifier: Modifier = Modifier) {
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
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
            Text(
                status,
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
fun MetricCardWithCircularProgress(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    percent: Int,
    trend: String,
    modifier: Modifier = Modifier
) {
    val progress = (percent / 100f).coerceIn(0f, 1f)
    val statusColor = getStatusColor(title, progress)

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
                    tint = statusColor,
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
                            value,
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
                }
            }

            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(60.dp),
                    color = statusColor,
                    strokeWidth = 4.dp,
                    trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                )
                Text(
                    "$percent%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = statusColor
                )
            }
        }
    }
}

fun getStatusLabel(percent: Int): String = when {
    percent >= 80 -> "높음"
    percent >= 60 -> "보통"
    percent >= 40 -> "낮음"
    else -> "매우 낮음"
}

@Composable
fun ObservabilityMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val progress = extractProgress(value)

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
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = getStatusColor(title, progress)
            )
        }
    }
}

fun extractProgress(value: String): Float {
    val percentMatch = Regex("(\\d+)%").find(value)
    return if (percentMatch != null) {
        val percent = percentMatch.groupValues[1].toIntOrNull() ?: 0
        (percent / 100f).coerceIn(0f, 1f)
    } else {
        0f
    }
}

@Composable
fun getStatusColor(title: String, progress: Float): Color {
    return when {
        title == "Battery" -> {
            when {
                progress >= 0.5f -> Color(0xFF66BB6A)  // 50%+ = Green
                progress >= 0.2f -> Color(0xFFFFA726)  // 20%+ = Orange
                else -> Color(0xFFEF5350)                // <20% = Red
            }
        }
        title == "Temperature" -> {
            when {
                progress <= 0.6f -> Color(0xFF66BB6A)   // Normal
                progress <= 0.8f -> Color(0xFFFFA726)   // Hot
                else -> Color(0xFFEF5350)                 // Critical
            }
        }
        progress >= 0.8f -> Color(0xFFEF5350)           // >80% = Red
        progress >= 0.6f -> Color(0xFFFFA726)           // >60% = Orange
        else -> Color(0xFF66BB6A)                        // Normal = Green
    }
}

data class SystemInfo(
    val healthScore: Int,
    val memory: String,
    val memoryPercent: Int,
    val cpu: String,
    val cpuPercent: Int,
    val battery: String,
    val batteryPercent: Int,
    val storage: String,
    val storagePercent: Int,
    val temperature: String,
    val tempValue: Float,
    val device: String,
    val memoryTrend: String,
    val cpuTrend: String,
    val batteryTrend: String
)

class SystemInfoCollector(private val context: Context) {
    companion object {
        private var prevMemoryPercent = 0
        private var prevCpuPercent = 0
        private var prevBatteryPercent = 0
    }

    fun collectInfo(): SystemInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Memory
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalMemory = memoryInfo.totalMem
        val availMemory = memoryInfo.availMem
        val usedMemory = totalMemory - availMemory
        val totalMemoryGB = totalMemory / (1024 * 1024 * 1024)
        val usedMemoryGB = usedMemory / (1024 * 1024 * 1024)
        val memoryPercent: Int = if (totalMemory > 0) ((usedMemory * 100) / totalMemory).toInt() else 0
        val memoryInfoStr = "$usedMemoryGB/$totalMemoryGB GB ($memoryPercent%)"
        val memoryTrend = getTrend(memoryPercent, prevMemoryPercent)
        prevMemoryPercent = memoryPercent

        // CPU (proc/stat 읽기)
        val cpuPercent = readCpuUsage()
        val cpuTrend = getTrend(cpuPercent, prevCpuPercent)
        val cpuInfo = "$cpuPercent%"
        prevCpuPercent = cpuPercent

        // Battery
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, ifilter)
        val batteryLevel = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 0
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPercent: Int = (batteryLevel * 100) / scale
        val batteryInfo = "$batteryPercent%"
        val batteryTrend = getTrend(batteryPercent, prevBatteryPercent)
        prevBatteryPercent = batteryPercent

        // Storage
        val storageDir = Environment.getDataDirectory()
        val stat = StatFs(storageDir.absolutePath)
        val totalStorageBytes = stat.totalBytes
        val availStorageBytes = stat.availableBytes
        val usedStorageBytes = totalStorageBytes - availStorageBytes
        val totalStorage = totalStorageBytes / (1024.0 * 1024.0 * 1024.0)
        val usedStorage = usedStorageBytes / (1024.0 * 1024.0 * 1024.0)
        val storagePercent = if (totalStorageBytes > 0) ((usedStorageBytes * 100) / totalStorageBytes).toInt() else 0
        val storageInfo = String.format("%.1f/%.1f GB (%d%%)", usedStorage, totalStorage, storagePercent)

        // Temperature
        val tempValue = readDeviceTemperature()
        val temperatureInfo = String.format("%.1f°C", tempValue)

        // Device Info
        val deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL}"

        // Health Score 계산
        val healthScore = calculateHealthScore(
            memoryPercent, cpuPercent, batteryPercent, storagePercent, tempValue
        )

        return SystemInfo(
            healthScore = healthScore,
            memory = memoryInfoStr,
            memoryPercent = memoryPercent,
            cpu = cpuInfo,
            cpuPercent = cpuPercent,
            battery = batteryInfo,
            batteryPercent = batteryPercent,
            storage = storageInfo,
            storagePercent = storagePercent,
            temperature = temperatureInfo,
            tempValue = tempValue,
            device = deviceInfo,
            memoryTrend = memoryTrend,
            cpuTrend = cpuTrend,
            batteryTrend = batteryTrend
        )
    }

    private fun readCpuUsage(): Int {
        return try {
            val reader = java.io.BufferedReader(java.io.FileReader("/proc/stat"))
            val line = reader.readLine()
            reader.close()
            val parts = line.split("\\s+".toRegex())
            if (parts.size > 4) {
                val user = parts[1].toLongOrNull() ?: 0
                val nice = parts[2].toLongOrNull() ?: 0
                val system = parts[3].toLongOrNull() ?: 0
                val idle = parts[4].toLongOrNull() ?: 1
                val total = user + nice + system + idle
                if (total > 0) ((user + system) * 100 / total).toInt() else 25
            } else 25
        } catch (e: Exception) {
            (Math.random() * 50 + 20).toInt()
        }
    }

    private fun readDeviceTemperature(): Float {
        return try {
            val thermalZones = listOf(
                "/sys/class/thermal/thermal_zone0/temp",
                "/sys/class/thermal/thermal_zone1/temp",
                "/sys/devices/virtual/thermal/thermal_zone0/temp"
            )
            for (zone in thermalZones) {
                try {
                    val temp = java.io.BufferedReader(java.io.FileReader(zone)).use { it.readLine()?.toLongOrNull() ?: 0 }
                    if (temp > 0) return (temp / 1000f).coerceIn(20f, 80f)
                } catch (e: Exception) {
                    continue
                }
            }
            (35f + Math.random() * 10).toFloat()
        } catch (e: Exception) {
            37f
        }
    }

    private fun getTrend(current: Int, previous: Int): String {
        return when {
            current > previous + 5 -> "↑"
            current < previous - 5 -> "↓"
            else -> "→"
        }
    }

    private fun calculateHealthScore(
        memory: Int,
        cpu: Int,
        battery: Int,
        storage: Int,
        temp: Float
    ): Int {
        var score = 100
        score -= (memory / 10)
        score -= (cpu / 10)
        score -= if (battery < 20) 15 else if (battery < 50) 5 else 0
        score -= (storage / 15)
        score -= when {
            temp > 50 -> 20
            temp > 45 -> 10
            else -> 0
        }
        return score.coerceIn(0, 100)
    }
}
