package com.deviceintelligence.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deviceintelligence.data.model.BlockchainVerificationResult
import com.deviceintelligence.service.BlockchainVerificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockchainVerificationViewModel @Inject constructor(
    private val verificationService: BlockchainVerificationService
) : ViewModel() {

    private val _verificationResult = MutableLiveData<BlockchainVerificationResult?>(null)
    val verificationResult: LiveData<BlockchainVerificationResult?> = _verificationResult

    private val _chainIntegrityResult = MutableLiveData<BlockchainVerificationResult?>(null)
    val chainIntegrityResult: LiveData<BlockchainVerificationResult?> = _chainIntegrityResult

    private val _smartContractStatus = MutableLiveData<String>("")
    val smartContractStatus: LiveData<String> = _smartContractStatus

    private val _isVerifying = MutableLiveData<Boolean>(false)
    val isVerifying: LiveData<Boolean> = _isVerifying

    fun verifyThreatData(threatData: String, blockId: String) {
        _isVerifying.value = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = verificationService.verifyThreatData(threatData, blockId)
                _verificationResult.postValue(result)
            } finally {
                _isVerifying.postValue(false)
            }
        }
    }

    fun verifyChainIntegrity() {
        _isVerifying.value = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = verificationService.verifyChainIntegrity()
                _chainIntegrityResult.postValue(result)
            } finally {
                _isVerifying.postValue(false)
            }
        }
    }

    fun executeSmartContract(threatLevel: Float) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val status = verificationService.executeSmartContract(threatLevel)
                _smartContractStatus.postValue(status)
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }
}
