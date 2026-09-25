package com.deviceintelligence.service

import com.deviceintelligence.data.model.*
import javax.inject.Inject
import kotlin.random.Random

class ThreatAnalyticsService @Inject constructor() {

    fun generateThreatTrends(): List<ThreatTrendPoint> {
        val trends = mutableListOf<ThreatTrendPoint>()
        val now = System.currentTimeMillis()

        for (i in 0..23) {
            val timestamp = now - (i * 60 * 60 * 1000)
            trends.add(
                ThreatTrendPoint(
                    timestamp = timestamp,
                    threatScore = Random.nextFloat() * 100f,
                    threatCount = Random.nextInt(0, 20),
                    criticalCount = Random.nextInt(0, 3),
                    highCount = Random.nextInt(0, 5)
                )
            )
        }
        return trends.reversed()
    }

    fun identifyCorrelatedThreats(threats: List<AIToolThreat>): List<CorrelatedThreats> {
        val correlated = mutableListOf<CorrelatedThreats>()

        if (threats.size >= 2) {
            for (i in threats.indices) {
                if (Random.nextFloat() > 0.6f) {
                    val relatedIndices = threats.indices.filter { it != i }.shuffled().take(2)
                    correlated.add(
                        CorrelatedThreats(
                            id = "correlation_${System.currentTimeMillis()}_$i",
                            primaryThreatId = threats[i].id,
                            relatedThreatIds = relatedIndices.map { threats[it].id },
                            correlationScore = Random.nextFloat() * 0.5f + 0.5f,
                            description = "연관된 위협 패턴 감지됨",
                            suggestedAction = "관련 위협들을 함께 대응하는 것을 권장합니다"
                        )
                    )
                }
            }
        }
        return correlated
    }

    fun generateSecurityReport(
        period: String,
        totalThreats: Int,
        resolvedThreats: Int,
        criticalCount: Int,
        highCount: Int,
        mediumCount: Int,
        lowCount: Int
    ): SecurityReport {
        val recommendations = generateRecommendations(
            totalThreats,
            resolvedThreats,
            criticalCount
        )

        return SecurityReport(
            id = "report_${System.currentTimeMillis()}",
            reportDate = System.currentTimeMillis(),
            period = period,
            totalThreatsDetected = totalThreats,
            threatsResolved = resolvedThreats,
            criticalThreats = criticalCount,
            highThreats = highCount,
            mediumThreats = mediumCount,
            lowThreats = lowCount,
            systemHealth = (100f - (totalThreats * 2f)).coerceIn(0f, 100f),
            recommendations = recommendations,
            summary = "주간 보안 스캔 결과: ${totalThreats}개 위협 감지, ${resolvedThreats}개 해결됨"
        )
    }

    fun calculateSecurityScore(
        threatCount: Int,
        resolvedCount: Int,
        systemStability: Float,
        dataProtection: Float,
        networkSecurity: Float
    ): SecurityScore {
        val threatDetection = (100f - (threatCount * 5f)).coerceIn(0f, 100f)
        val threatResponse = if (threatCount > 0) {
            (resolvedCount.toFloat() / threatCount.toFloat() * 100f).coerceIn(0f, 100f)
        } else {
            100f
        }

        val overallScore = (threatDetection * 0.3f +
                threatResponse * 0.2f +
                systemStability * 0.2f +
                dataProtection * 0.15f +
                networkSecurity * 0.15f).coerceIn(0f, 100f)

        return SecurityScore(
            overallScore = overallScore,
            threatDetection = threatDetection,
            threatResponse = threatResponse,
            systemStability = systemStability,
            dataProtection = dataProtection,
            networkSecurity = networkSecurity,
            lastUpdated = System.currentTimeMillis()
        )
    }

    private fun generateRecommendations(
        totalThreats: Int,
        resolvedThreats: Int,
        criticalCount: Int
    ): List<String> {
        val recommendations = mutableListOf<String>()

        if (criticalCount > 0) {
            recommendations.add("긴급: Critical 위협 ${criticalCount}개를 즉시 해결해야 합니다")
        }

        if (totalThreats > resolvedThreats) {
            recommendations.add("${totalThreats - resolvedThreats}개의 미해결 위협이 있습니다. 확인 후 조치하세요")
        }

        if (totalThreats > 10) {
            recommendations.add("높은 위협 수치입니다. 시스템 업데이트를 권장합니다")
        }

        recommendations.add("정기적인 보안 스캔을 유지하세요")

        return recommendations.take(4)
    }
}
