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
import androidx.hilt.navigation.compose.hiltViewModel
import com.deviceintelligence.utils.getCurrentStrings
import com.deviceintelligence.viewmodel.RuleManagementViewModel

@Composable
fun RuleManagementScreen(
    viewModel: RuleManagementViewModel = hiltViewModel()
) {
    val strings = getCurrentStrings()
    val context = LocalContext.current
    val allRules = viewModel.allRules.value ?: emptyList()
    val isLoading = viewModel.isLoading.value ?: false

    var showCreateDialog by remember { mutableStateOf(false) }
    var ruleName by remember { mutableStateOf("") }
    var rulePattern by remember { mutableStateOf("") }
    var ruleSeverity by remember { mutableStateOf("5") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("탐지 규칙 관리") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "규칙 추가")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 안내
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2196F3).copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            "커스텀 탐지 규칙을 추가하여 보안을 강화하세요",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 활성 규칙 통계
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
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                allRules.size.toString(),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "전체 규칙",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                allRules.count { it.enabled }.toString(),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF66BB6A)
                                )
                            )
                            Text(
                                "활성 규칙",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                allRules.count { !it.enabled }.toString(),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9E9E9E)
                                )
                            )
                            Text(
                                "비활성 규칙",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 규칙 목록
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(allRules.size) { index ->
                    val rule = allRules[index]
                    RuleCard(
                        rule = rule,
                        onToggle = {
                            viewModel.updateRuleStatus(rule.id, !rule.enabled)
                            Toast.makeText(
                                context,
                                if (!rule.enabled) "규칙이 활성화되었습니다" else "규칙이 비활성화되었습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDelete = {
                            viewModel.deleteRule(rule.id)
                            Toast.makeText(context, "규칙이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // 규칙 추가 다이얼로그
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("새 탐지 규칙") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = ruleName,
                        onValueChange = { ruleName = it },
                        label = { Text("규칙 이름") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = rulePattern,
                        onValueChange = { rulePattern = it },
                        label = { Text("패턴") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = ruleSeverity,
                        onValueChange = { ruleSeverity = it },
                        label = { Text("심각도 (1-10)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (ruleName.isNotBlank() && rulePattern.isNotBlank()) {
                            viewModel.createRule(
                                name = ruleName,
                                description = "커스텀 규칙",
                                pattern = rulePattern,
                                severity = ruleSeverity.toIntOrNull() ?: 5
                            )
                            ruleName = ""
                            rulePattern = ""
                            ruleSeverity = "5"
                            showCreateDialog = false
                            Toast.makeText(context, "규칙이 추가되었습니다", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                ) {
                    Text("추가")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
private fun RuleCard(
    rule: com.deviceintelligence.data.model.CustomDetectionRule,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (rule.enabled) {
                Color(0xFF66BB6A).copy(alpha = 0.1f)
            } else {
                Color(0xFF9E9E9E).copy(alpha = 0.1f)
            }
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            rule.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when {
                                        rule.severity >= 8 -> Color(0xFFEF5350)
                                        rule.severity >= 6 -> Color(0xFFFFA726)
                                        else -> Color(0xFFFFB74D)
                                    }
                                )
                                .padding(4.dp)
                        ) {
                            Text(
                                "${rule.severity}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "패턴: ${rule.pattern.take(40)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onToggle
                ) {
                    Icon(
                        if (rule.enabled) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                        contentDescription = null,
                        tint = if (rule.enabled) Color(0xFF66BB6A) else Color(0xFF9E9E9E),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "상태: ${if (rule.enabled) "활성" else "비활성"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
