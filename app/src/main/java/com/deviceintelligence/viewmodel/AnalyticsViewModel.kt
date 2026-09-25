package com.deviceintelligence.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deviceintelligence.data.model.*
import com.deviceintelligence.service.ThreatAnalyticsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsService: ThreatAnalyticsService
) : ViewModel() {

    private val _threatTrends = MutableLiveData<List<ThreatTrendPoint>>(emptyList())
    val threatTrends: LiveData<List<ThreatTrendPoint>> = _threatTrends

    private val _correlatedThreats = MutableLiveData<List<CorrelatedThreats>>(emptyList())
    val correlatedThreats: LiveData<List<CorrelatedThreats>> = _correlatedThreats

    private val _securityReport = MutableLiveData<SecurityReport?>(null)
    val securityReport: LiveData<SecurityReport?> = _securityReport

    private val _securityScore = MutableLiveData<SecurityScore?>(null)
    val securityScore: LiveData<SecurityScore?> = _securityScore

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun generateThreatTrends() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val trends = analyticsService.generateThreatTrends()
                _threatTrends.postValue(trends)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun analyzeCorrelatedThreats(threats: List<AIToolThreat>) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val correlated = analyticsService.identifyCorrelatedThreats(threats)
                _correlatedThreats.postValue(correlated)
            } catch (e: Exception) {
                _correlatedThreats.postValue(emptyList())
            }
        }
    }

    fun generateWeeklyReport(
        totalThreats: Int,
        resolvedThreats: Int,
        criticalCount: Int,
        highCount: Int,
        mediumCount: Int,
        lowCount: Int
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val report = analyticsService.generateSecurityReport(
                    period = "WEEKLY",
                    totalThreats = totalThreats,
                    resolvedThreats = resolvedThreats,
                    criticalCount = criticalCount,
                    highCount = highCount,
                    mediumCount = mediumCount,
                    lowCount = lowCount
                )
                _securityReport.postValue(report)
            } catch (e: Exception) {
                _securityReport.postValue(null)
            }
        }
    }

    fun generateMonthlyReport(
        totalThreats: Int,
        resolvedThreats: Int,
        criticalCount: Int,
        highCount: Int,
        mediumCount: Int,
        lowCount: Int
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val report = analyticsService.generateSecurityReport(
                    period = "MONTHLY",
                    totalThreats = totalThreats,
                    resolvedThreats = resolvedThreats,
                    criticalCount = criticalCount,
                    highCount = highCount,
                    mediumCount = mediumCount,
                    lowCount = lowCount
                )
                _securityReport.postValue(report)
            } catch (e: Exception) {
                _securityReport.postValue(null)
            }
        }
    }

    fun calculateSecurityScore(
        threatCount: Int,
        resolvedCount: Int,
        systemStability: Float = 85f,
        dataProtection: Float = 90f,
        networkSecurity: Float = 80f
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val score = analyticsService.calculateSecurityScore(
                    threatCount = threatCount,
                    resolvedCount = resolvedCount,
                    systemStability = systemStability,
                    dataProtection = dataProtection,
                    networkSecurity = networkSecurity
                )
                _securityScore.postValue(score)
            } catch (e: Exception) {
                _securityScore.postValue(null)
            }
        }
    }
}
