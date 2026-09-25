package com.deviceintelligence.data.model

data class SecurityEvent(
    val id: Int = 0,
    val eventType: String,
    val eventDescription: String,
    val appName: String,
    val packageName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val severity: Int,
    val actionTaken: String,
    val blockchainRecorded: Boolean = false
)

data class SecurityAnalysis(
    val behaviorScore: Float,
    val anomalyDetected: Boolean,
    val riskLevel: String,
    val recommendedActions: List<String>,
    val blockchainVerified: Boolean
)

data class SecurityDashboard(
    val overallSecurityScore: Float,
    val threats: List<SecurityThreat>,
    val recentEvents: List<SecurityEvent>,
    val aiRecommendations: List<String>,
    val blockchainStatus: BlockchainMetrics,
    val lastUpdate: Long = System.currentTimeMillis()
)
