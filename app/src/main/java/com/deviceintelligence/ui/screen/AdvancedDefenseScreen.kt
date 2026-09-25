package com.deviceintelligence.ui.screen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deviceintelligence.data.model.*
import com.deviceintelligence.utils.getCurrentStrings
import com.deviceintelligence.viewmodel.AdvancedDefenseViewModel

@Composable
fun AdvancedDefenseScreen(
    viewModel: AdvancedDefenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val strings = getCurrentStrings()
    val isAnalyzing = viewModel.isAnalyzing.value ?: false
    val overallScore = viewModel.overallThreatScore.value ?: 0f

    val aiToolThreats = viewModel.aiToolThreats.value ?: emptyList()
    val promptInjections = viewModel.promptInjectionDetections.value ?: emptyList()
    val modelExtraction = viewModel.modelExtractionThreats.value ?: emptyList()
    val zeroDayThreats = viewModel.zeroDayThreats.value ?: emptyList()
    val deepFakeResults = viewModel.deepFakeResults.value ?: emptyList()
    val statistics = viewModel.getThreatStatistics()

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 종합 위협 점수
            item {
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
                            text = "AI Attack Threat Level",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${overallScore.toInt()}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 70.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = when {
                                overallScore >= 80 -> Color(0xFFEF5350)
                                overallScore >= 60 -> Color(0xFFFFA726)
                                else -> Color(0xFF66BB6A)
                            }
                        )
                        Text(
                            text = "/100",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 위협 통계
            item {
                ThreatStatisticsCard(statistics = statistics)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 종합 분석 버튼
            item {
                Button(
                    onClick = { viewModel.performComprehensiveAnalysis("com.android.system") },
                    enabled = !isAnalyzing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Security, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(if (isAnalyzing) "Analyzing..." else "Run AI Analysis")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 대응적 AI 방어
            item {
                if (aiToolThreats.isNotEmpty()) {
                    Text(
                        "Adversarial Threats (${aiToolThreats.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        aiToolThreats.forEach { threat ->
                            ThreatCardAdvanced(threat = threat)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 프롬프트 인젝션
            item {
                if (promptInjections.isNotEmpty()) {
                    Text(
                        "Prompt Injection (${promptInjections.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        promptInjections.forEach { injection ->
                            PromptInjectionCard(injection = injection)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 모델 추출 공격
            item {
                if (modelExtraction.isNotEmpty()) {
                    Text(
                        "Model Extraction (${modelExtraction.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        modelExtraction.forEach { attack ->
                            ModelExtractionCard(attack = attack)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Zero-Day 위협
            item {
                if (zeroDayThreats.isNotEmpty()) {
                    Text(
                        "Zero-Day (${zeroDayThreats.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        zeroDayThreats.forEach { threat ->
                            ZeroDayCard(threat = threat)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Deep Fake 감지
            item {
                val deepFakeDetected = deepFakeResults.filter { it.isDeepFake }
                if (deepFakeDetected.isNotEmpty()) {
                    Text(
                        "Deep Fake (${deepFakeDetected.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        deepFakeDetected.forEach { result ->
                            DeepFakeCard(result = result)
                        }
                    }
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
fun ThreatStatisticsCard(statistics: Map<String, Int>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Threat Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            statistics.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        key.replace("_", " ").uppercase(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        value.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (value > 0) Color(0xFFEF5350) else Color(0xFF66BB6A)
                    )
                }
            }
        }
    }
}

@Composable
fun ThreatCardAdvanced(threat: AIToolThreat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                threat.severity >= 8 -> Color(0xFFEF5350).copy(alpha = 0.15f)
                threat.severity >= 6 -> Color(0xFFFFA726).copy(alpha = 0.15f)
                else -> MaterialTheme.colorScheme.surfaceVariant
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
                        threat.threatType,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            threat.severity >= 8 -> Color(0xFFEF5350)
                            threat.severity >= 6 -> Color(0xFFFFA726)
                            else -> MaterialTheme.colorScheme.onBackground
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        threat.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                threat.severity >= 8 -> Color(0xFFEF5350)
                                threat.severity >= 6 -> Color(0xFFFFA726)
                                else -> Color(0xFFFFB74D)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            threat.severity.toString(),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        Text(
                            "/10",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            DetailRow("Detection Method", threat.detectionMethod)
            DetailRow("Risk Score", "${(threat.riskScore * 100).toInt()}%")
            DetailRow("Source", threat.source)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Detected: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date(threat.timestamp))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PromptInjectionCard(injection: PromptInjectionDetection) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B6B).copy(alpha = 0.15f)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Prompt Injection Attack",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF5350)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "입력 값 검증 실패",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFA726)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF5350)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        injection.severity,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            DetailRow("Detection Pattern", injection.injectionPattern.take(30) + "...")
            DetailRow("Severity", injection.severity)
            DetailRow(
                "Status",
                if (injection.blockAction) "✓ BLOCKED" else "DETECTED"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "차단 이유",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "의도하지 않은 명령 또는 쿼리 구조가 감지되었습니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ModelExtractionCard(attack: ModelExtractionThreat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF9C27B0).copy(alpha = 0.15f)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Model Extraction Attack",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9C27B0)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "모델 탈취 시도 감지",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFA726)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF9C27B0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${(attack.successRate * 100).toInt()}%",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            DetailRow("Target Model", attack.targetModel)
            DetailRow("Attack Method", attack.extractionMethod)
            DetailRow("Query Count", attack.queriesUsed.toString())
            DetailRow("Success Rate", "${(attack.successRate * 100).toInt()}%")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "대응 방안",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "모델 접근 권한 재검토 및 제한된 API 쿼리 설정 필요",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF66BB6A),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF66BB6A).copy(alpha = 0.15f))
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ZeroDayCard(threat: ZeroDayThreat) {
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
            // 헤더: 취약점 이름 + 심각도
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        threat.unknownVulnerability,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF5350)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Unknown Vulnerability",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFA726)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF5350)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "${threat.severity}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        Text(
                            "/10",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 상세 정보
            DetailRow("Vulnerability ID", threat.unknownVulnerability)
            DetailRow("Exploit Signature", threat.exploitSignature)
            DetailRow("Target Component", threat.targetComponent)

            Spacer(modifier = Modifier.height(8.dp))

            // 영향도
            Text(
                "Impact Assessment",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp)
            ) {
                Text(
                    "• 시스템 안정성 손상 가능성",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFEF5350)
                )
                Text(
                    "• 권한 상승 위험",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFEF5350)
                )
                Text(
                    "• 데이터 유출 위험",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFEF5350)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 대응 방안
            Text(
                "Mitigation Strategy",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                threat.mitigationStrategy,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF66BB6A),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF66BB6A).copy(alpha = 0.15f))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 발견 시간
            Text(
                "Detected: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date(threat.timestamp))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun DeepFakeCard(result: DeepFakeAnalysisResult) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Deep Fake Detected",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF5350)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "비정상 미디어 감지됨",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFA726)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF5350)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${(result.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            DetailRow("Media Type", result.mediaType)
            DetailRow("Confidence", "${(result.confidence * 100).toInt()}%")
            val anomalyStatus = if (result.biometricAnomaly > 0.5f) "감지됨 (${(result.biometricAnomaly * 100).toInt()}%)" else "정상"
            DetailRow("Biometric Anomaly", anomalyStatus)

            if (result.artifacts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "분석 결과 (${result.artifacts.size}개)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {
                    result.artifacts.take(3).forEach { artifact ->
                        Text(
                            "• $artifact",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFA726)
                        )
                    }
                    if (result.artifacts.size > 3) {
                        Text(
                            "... +${result.artifacts.size - 3}개 더보기",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "권장 조치",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "이 미디어 파일의 진위 여부를 수동으로 검증하시기 바랍니다",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF66BB6A),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF66BB6A).copy(alpha = 0.15f))
                    .padding(8.dp)
            )
        }
    }
}
