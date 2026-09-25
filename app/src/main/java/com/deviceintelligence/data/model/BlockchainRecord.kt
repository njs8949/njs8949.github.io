package com.deviceintelligence.data.model

data class BlockchainRecord(
    val id: Int = 0,
    val blockIndex: Int,
    val previousHash: String,
    val currentHash: String,
    val timestamp: Long = System.currentTimeMillis(),
    val securityScore: Int,
    val threatCount: Int,
    val deviceId: String,
    val cloudSynced: Boolean = false,
    val merkleRoot: String,
    val nonce: Long = 0
)

data class BlockchainMetrics(
    val totalBlocks: Int,
    val chainIntegrity: Float,
    val averageScore: Float,
    val lastBlockTime: Long,
    val cloudSyncStatus: Boolean
)

data class SmartContractState(
    val contractId: String,
    val threatThreshold: Int,
    val autoResponseEnabled: Boolean,
    val isolationMode: Boolean,
    val alertLevel: String
)
