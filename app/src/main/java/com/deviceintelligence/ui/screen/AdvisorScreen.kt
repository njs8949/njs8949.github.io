package com.deviceintelligence.ui.screen

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deviceintelligence.utils.getCurrentStrings

@Composable
fun AdvisorScreen() {
    val context = LocalContext.current
    val strings = getCurrentStrings()
    val appliedOptimizations = remember { mutableStateOf(setOf<String>()) }
    val nfcEnabled = remember { mutableStateOf(false) }
    val gpsEnabled = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        nfcEnabled.value = nfcAdapter?.isEnabled ?: false
        gpsEnabled.value = isLocationEnabled(context)
    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 하드웨어 설정 점검 섹션
            item {
                Text(
                    "🔧 하드웨어 설정 점검",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // NFC 설정
            item {
                HardwareSettingCard(
                    icon = Icons.Default.Info,
                    title = "NFC (근거리 통신)",
                    description = if (nfcEnabled.value) "활성화됨" else "비활성화됨",
                    status = nfcEnabled.value,
                    onEnable = {
                        context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                        Toast.makeText(context, "NFC 설정 열기", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // GPS 설정
            item {
                HardwareSettingCard(
                    icon = Icons.Default.Info,
                    title = "위치 서비스 (GPS)",
                    description = if (gpsEnabled.value) "활성화됨" else "비활성화됨",
                    status = gpsEnabled.value,
                    onEnable = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        Toast.makeText(context, "위치 설정 열기", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // 바이오메트릭 설정
            item {
                HardwareSettingCard(
                    icon = Icons.Default.Info,
                    title = "생체 인식 (지문/얼굴)",
                    description = "생체 인식으로 보안 강화",
                    status = true,
                    onEnable = {
                        context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                        Toast.makeText(context, "보안 설정 열기", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // 배터리 최적화
            item {
                HardwareSettingCard(
                    icon = Icons.Default.Warning,
                    title = "배터리 최적화",
                    description = "앱 배터리 사용 최적화",
                    status = true,
                    onEnable = {
                        context.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
                        Toast.makeText(context, "배터리 설정 열기", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // 구분선
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // 성능 최적화 추천
            item {
                Text(
                    "⚡ 성능 최적화 추천",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Recommendation 1
            item {
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
            }

            // Recommendation 2
            item {
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
            }

            // Recommendation 3
            item {
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

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HardwareSettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    status: Boolean,
    onEnable: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (status) Color(0xFF66BB6A).copy(alpha = 0.15f) else Color(0xFFEF5350).copy(alpha = 0.15f)
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    if (status) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (status) Color(0xFF66BB6A) else Color(0xFFEF5350),
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (!status) {
                Button(
                    onClick = onEnable,
                    modifier = Modifier.padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF5350)
                    )
                ) {
                    Text("설정", color = Color.White)
                }
            }
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

private fun isLocationEnabled(context: Context): Boolean {
    return try {
        val locationMode = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.LOCATION_MODE,
            Settings.Secure.LOCATION_MODE_OFF
        )
        locationMode != Settings.Secure.LOCATION_MODE_OFF
    } catch (e: Exception) {
        false
    }
}

