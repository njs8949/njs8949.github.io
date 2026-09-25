package com.deviceintelligence.data.model

data class SecurityThreat(
    val id: Int = 0,
    val threatType: String,
    val severity: Int,
    val threatName: String,
    val threatSource: String,
    val detectionTime: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false,
    val recommendation: String,
    val aiConfidence: Float
)

enum class ThreatType {
    MALWARE,
    PERMISSION_ABUSE,
    NETWORK_ANOMALY,
    UNAUTHORIZED_ACCESS,
    CRYPTO_THREAT,
    BEHAVIORAL_ANOMALY
}

data class ThreatMetrics(
    val totalThreats: Int,
    val resolvedThreats: Int,
    val activeThreats: Int,
    val threatScore: Float,
    val aiAccuracy: Float
)
