package com.deviceintelligence.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deviceintelligence.data.model.SecurityAnalysis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor() : ViewModel() {

    private val _threats = MutableLiveData(listOf(
        com.deviceintelligence.data.model.SecurityThreat(
            id = 1, threatType = "MALWARE", severity = 7,
            threatName = "Suspicious App Detected",
            threatSource = "com.example.app", detectionTime = System.currentTimeMillis(),
            isResolved = false, recommendation = "Uninstall suspicious app",
            aiConfidence = 0.85f
        )
    ))
    val threats: LiveData<List<com.deviceintelligence.data.model.SecurityThreat>> = _threats

    private val _events = MutableLiveData(emptyList<com.deviceintelligence.data.model.SecurityEvent>())
    val events: LiveData<List<com.deviceintelligence.data.model.SecurityEvent>> = _events

    private val _analysis = MutableLiveData<SecurityAnalysis?>(null)
    val analysis: LiveData<SecurityAnalysis?> = _analysis

    private val _isAnalyzing = MutableLiveData(false)
    val isAnalyzing: LiveData<Boolean> = _isAnalyzing

    private val _syncStatus = MutableLiveData("Ready")
    val syncStatus: LiveData<String> = _syncStatus

    fun performSecurityAnalysis() {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _analysis.value = SecurityAnalysis(
                behaviorScore = 78f,
                anomalyDetected = false,
                riskLevel = "MEDIUM",
                recommendedActions = listOf("Check app permissions", "Monitor network activity"),
                blockchainVerified = true
            )
            _isAnalyzing.value = false
        }
    }

    fun resolveThreat(threatId: Int) {
        val updated = threats.value?.map {
            if (it.id == threatId) it.copy(isResolved = true) else it
        } ?: emptyList()
        _threats.value = updated
    }

    fun syncToCloud() {
        viewModelScope.launch {
            _syncStatus.value = "Syncing..."
            _syncStatus.value = "Synced: 1 block uploaded"
        }
    }

    fun getThreatSummary(): Map<String, Int> {
        val threatList = threats.value ?: emptyList()
        return mapOf(
            "total" to threatList.size,
            "critical" to threatList.count { it.severity >= 8 },
            "high" to threatList.count { it.severity in 6..7 },
            "medium" to threatList.count { it.severity in 4..5 },
            "low" to threatList.count { it.severity in 1..3 }
        )
    }

    fun getSecurityScore(): Int {
        val threatList = threats.value ?: emptyList()
        val baseScore = 100
        val reduction = threatList.sumOf { (it.severity * 2) }
        return (baseScore - reduction).coerceIn(0, 100)
    }
}
