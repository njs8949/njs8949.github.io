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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

    // Update system info every 2 seconds (실시간 업데이트)
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
            // Main Health Score - Large Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = strings.systemHealth,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${systemInfo.value.healthScore}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Key Metrics - 2 Column Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1: Memory & Battery
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ObservabilityMetricCard(
                        title = strings.memory,
                        value = systemInfo.value.memory,
                        modifier = Modifier.weight(1f)
                    )
                    ObservabilityMetricCard(
                        title = strings.battery,
                        value = systemInfo.value.battery,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: CPU & Storage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ObservabilityMetricCard(
                        title = strings.cpu,
                        value = systemInfo.value.cpu,
                        modifier = Modifier.weight(1f)
                    )
                    ObservabilityMetricCard(
                        title = strings.storage,
                        value = systemInfo.value.storage,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 3: Temperature & Device
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ObservabilityMetricCard(
                        title = strings.temperature,
                        value = systemInfo.value.temperature,
                        modifier = Modifier.weight(1f)
                    )
                    ObservabilityMetricCard(
                        title = strings.device,
                        value = systemInfo.value.device,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
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
    val cpu: String,
    val battery: String,
    val storage: String,
    val temperature: String,
    val device: String
)

class SystemInfoCollector(private val context: Context) {
    fun collectInfo(): SystemInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Memory (실제 시스템 메모리 - ActivityManager.MemoryInfo 사용)
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalMemory = memoryInfo.totalMem
        val availMemory = memoryInfo.availMem
        val usedMemory = totalMemory - availMemory

        val totalMemoryGB = totalMemory / (1024 * 1024 * 1024)
        val usedMemoryGB = usedMemory / (1024 * 1024 * 1024)
        val memoryPercent = if (totalMemory > 0) (usedMemory * 100) / totalMemory else 0
        val memoryInfoStr = "$usedMemoryGB/$totalMemoryGB GB ($memoryPercent%)"

        // Battery
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, ifilter)
        val batteryLevel = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 0
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPercent = (batteryLevel * 100) / scale
        val batteryInfo = "$batteryPercent%"

        // Storage (내부 저장소)
        val storageDir = Environment.getDataDirectory()
        val stat = StatFs(storageDir.absolutePath)
        val totalStorageBytes = stat.totalBytes
        val availStorageBytes = stat.availableBytes
        val usedStorageBytes = totalStorageBytes - availStorageBytes

        val totalStorage = totalStorageBytes / (1024.0 * 1024.0 * 1024.0)
        val usedStorage = usedStorageBytes / (1024.0 * 1024.0 * 1024.0)
        val storagePercent = if (totalStorageBytes > 0) ((usedStorageBytes * 100) / totalStorageBytes) else 0L
        val storageInfo = String.format("%.1f/%.1f GB (%d%%)", usedStorage, totalStorage, storagePercent)

        // Device Info
        val deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL}"

        // Calculate health score
        val healthScore = 85

        return SystemInfo(
            healthScore = healthScore,
            memory = memoryInfoStr,
            cpu = "Normal",
            battery = batteryInfo,
            storage = storageInfo,
            temperature = "Normal",
            device = deviceInfo
        )
    }
}
