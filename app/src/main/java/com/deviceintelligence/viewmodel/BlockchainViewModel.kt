package com.deviceintelligence.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deviceintelligence.data.model.BlockchainRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockchainViewModel @Inject constructor() : ViewModel() {

    private val _blocks = MutableLiveData(listOf(
        BlockchainRecord(
            blockIndex = 0, previousHash = "0000000000000000",
            currentHash = "abc123def456", timestamp = System.currentTimeMillis(),
            securityScore = 95, threatCount = 0, deviceId = "SM-F958N",
            cloudSynced = true, merkleRoot = "genesis", nonce = 0
        )
    ))
    val blocks: LiveData<List<BlockchainRecord>> = _blocks

    private val _metrics = MutableLiveData(
        com.deviceintelligence.data.model.BlockchainMetrics(
            totalBlocks = 1, chainIntegrity = 0.98f, averageScore = 85f,
            lastBlockTime = System.currentTimeMillis(), cloudSyncStatus = true
        )
    )
    val metrics: LiveData<com.deviceintelligence.data.model.BlockchainMetrics> = _metrics

    private val _chainIntegrity = MutableLiveData(true)
    val chainIntegrity: LiveData<Boolean> = _chainIntegrity

    private val _syncProgress = MutableLiveData(0f)
    val syncProgress: LiveData<Float> = _syncProgress

    private val _blockCount = MutableLiveData(1)
    val blockCount: LiveData<Int> = _blockCount

    private val _averageScore = MutableLiveData(85f)
    val averageScore: LiveData<Float> = _averageScore

    fun recordSecurityScore(score: Int, threatCount: Int) {
        viewModelScope.launch {
            val newBlock = BlockchainRecord(
                blockIndex = (blocks.value?.size ?: 0),
                previousHash = blocks.value?.lastOrNull()?.currentHash ?: "0000",
                currentHash = "hash_${System.currentTimeMillis()}",
                timestamp = System.currentTimeMillis(),
                securityScore = score,
                threatCount = threatCount,
                deviceId = "SM-F958N",
                cloudSynced = false,
                merkleRoot = "root_${System.currentTimeMillis()}",
                nonce = System.nanoTime()
            )
            val updated = (blocks.value ?: emptyList()) + newBlock
            _blocks.value = updated
            _blockCount.value = updated.size
        }
    }

    fun verifyChainIntegrity() {
        viewModelScope.launch {
            _chainIntegrity.value = true
        }
    }

    fun syncToNetwork() {
        viewModelScope.launch {
            _syncProgress.value = 0.2f
            _syncProgress.value = 0.8f
            _syncProgress.value = 1.0f
        }
    }

    fun getLatestBlock(): BlockchainRecord? {
        return blocks.value?.lastOrNull()
    }

    fun getBlockchainStatistics(): Map<String, Any> {
        val blockList = blocks.value ?: emptyList()
        return mapOf(
            "totalBlocks" to blockList.size,
            "averageScore" to (blockList.map { it.securityScore }.average()),
            "highestScore" to (blockList.maxOfOrNull { it.securityScore } ?: 0),
            "lowestScore" to (blockList.minOfOrNull { it.securityScore } ?: 0),
            "totalThreats" to blockList.sumOf { it.threatCount },
            "chainIntegrity" to (_chainIntegrity.value ?: true)
        )
    }

    fun getScoreTrend(): List<Int> {
        return blocks.value?.takeLast(10)?.map { it.securityScore } ?: emptyList()
    }

    fun enableAutoResponse(enable: Boolean) {
        // No-op
    }

    fun updateThreatThreshold(threshold: Int) {
        // No-op
    }

    fun getSmartContractState() = com.deviceintelligence.data.model.SmartContractState(
        contractId = "optira_v1",
        threatThreshold = 5,
        autoResponseEnabled = true,
        isolationMode = false,
        alertLevel = "MONITOR"
    )
}
