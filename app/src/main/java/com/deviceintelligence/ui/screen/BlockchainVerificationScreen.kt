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
import com.deviceintelligence.viewmodel.BlockchainVerificationViewModel

@Composable
fun BlockchainVerificationScreen(
    viewModel: BlockchainVerificationViewModel = hiltViewModel()
) {
    val strings = getCurrentStrings()
    val chainIntegrityResult = viewModel.chainIntegrityResult.value
    val verificationResult = viewModel.verificationResult.value
    val smartContractStatus = viewModel.smartContractStatus.value
    val isVerifying = viewModel.isVerifying.value ?: false

    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.verifyChainIntegrity()
        viewModel.executeSmartContract(65f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("블록체인 검증") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 탭
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("체인 무결성") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("스마트 계약") }
                )
            }

            when (selectedTab) {
                0 -> ChainIntegrityContent(
                    result = chainIntegrityResult,
                    isVerifying = isVerifying,
                    onVerify = { viewModel.verifyChainIntegrity() }
                )
                1 -> SmartContractContent(
                    status = smartContractStatus ?: "",
                    onExecute = { viewModel.executeSmartContract(kotlin.random.Random.nextFloat() * 100f) }
                )
            }
        }
    }
}

@Composable
private fun ChainIntegrityContent(
    result: com.deviceintelligence.data.model.BlockchainVerificationResult?,
    isVerifying: Boolean,
    onVerify: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 체인 상태
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (result?.isValid == true) {
                        Color(0xFF66BB6A).copy(alpha = 0.15f)
                    } else {
                        Color(0xFFEF5350).copy(alpha = 0.15f)
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        if (result?.isValid == true) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (result?.isValid == true) Color(0xFF66BB6A) else Color(0xFFEF5350),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (result?.isValid == true) "체인 검증 완료" else "체인 변조 감지",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (result?.isValid == true) Color(0xFF66BB6A) else Color(0xFFEF5350)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "무결성: ${(result?.chainIntegrity?.times(100) ?: 0f).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 검증 세부사항
        if (result != null) {
            item {
                Text(
                    "검증 세부사항",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                            .padding(12.dp)
                    ) {
                        DetailItem("Block ID", result.blockId.take(20) + "...")
                        DetailItem("Hash", result.threatDataHash)
                        DetailItem(
                            "상태",
                            if (result.isValid) "✓ 유효함" else "✗ 무효함"
                        )
                        DetailItem("무결성", "${(result.chainIntegrity * 100).toInt()}%")
                        DetailItem(
                            "스마트 계약",
                            result.smartContractStatus
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 변조 감지
            if (result.detectedTampering.isNotEmpty()) {
                item {
                    Text(
                        "감지된 변조 (${result.detectedTampering.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF5350)
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(result.detectedTampering.size) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEF5350).copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = Color(0xFFEF5350),
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp)
                            )
                            Text(
                                result.detectedTampering[index],
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onVerify,
                    modifier = Modifier
                        .padding(16.dp),
                    enabled = !isVerifying
                ) {
                if (isVerifying) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isVerifying) "검증 중..." else "재검증")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SmartContractContent(
    status: String,
    onExecute: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                "현재 상태",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        status.contains("ACTIVE") -> Color(0xFF66BB6A).copy(alpha = 0.15f)
                        status.contains("TRIGGERED") -> Color(0xFFFFA726).copy(alpha = 0.15f)
                        status.contains("ALERT") -> Color(0xFFEF5350).copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    status.contains("ACTIVE") -> Color(0xFF66BB6A)
                                    status.contains("TRIGGERED") -> Color(0xFFFFA726)
                                    status.contains("ALERT") -> Color(0xFFEF5350)
                                    else -> Color(0xFF9E9E9E)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            when {
                                status.contains("ACTIVE") -> Icons.Default.CheckCircle
                                status.contains("TRIGGERED") -> Icons.Default.Notifications
                                else -> Icons.Default.Error
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "스마트 계약 상태",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            status,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                "기능",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        listOf(
            Triple("자동 격리", "높은 위협에서 자동 격리 활성화", true),
            Triple("접근 제한", "제한된 접근 모드 활성화", false),
            Triple("모니터링", "강화된 모니터링 활성화", true)
        ).forEach { (title, desc, enabled) ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                desc,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            if (enabled) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                            contentDescription = null,
                            tint = if (enabled) Color(0xFF66BB6A) else Color(0xFF9E9E9E),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onExecute,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("계약 실행")
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
