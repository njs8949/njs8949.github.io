package com.deviceintelligence.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deviceintelligence.data.model.RealtimeMonitoringData
import com.deviceintelligence.service.RealtimeMonitoringService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val monitoringService: RealtimeMonitoringService
) : ViewModel() {

    private val _realtimeData = MutableLiveData<RealtimeMonitoringData?>(null)
    val realtimeData: LiveData<RealtimeMonitoringData?> = _realtimeData

    private val _monitoringHistory = MutableLiveData<List<RealtimeMonitoringData>>(emptyList())
    val monitoringHistory: LiveData<List<RealtimeMonitoringData>> = _monitoringHistory

    private val _isMonitoring = MutableLiveData<Boolean>(false)
    val isMonitoring: LiveData<Boolean> = _isMonitoring

    private val _averageThreatScore = MutableLiveData<Float>(0f)
    val averageThreatScore: LiveData<Float> = _averageThreatScore

    private var monitoringJob: Job? = null

    fun startContinuousMonitoring(intervalMs: Long = 5000) {
        if (_isMonitoring.value == true) return

        _isMonitoring.value = true
        monitoringJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val data = monitoringService.collectRealtimeData()
                    _realtimeData.postValue(data)

                    val history = (_monitoringHistory.value ?: emptyList()).toMutableList()
                    history.add(data)
                    if (history.size > 100) history.removeAt(0)
                    _monitoringHistory.postValue(history)

                    updateAverageThreatScore(history)

                    delay(intervalMs)
                } catch (e: Exception) {
                    // 에러 처리
                }
            }
        }
    }

    fun stopContinuousMonitoring() {
        _isMonitoring.value = false
        monitoringJob?.cancel()
        monitoringJob = null
    }

    fun collectSingleSnapshot() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val data = monitoringService.collectRealtimeData()
                _realtimeData.postValue(data)
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    private fun updateAverageThreatScore(history: List<RealtimeMonitoringData>) {
        if (history.isNotEmpty()) {
            val average = history.map { it.threatScore }.average().toFloat()
            _averageThreatScore.postValue(average)
        }
    }

    override fun onCleared() {
        stopContinuousMonitoring()
        super.onCleared()
    }
}
