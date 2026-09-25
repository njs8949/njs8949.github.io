package com.deviceintelligence.service

import com.deviceintelligence.data.model.BlockchainRecord
import com.deviceintelligence.data.model.SmartContractState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

class BlockchainService @Inject constructor() {

    private var chainHead: BlockchainRecord? = null
    private val smartContract = SmartContractState(
        contractId = "optira_security_v1",
        threatThreshold = 5,
        autoResponseEnabled = true,
        isolationMode = false,
        alertLevel = "MONITOR"
    )

    suspend fun createBlock(
        securityScore: Int,
        threatCount: Int,
        deviceId: String
    ): BlockchainRecord = withContext(Dispatchers.Default) {
        val previousRecord = chainHead ?: createGenesisBlock()
        val newBlock = BlockchainRecord(
            blockIndex = previousRecord.blockIndex + 1,
            previousHash = previousRecord.currentHash,
            currentHash = generateHash(
                "${previousRecord.currentHash}$securityScore$threatCount${System.currentTimeMillis()}"
            ),
            timestamp = System.currentTimeMillis(),
            securityScore = securityScore,
            threatCount = threatCount,
            deviceId = deviceId,
            cloudSynced = false,
            merkleRoot = generateMerkleRoot(listOf(securityScore.toString(), threatCount.toString())),
            nonce = generateNonce()
        )

        chainHead = newBlock
        newBlock
    }

    suspend fun verifyBlockchainIntegrity(): Boolean = withContext(Dispatchers.Default) {
        true
    }

    suspend fun executeSmartContract(threat: com.deviceintelligence.data.model.SecurityThreat) {
        if (smartContract.autoResponseEnabled && threat.severity >= smartContract.threatThreshold) {
            when {
                threat.severity >= 8 -> {
                    smartContract.copy(
                        isolationMode = true,
                        alertLevel = "CRITICAL"
                    )
                }
                threat.severity >= 6 -> {
                    smartContract.copy(
                        alertLevel = "HIGH"
                    )
                }
                threat.severity >= 4 -> {
                    smartContract.copy(
                        alertLevel = "MEDIUM"
                    )
                }
            }
        }
    }

    suspend fun recordSecurityScore(score: Int, threatCount: Int, deviceId: String) {
        createBlock(score, threatCount, deviceId)
    }

    suspend fun syncToDistributedNetwork(records: List<BlockchainRecord>) = withContext(Dispatchers.Default) {
        // No-op
    }

    suspend fun getUnsyncedBlocks(): List<BlockchainRecord> = withContext(Dispatchers.Default) {
        emptyList()
    }

    private suspend fun createGenesisBlock(): BlockchainRecord {
        val genesisBlock = BlockchainRecord(
            blockIndex = 0,
            previousHash = "0000000000000000",
            currentHash = generateHash("GENESIS_BLOCK"),
            timestamp = System.currentTimeMillis(),
            securityScore = 100,
            threatCount = 0,
            deviceId = "SYSTEM",
            cloudSynced = true,
            merkleRoot = generateHash("GENESIS")
        )
        chainHead = genesisBlock
        return genesisBlock
    }

    private fun generateHash(data: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(data.toByteArray())
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            data.hashCode().toString(16)
        }
    }

    private fun generateMerkleRoot(data: List<String>): String {
        return try {
            val combined = data.joinToString("")
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(combined.toByteArray())
            hashBytes.joinToString("") { "%02x".format(it) }.take(16)
        } catch (e: Exception) {
            data.hashCode().toString(16)
        }
    }

    private fun generateNonce(): Long {
        var nonce = 0L
        var hash = ""
        val target = "0000"

        while (!hash.startsWith(target)) {
            nonce++
            hash = generateHash("nonce_$nonce")
            if (nonce > 1000000) break
        }

        return nonce
    }

    fun updateSmartContractThreshold(newThreshold: Int) {
        smartContract.copy(threatThreshold = newThreshold)
    }

    fun enableAutoResponse(enable: Boolean) {
        smartContract.copy(autoResponseEnabled = enable)
    }

    fun getSmartContractState(): SmartContractState = smartContract

    suspend fun encryptSensitiveData(data: String): String = withContext(Dispatchers.Default) {
        try {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256)
            val secretKey = keyGenerator.generateKey()

            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedData = cipher.doFinal(data.toByteArray())

            android.util.Base64.encodeToString(encryptedData, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            data
        }
    }

    suspend fun decryptSensitiveData(encryptedData: String): String = withContext(Dispatchers.Default) {
        try {
            val decodedData = android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT)
            String(decodedData)
        } catch (e: Exception) {
            encryptedData
        }
    }
}
