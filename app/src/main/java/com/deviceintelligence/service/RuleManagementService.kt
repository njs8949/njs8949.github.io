package com.deviceintelligence.service

import com.deviceintelligence.data.model.CustomDetectionRule
import javax.inject.Inject

class RuleManagementService @Inject constructor() {

    private val rules = mutableListOf<CustomDetectionRule>()

    init {
        initializeDefaultRules()
    }

    fun createRule(
        name: String,
        description: String,
        pattern: String,
        severity: Int
    ): CustomDetectionRule {
        val rule = CustomDetectionRule(
            id = "rule_${System.currentTimeMillis()}",
            name = name,
            description = description,
            pattern = pattern,
            severity = severity,
            enabled = true,
            createdAt = System.currentTimeMillis(),
            lastModified = System.currentTimeMillis()
        )
        rules.add(rule)
        return rule
    }

    fun updateRule(ruleId: String, enabled: Boolean): Boolean {
        val rule = rules.find { it.id == ruleId }
        return if (rule != null) {
            val index = rules.indexOf(rule)
            rules[index] = rule.copy(enabled = enabled, lastModified = System.currentTimeMillis())
            true
        } else {
            false
        }
    }

    fun deleteRule(ruleId: String): Boolean {
        return rules.removeIf { it.id == ruleId }
    }

    fun getAllRules(): List<CustomDetectionRule> = rules.toList()

    fun getEnabledRules(): List<CustomDetectionRule> = rules.filter { it.enabled }

    fun getRulesByPattern(pattern: String): List<CustomDetectionRule> {
        return rules.filter { it.pattern.contains(pattern, ignoreCase = true) }
    }

    private fun initializeDefaultRules() {
        rules.addAll(
            listOf(
                CustomDetectionRule(
                    id = "default_rule_1",
                    name = "수상한 프로세스 접근",
                    description = "시스템 프로세스에 대한 비정상 접근 감지",
                    pattern = "syscall:.*elevated.*",
                    severity = 8,
                    enabled = true,
                    createdAt = System.currentTimeMillis(),
                    lastModified = System.currentTimeMillis()
                ),
                CustomDetectionRule(
                    id = "default_rule_2",
                    name = "비정상 네트워크 활동",
                    description = "인증되지 않은 네트워크 연결 시도",
                    pattern = "network:unknown_host:.*",
                    severity = 7,
                    enabled = true,
                    createdAt = System.currentTimeMillis(),
                    lastModified = System.currentTimeMillis()
                ),
                CustomDetectionRule(
                    id = "default_rule_3",
                    name = "파일 수정 감시",
                    description = "시스템 파일의 비정상 수정",
                    pattern = "file:system:modified:.*",
                    severity = 9,
                    enabled = true,
                    createdAt = System.currentTimeMillis(),
                    lastModified = System.currentTimeMillis()
                )
            )
        )
    }
}
