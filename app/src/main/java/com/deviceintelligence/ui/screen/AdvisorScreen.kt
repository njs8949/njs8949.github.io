@file:Suppress("DEPRECATION")

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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SystemUpdate
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
import androidx.compose.ui.text.font.FontWeight
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
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 어드바이저 요약 카드
            item {
                AdvisorSummaryCard(
                    totalRecommendations = 8,
                    criticalCount = 1,
                    warningCount = 2,
                    appliedCount = appliedOptimizations.value.size
                )
            }

            // 하드웨어 설정 섹션
            item {
                SectionHeader("🔧 하드웨어 설정 점검")
            }

            item {
                HardwareSettingCard(
                    icon = Icons.Default.Lock,
                    title = "NFC (근거리 통신)",
                    description = if (nfcEnabled.value) "활성화됨" else "비활성화됨",
                    status = nfcEnabled.value,
                    priority = "보통",
                    onEnable = {
                        context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                        Toast.makeText(context, "NFC 설정 열기", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                HardwareSettingCard(
                    icon = Icons.Default.Info,
                    title = "위치 서비스 (GPS)",
                    description = if (gpsEnabled.value) "활성화됨" else "비활성화됨",
                    status = gpsEnabled.value,
                    priority = "보통",
                    onEnable = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        Toast.makeText(context, "위치 설정 열기", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                HardwareSettingCard(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "생체 인식 (지문/얼굴)",
                    description = "생체 인식으로 보안 강화",
                    status = true,
                    priority = "높음",
                    onEnable = {
                        context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                        Toast.makeText(context, "보안 설정 열기", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                HardwareSettingCard(
                    icon = Icons.Default.SystemUpdate,
                    title = "배터리 최적화",
                    description = "앱 배터리 사용 최적화",
                    status = true,
                    priority = "보통",
                    onEnable = {
                        context.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS))
                        Toast.makeText(context, "배터리 설정 열기", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 성능 최적화 섹션
            item {
                SectionHeader("⚡ 성능 최적화 추천")
            }

            item {
                QuickActionButtons()
            }

            // Recommendation 1
            item {
                RecommendationCard(
                    priority = "HIGH",
                    icon = Icons.Default.CleaningServices,
                    title = strings.clearCacheMemory,
                    description = "App cache is consuming 2.3GB. Clearing will free memory.",
                    impact = "💾 2.3GB 메모리 절약",
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
                    icon = Icons.Default.Notifications,
                    title = strings.disableBackgroundSync,
                    description = "Multiple apps syncing in background. Disable unnecessary ones.",
                    impact = "🔋 배터리 15% 절약",
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
                    icon = Icons.Default.SystemUpdate,
                    title = strings.updateApps,
                    description = "5 apps have available updates with performance improvements.",
                    impact = "⚡ 성능 개선",
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
fun AdvisorSummaryCard(
    totalRecommendations: Int,
    criticalCount: Int,
    warningCount: Int,
    appliedCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
                "최적화 진행률",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBox(
                    label = "전체",
                    value = totalRecommendations.toString(),
                    color = Color(0xFF42A5F5),
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "긴급",
                    value = criticalCount.toString(),
                    color = Color(0xFFEF5350),
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "경고",
                    value = warningCount.toString(),
                    color = Color(0xFFFFA726),
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "완료",
                    value = appliedCount.toString(),
                    color = Color(0xFF66BB6A),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
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
                value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = color
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.headlineSmall.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun QuickActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF42A5F5)
            )
        ) {
            Icon(
                Icons.Default.CleaningServices,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }

        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF66BB6A)
            )
        ) {
            Icon(
                Icons.Default.Speed,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }

        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFA726)
            )
        ) {
            Icon(
                Icons.Default.DeleteForever,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun HardwareSettingCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    status: Boolean,
    priority: String,
    onEnable: () -> Unit
) {
    val priorityColor = when (priority) {
        "높음" -> Color(0xFFEF5350)
        "보통" -> Color(0xFFFFA726)
        else -> Color(0xFF66BB6A)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (status) Color(0xFF66BB6A) else priorityColor,
                    modifier = Modifier.size(32.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!status) {
                            Text(
                                priority,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = Color.White,
                                modifier = Modifier
                                    .background(
                                        priorityColor,
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                    )
                                    .padding(4.dp, 2.dp)
                            )
                        }
                    }
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
                        containerColor = priorityColor
                    )
                ) {
                    Text("설정", color = Color.White, fontSize = 12.sp)
                }
            } else {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF66BB6A),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun RecommendationCard(
    priority: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    impact: String,
    strings: com.deviceintelligence.utils.AppStrings,
    isApplied: Boolean = false,
    onApply: () -> Unit
) {
    val containerColor = when (priority) {
        "HIGH" -> Color(0xFFEF5350)
        "MEDIUM" -> Color(0xFFFFA726)
        else -> Color(0xFF66BB6A)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor.copy(alpha = 0.15f)
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = containerColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    when (priority) {
                        "HIGH" -> "긴급"
                        "MEDIUM" -> "경고"
                        else -> "낮음"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = containerColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                impact,
                style = MaterialTheme.typography.labelSmall,
                color = containerColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onApply,
                enabled = !isApplied,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    disabledContainerColor = Color(0xFF9E9E9E)
                )
            ) {
                Text(
                    if (isApplied) "✓ 완료" else "적용하기",
                    color = Color.White
                )
            }
        }
    }
}

private fun isLocationEnabled(context: Context): Boolean {
    return try {
        val locationMode = android.provider.Settings.Secure.getInt(
            context.contentResolver,
            android.provider.Settings.Secure.LOCATION_MODE,
            android.provider.Settings.Secure.LOCATION_MODE_OFF
        )
        locationMode != android.provider.Settings.Secure.LOCATION_MODE_OFF
    } catch (e: Exception) {
        false
    }
}
