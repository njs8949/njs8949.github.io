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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deviceintelligence.data.model.*
import com.deviceintelligence.utils.getCurrentStrings
import com.deviceintelligence.viewmodel.SecurityViewModel
import com.deviceintelligence.viewmodel.AdvancedDefenseViewModel
import com.deviceintelligence.viewmodel.BlockchainViewModel

@Composable
fun IntegratedSecurityScreen(
    securityViewModel: SecurityViewModel = hiltViewModel(),
    advancedDefenseViewModel: AdvancedDefenseViewModel = hiltViewModel(),
    blockchainViewModel: BlockchainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val strings = getCurrentStrings()
    val selectedTab = remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 탭
        TabRow(selectedTabIndex = selectedTab.value) {
            Tab(
                selected = selectedTab.value == 0,
                onClick = { selectedTab.value = 0 },
                text = { Text("Traditional") }
            )
            Tab(
                selected = selectedTab.value == 1,
                onClick = { selectedTab.value = 1 },
                text = { Text("AI Defense") }
            )
            Tab(
                selected = selectedTab.value == 2,
                onClick = { selectedTab.value = 2 },
                text = { Text("Blockchain") }
            )
        }

        // 탭 콘텐츠
        when (selectedTab.value) {
            0 -> TraditionalSecurityContent(securityViewModel, strings, context)
            1 -> AIDefenseContent(advancedDefenseViewModel, strings, context)
            2 -> BlockchainSecurityContent(blockchainViewModel, strings, context)
        }
    }
}

@Composable
private fun TraditionalSecurityContent(
    viewModel: SecurityViewModel,
    strings: com.deviceintelligence.utils.AppStrings,
    context: android.content.Context
) {
    val threats = viewModel.threats.value ?: emptyList()
    val analysis = viewModel.analysis.value
    val isAnalyzing = viewModel.isAnalyzing.value ?: false
    val securityScore = viewModel.getSecurityScore()
    val threatSummary = viewModel.getThreatSummary()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Security Score Display
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = strings.securityLevel,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$securityScore",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = when {
                            securityScore >= 80 -> Color(0xFF66BB6A)
                            securityScore >= 60 -> Color(0xFFFFA726)
                            else -> Color(0xFFEF5350)
                        }
                    )
                    Text(
                        text = "/100",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // Threat Summary
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThreatSummaryCard(
                    title = strings.total,
                    count = threatSummary["total"] ?: 0,
                    color = Color(0xFF9C27B0),
                    modifier = Modifier.weight(1f)
                )
                ThreatSummaryCard(
                    title = strings.critical,
                    count = threatSummary["critical"] ?: 0,
                    color = Color(0xFFEF5350),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Analysis Button
        item {
            Button(
                onClick = { viewModel.performSecurityAnalysis() },
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
                Text(if (isAnalyzing) strings.analyzing else strings.scanNow)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // AI Analysis Result
        item {
            if (analysis != null) {
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
                            text = strings.aiAnalysis,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "${strings.riskLevel}: ${analysis!!.riskLevel}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "${strings.behaviorScore}: ${analysis!!.behaviorScore.toInt()}/100",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Active Threats
        item {
            Text(
                strings.activeThreats,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (threats.isEmpty()) {
            item {
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
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF66BB6A),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(strings.noThreatsDetected, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            item {
                Column {
                    threats.forEach { threat ->
                        ThreatCard(threat = threat, strings = strings) {
                            viewModel.resolveThreat(threat.id)
                            Toast.makeText(context, strings.markedAsResolved, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AIDefenseContent(
    viewModel: AdvancedDefenseViewModel,
    strings: com.deviceintelligence.utils.AppStrings,
    context: android.content.Context
) {
    val isAnalyzing = viewModel.isAnalyzing.value ?: false
    val overallScore = viewModel.overallThreatScore.value ?: 0f
    val aiToolThreats = viewModel.aiToolThreats.value ?: emptyList()
    val promptInjections = viewModel.promptInjectionDetections.value ?: emptyList()
    val modelExtraction = viewModel.modelExtractionThreats.value ?: emptyList()
    val zeroDayThreats = viewModel.zeroDayThreats.value ?: emptyList()
    val deepFakeResults = viewModel.deepFakeResults.value ?: emptyList()
    val statistics = viewModel.getThreatStatistics()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // AI Threat Level
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

        // Statistics
        item {
            ThreatStatisticsCard(statistics = statistics)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Analysis Button
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
                Text(if (isAnalyzing) "Analyzing..." else "Scan AI Threats")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Threats
        item {
            if (aiToolThreats.isNotEmpty()) {
                Text(
                    "AI Threats (${aiToolThreats.size})",
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

        // Prompt Injection
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

        // Model Extraction
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

        // Zero-Day
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

        // Deep Fake
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

@Composable
private fun BlockchainSecurityContent(
    viewModel: BlockchainViewModel,
    strings: com.deviceintelligence.utils.AppStrings,
    context: android.content.Context
) {
    val blocks = viewModel.blocks.value ?: emptyList()
    val chainIntegrity = viewModel.chainIntegrity.value ?: true
    val blockCount = viewModel.blockCount.value ?: 0
    val averageScore = viewModel.averageScore.value ?: 85f
    val syncProgress = viewModel.syncProgress.value ?: 0f
    val smartContractState = viewModel.getSmartContractState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = strings.chainHealth,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${averageScore.toInt()}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF0B84D3)
                    )
                    Text(
                        text = "/100",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = strings.blocks,
                    value = blockCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = strings.status,
                    value = if (chainIntegrity) strings.valid else strings.invalid,
                    color = if (chainIntegrity) Color(0xFF66BB6A) else Color(0xFFEF5350),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
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
                        strings.smartContract,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "${strings.autoResponse}: ${if (smartContractState.autoResponseEnabled) strings.enabled else strings.disabled}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "${strings.alertLevel}: ${smartContractState.alertLevel}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.verifyChainIntegrity()
                            Toast.makeText(context, strings.chainVerification, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(strings.verifyChain, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                strings.blockHistory,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Column {
                blocks.takeLast(10).forEach { block ->
                    BlockCard(block = block)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
