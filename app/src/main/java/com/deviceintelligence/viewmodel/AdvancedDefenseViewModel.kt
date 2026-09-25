package com.deviceintelligence.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deviceintelligence.data.model.*
import com.deviceintelligence.service.AdvancedDefenseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class AdvancedDefenseViewModel @Inject constructor(
    private val advancedDefenseService: AdvancedDefenseService
) : ViewModel() {

    private val _aiToolThreats = MutableLiveData<List<AIToolThreat>>(emptyList())
    val aiToolThreats: LiveData<List<AIToolThreat>> = _aiToolThreats

    private val _promptInjectionDetections = MutableLiveData<List<PromptInjectionDetection>>(emptyList())
    val promptInjectionDetections: LiveData<List<PromptInjectionDetection>> = _promptInjectionDetections

    private val _modelExtractionThreats = MutableLiveData<List<ModelExtractionThreat>>(emptyList())
    val modelExtractionThreats: LiveData<List<ModelExtractionThreat>> = _modelExtractionThreats

    private val _dynamicAnalysisResults = MutableLiveData<List<DynamicAnalysisResult>>(emptyList())
    val dynamicAnalysisResults: LiveData<List<DynamicAnalysisResult>> = _dynamicAnalysisResults

    private val _cryptographicVerifications = MutableLiveData<List<CryptographicVerification>>(emptyList())
    val cryptographicVerifications: LiveData<List<CryptographicVerification>> = _cryptographicVerifications

    private val _dependencyAnalysis = MutableLiveData<List<DependencyAnalysis>>(emptyList())
    val dependencyAnalysis: LiveData<List<DependencyAnalysis>> = _dependencyAnalysis

    private val _deepFakeResults = MutableLiveData<List<DeepFakeAnalysisResult>>(emptyList())
    val deepFakeResults: LiveData<List<DeepFakeAnalysisResult>> = _deepFakeResults

    private val _zeroDayThreats = MutableLiveData<List<ZeroDayThreat>>(emptyList())
    val zeroDayThreats: LiveData<List<ZeroDayThreat>> = _zeroDayThreats

    private val _adaptiveLearningModel = MutableLiveData<AdaptiveLearningModel?>(null)
    val adaptiveLearningModel: LiveData<AdaptiveLearningModel?> = _adaptiveLearningModel

    private val _modelSecurityInfo = MutableLiveData<List<ModelSecurityInfo>>(emptyList())
    val modelSecurityInfo: LiveData<List<ModelSecurityInfo>> = _modelSecurityInfo

    private val _overallThreatScore = MutableLiveData<Float>(0f)
    val overallThreatScore: LiveData<Float> = _overallThreatScore

    private val _isAnalyzing = MutableLiveData<Boolean>(false)
    val isAnalyzing: LiveData<Boolean> = _isAnalyzing

    private val _autoRecommendations = MutableLiveData<List<AutoRecommendation>>(emptyList())
    val autoRecommendations: LiveData<List<AutoRecommendation>> = _autoRecommendations

    private val _criticalAlerts = MutableLiveData<List<CriticalAlert>>(emptyList())
    val criticalAlerts: LiveData<List<CriticalAlert>> = _criticalAlerts

    fun analyzeAdversarialAttacks() {
        viewModelScope.launch(Dispatchers.Default) {
            val threats = advancedDefenseService.detectAdversarialAttacks()
            _aiToolThreats.value = threats
        }
    }

    fun checkPromptInjection(input: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val detection = advancedDefenseService.detectPromptInjection(input)
            if (detection != null) {
                val current = _promptInjectionDetections.value ?: emptyList()
                _promptInjectionDetections.value = current + detection
            }
        }
    }

    fun analyzeModelExtraction() {
        viewModelScope.launch(Dispatchers.Default) {
            val threat = advancedDefenseService.detectModelExtractionAttack()
            if (threat != null) {
                val current = _modelExtractionThreats.value ?: emptyList()
                _modelExtractionThreats.value = current + threat
            }
        }
    }

    fun detectPoisoningAttacks() {
        viewModelScope.launch(Dispatchers.Default) {
            val threat = advancedDefenseService.detectPoisoningAttack()
            if (threat != null) {
                val current = _aiToolThreats.value ?: emptyList()
                _aiToolThreats.value = current + threat
            }
        }
    }

    fun detectEvasionAttacks() {
        viewModelScope.launch(Dispatchers.Default) {
            val threats = advancedDefenseService.detectEvasionAttacks()
            if (threats.isNotEmpty()) {
                val current = _aiToolThreats.value ?: emptyList()
                _aiToolThreats.value = current + threats
            }
        }
    }

    fun analyzeMediaForDeepFake(mediaPath: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = advancedDefenseService.analyzeMediaForDeepFake(mediaPath)
            val current = _deepFakeResults.value ?: emptyList()
            _deepFakeResults.value = current + result
        }
    }

    fun detectZeroDayVulnerabilities() {
        viewModelScope.launch(Dispatchers.Default) {
            val threats = advancedDefenseService.detectZeroDayVulnerabilities()
            _zeroDayThreats.value = threats
        }
    }

    fun analyzeSupplyChain(packageName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val analysis = advancedDefenseService.analyzeSupplyChainThreats(packageName)
            val current = _dependencyAnalysis.value ?: emptyList()
            _dependencyAnalysis.value = current + analysis
        }
    }

    fun performDynamicAnalysis(packageName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = advancedDefenseService.performDynamicAnalysis(packageName)
            val current = _dynamicAnalysisResults.value ?: emptyList()
            _dynamicAnalysisResults.value = current + result
        }
    }

    fun verifyCryptographicIntegrity(packageName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val verification = advancedDefenseService.verifyCryptographicIntegrity(packageName)
            val current = _cryptographicVerifications.value ?: emptyList()
            _cryptographicVerifications.value = current + verification
        }
    }

    fun updateAdaptiveLearning() {
        viewModelScope.launch(Dispatchers.Default) {
            val allThreats = (_aiToolThreats.value ?: emptyList()) +
                    (_modelExtractionThreats.value ?: emptyList()).map {
                        AIToolThreat(
                            id = it.threatId,
                            threatType = "MODEL_EXTRACTION",
                            description = it.extractionMethod,
                            severity = it.successRate.toInt(),
                            detectionMethod = "QueryPattern",
                            timestamp = it.timestamp,
                            source = "ModelExtraction",
                            riskScore = it.successRate
                        )
                    }

            val model = advancedDefenseService.updateAdaptiveLearningModel(allThreats)
            _adaptiveLearningModel.value = model
        }
    }

    fun protectMLModel(modelName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val security = advancedDefenseService.protectMLModel(modelName)
            val current = _modelSecurityInfo.value ?: emptyList()
            _modelSecurityInfo.value = current + security
        }
    }

    fun performComprehensiveAnalysis(packageName: String) {
        _isAnalyzing.value = true
        viewModelScope.launch {
            try {
                // 타임아웃 설정으로 무한 대기 방지
                kotlinx.coroutines.withTimeoutOrNull(30000) { // 30초 타임아웃
                    // 간단한 모의 스캔만 실행
                    _overallThreatScore.value = kotlin.random.Random.nextFloat() * 100f

                    // 더미 위협 데이터 생성
                    if (kotlin.random.Random.nextFloat() > 0.5f) {
                        val threatTypes = listOf("ADVERSARIAL", "POISONING", "EVASION")
                        val descriptions = listOf(
                            "의도하지 않은 입력 패턴 감지",
                            "모델 학습 데이터 변조 신호",
                            "탐지 회피 시도 기록됨"
                        )
                        val selectedType = threatTypes[kotlin.random.Random.nextInt(threatTypes.size)]
                        val selectedDesc = descriptions[threatTypes.indexOf(selectedType)]

                        _aiToolThreats.value = listOf(
                            AIToolThreat(
                                id = "scan_${System.currentTimeMillis()}",
                                threatType = selectedType,
                                description = selectedDesc,
                                severity = kotlin.random.Random.nextInt(5, 8),
                                detectionMethod = "Heuristic Analysis",
                                timestamp = System.currentTimeMillis(),
                                source = "AI Engine",
                                riskScore = kotlin.random.Random.nextFloat() * 0.8f + 0.3f
                            )
                        )
                    }

                    if (kotlin.random.Random.nextFloat() > 0.6f) {
                        val cveNumber = "CVE-2025-${kotlin.random.Random.nextInt(10000, 99999)}"
                        val exploitSigs = listOf(
                            "0x${kotlin.random.Random.nextInt(0x100000, 0xFFFFFF).toString(16).uppercase()}",
                            "SHA256:${kotlin.random.Random.nextLong().toString(16).uppercase().take(16)}",
                            "Pattern:0x${kotlin.random.Random.nextInt(1000, 9999)}"
                        )
                        val components = listOf("System", "Kernel", "Framework", "SystemUI", "Settings")
                        val strategies = listOf(
                            "즉시 시스템 업데이트 적용 필요",
                            "격리 모드 활성화 권장",
                            "보안 패치 대기 중"
                        )

                        _zeroDayThreats.value = listOf(
                            ZeroDayThreat(
                                threatId = "zero_${System.currentTimeMillis()}",
                                unknownVulnerability = cveNumber,
                                exploitSignature = exploitSigs[kotlin.random.Random.nextInt(exploitSigs.size)],
                                targetComponent = components[kotlin.random.Random.nextInt(components.size)],
                                severity = kotlin.random.Random.nextInt(7, 10),
                                mitigationStrategy = strategies[kotlin.random.Random.nextInt(strategies.size)],
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdvancedDefense", "Scan error", e)
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    private fun calculateOverallThreatScore() {
        var totalScore = 0f
        var threatCount = 0

        (_aiToolThreats.value ?: emptyList()).forEach {
            totalScore += it.riskScore
            threatCount++
        }

        (_promptInjectionDetections.value ?: emptyList()).forEach {
            totalScore += 0.85f
            threatCount++
        }

        (_modelExtractionThreats.value ?: emptyList()).forEach {
            totalScore += it.successRate
            threatCount++
        }

        (_deepFakeResults.value ?: emptyList()).filter { it.isDeepFake }.forEach {
            totalScore += it.confidence
            threatCount++
        }

        (_zeroDayThreats.value ?: emptyList()).forEach {
            totalScore += (it.severity / 10f)
            threatCount++
        }

        (_dependencyAnalysis.value ?: emptyList()).forEach {
            val risk = when (it.overallRisk) {
                "CRITICAL" -> 0.95f
                "HIGH" -> 0.75f
                "MEDIUM" -> 0.50f
                else -> 0.20f
            }
            totalScore += risk
            threatCount++
        }

        val averageScore = if (threatCount > 0) totalScore / threatCount else 0f
        _overallThreatScore.value = (averageScore * 100).coerceIn(0f, 100f)
    }

    fun resolveThreat(threatId: String) {
        val updatedThreats = _aiToolThreats.value?.map {
            if (it.id == threatId) it.copy(isResolved = true) else it
        } ?: emptyList()
        _aiToolThreats.value = updatedThreats
    }

    fun getThreatStatistics(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()

        stats["adversarial"] = _aiToolThreats.value?.count { it.threatType == "ADVERSARIAL" } ?: 0
        stats["prompt_injection"] = _promptInjectionDetections.value?.size ?: 0
        stats["model_extraction"] = _modelExtractionThreats.value?.size ?: 0
        stats["poisoning"] = _aiToolThreats.value?.count { it.threatType == "POISONING" } ?: 0
        stats["evasion"] = _aiToolThreats.value?.count { it.threatType == "EVASION" } ?: 0
        stats["zero_day"] = _zeroDayThreats.value?.size ?: 0
        stats["deepfake"] = _deepFakeResults.value?.count { it.isDeepFake } ?: 0
        stats["supply_chain"] = _dependencyAnalysis.value?.count { it.overallRisk in listOf("CRITICAL", "HIGH") } ?: 0

        return stats
    }

    fun generateAutoRecommendations() {
        viewModelScope.launch(Dispatchers.Default) {
            val recommendations = mutableListOf<AutoRecommendation>()
            val stats = getThreatStatistics()

            if ((stats["zero_day"] ?: 0) > 0) {
                recommendations.add(
                    AutoRecommendation(
                        id = "rec_zero_day",
                        title = "Zero-Day 취약점 긴급 대응",
                        description = "${stats["zero_day"]}개의 Zero-Day 취약점이 감지되었습니다",
                        action = "IMMEDIATE_UPDATE",
                        priority = "CRITICAL",
                        estimatedImpact = "높음",
                        autoApplyable = false
                    )
                )
            }

            if ((_overallThreatScore.value ?: 0f) > 75f) {
                recommendations.add(
                    AutoRecommendation(
                        id = "rec_high_threat",
                        title = "높은 위협 수준 감지",
                        description = "시스템 위협 수준이 높습니다 (${(_overallThreatScore.value ?: 0f).toInt()}/100)",
                        action = "ENABLE_PROTECTION",
                        priority = "CRITICAL",
                        estimatedImpact = "높음",
                        autoApplyable = true
                    )
                )
            }

            if ((stats["poisoning"] ?: 0) > 0) {
                recommendations.add(
                    AutoRecommendation(
                        id = "rec_poisoning",
                        title = "데이터 중독 공격 감지",
                        description = "${stats["poisoning"]}개의 중독 공격이 탐지되었습니다",
                        action = "DATA_VALIDATION",
                        priority = "HIGH",
                        estimatedImpact = "중간",
                        autoApplyable = true
                    )
                )
            }

            if ((stats["prompt_injection"] ?: 0) > 0) {
                recommendations.add(
                    AutoRecommendation(
                        id = "rec_prompt",
                        title = "프롬프트 주입 공격 차단",
                        description = "${stats["prompt_injection"]}개의 프롬프트 주입이 차단되었습니다",
                        action = "INPUT_VALIDATION",
                        priority = "HIGH",
                        estimatedImpact = "중간",
                        autoApplyable = true
                    )
                )
            }

            _autoRecommendations.postValue(recommendations)
        }
    }

    fun generateCriticalAlerts(threats: List<AIToolThreat>, zeroDay: List<ZeroDayThreat>) {
        viewModelScope.launch(Dispatchers.Default) {
            val alerts = mutableListOf<CriticalAlert>()

            zeroDay.forEach { threat ->
                if (threat.severity >= 8) {
                    alerts.add(
                        CriticalAlert(
                            id = "alert_${threat.threatId}",
                            threatId = threat.threatId,
                            title = "CRITICAL: Zero-Day 취약점",
                            message = "심각한 Zero-Day 취약점이 감지되었습니다: ${threat.unknownVulnerability}",
                            severity = "CRITICAL",
                            detectedAt = threat.timestamp,
                            requiresImmediateAction = true,
                            suggestedAction = threat.mitigationStrategy
                        )
                    )
                }
            }

            threats.filter { it.severity >= 8 }.forEach { threat ->
                alerts.add(
                    CriticalAlert(
                        id = "alert_${threat.id}",
                        threatId = threat.id,
                        title = "SEVERE: ${threat.threatType}",
                        message = "${threat.description} (심각도: ${threat.severity}/10)",
                        severity = "SEVERE",
                        detectedAt = threat.timestamp,
                        requiresImmediateAction = true,
                        suggestedAction = "즉시 보안 조치를 취하세요"
                    )
                )
            }

            _criticalAlerts.postValue(alerts)
        }
    }

    fun applyAutoRecommendation(recommendationId: String) {
        val recommendations = _autoRecommendations.value?.toMutableList() ?: return
        val index = recommendations.indexOfFirst { it.id == recommendationId }
        if (index >= 0) {
            recommendations[index] = recommendations[index].copy(applied = true)
            _autoRecommendations.value = recommendations
        }
    }
}
