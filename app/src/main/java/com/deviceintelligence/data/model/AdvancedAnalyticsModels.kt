package com.deviceintelligence.data.model

import java.util.Date

// 위협 히스토리
data class ThreatHistory(
    val id: String,
    val threatId: String,
    val threatType: String,
    val severity: Int,
    val status: String, // DETECTED, RESOLVED, IGNORED
    val detectedAt: Long,
    val resolvedAt: Long? = null,
    val action: String? = null
)

// 보안 보고서
data class SecurityReport(
    val id: String,
    val reportDate: Long,
    val period: String, // WEEKLY, MONTHLY
    val totalThreatsDetected: Int,
    val threatsResolved: Int,
    val criticalThreats: Int,
    val highThreats: Int,
    val mediumThreats: Int,
    val lowThreats: Int,
    val systemHealth: Float,
    val recommendations: List<String>,
    val summary: String
)

// 위협 분석 추이
data class ThreatTrendPoint(
    val timestamp: Long,
    val threatScore: Float,
    val threatCount: Int,
    val criticalCount: Int,
    val highCount: Int
)

// 연관 위협 그룹
data class CorrelatedThreats(
    val id: String,
    val primaryThreatId: String,
    val relatedThreatIds: List<String>,
    val correlationScore: Float,
    val description: String,
    val suggestedAction: String
)

// 자동 권장 사항
data class AutoRecommendation(
    val id: String,
    val title: String,
    val description: String,
    val action: String,
    val priority: String, // CRITICAL, HIGH, MEDIUM, LOW
    val estimatedImpact: String,
    val autoApplyable: Boolean,
    val applied: Boolean = false
)

// 커스텀 탐지 규칙
data class CustomDetectionRule(
    val id: String,
    val name: String,
    val description: String,
    val pattern: String,
    val severity: Int,
    val enabled: Boolean,
    val createdAt: Long,
    val lastModified: Long
)

// 실시간 모니터링 데이터
data class RealtimeMonitoringData(
    val timestamp: Long,
    val cpuUsage: Float,
    val memoryUsage: Float,
    val batteryDrain: Float,
    val networkActivity: Float,
    val processCount: Int,
    val suspiciousApps: Int,
    val threatScore: Float
)

// 긴급 알림
data class CriticalAlert(
    val id: String,
    val threatId: String,
    val title: String,
    val message: String,
    val severity: String, // CRITICAL, SEVERE
    val detectedAt: Long,
    val requiresImmediateAction: Boolean,
    val suggestedAction: String
)

// 블록체인 검증 결과
data class BlockchainVerificationResult(
    val blockId: String,
    val threatDataHash: String,
    val isValid: Boolean,
    val verificationTime: Long,
    val chainIntegrity: Float, // 0-1
    val detectedTampering: List<String>,
    val smartContractStatus: String // ACTIVE, TRIGGERED, RESOLVED
)

// 보안 스코어 계산
data class SecurityScore(
    val overallScore: Float, // 0-100
    val threatDetection: Float,
    val threatResponse: Float,
    val systemStability: Float,
    val dataProtection: Float,
    val networkSecurity: Float,
    val lastUpdated: Long
)

// 리소스 사용 최적화 힌트
data class ResourceOptimizationHint(
    val id: String,
    val category: String, // MEMORY, CPU, BATTERY, STORAGE
    val currentUsage: Float,
    val optimalUsage: Float,
    val savingPotential: Float,
    val recommendation: String,
    val priority: String
)
