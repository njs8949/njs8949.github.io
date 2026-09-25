package com.deviceintelligence.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deviceintelligence.data.model.CustomDetectionRule
import com.deviceintelligence.service.RuleManagementService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RuleManagementViewModel @Inject constructor(
    private val ruleService: RuleManagementService
) : ViewModel() {

    private val _allRules = MutableLiveData<List<CustomDetectionRule>>(emptyList())
    val allRules: LiveData<List<CustomDetectionRule>> = _allRules

    private val _enabledRules = MutableLiveData<List<CustomDetectionRule>>(emptyList())
    val enabledRules: LiveData<List<CustomDetectionRule>> = _enabledRules

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadAllRules()
    }

    fun loadAllRules() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val rules = ruleService.getAllRules()
                _allRules.postValue(rules)
                loadEnabledRules()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun loadEnabledRules() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val rules = ruleService.getEnabledRules()
                _enabledRules.postValue(rules)
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    fun createRule(
        name: String,
        description: String,
        pattern: String,
        severity: Int
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                ruleService.createRule(name, description, pattern, severity)
                loadAllRules()
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    fun updateRuleStatus(ruleId: String, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                ruleService.updateRule(ruleId, enabled)
                loadAllRules()
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    fun deleteRule(ruleId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                ruleService.deleteRule(ruleId)
                loadAllRules()
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    fun searchRulesByPattern(pattern: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val rules = ruleService.getRulesByPattern(pattern)
                _allRules.postValue(rules)
            } catch (e: Exception) {
                loadAllRules()
            }
        }
    }
}
