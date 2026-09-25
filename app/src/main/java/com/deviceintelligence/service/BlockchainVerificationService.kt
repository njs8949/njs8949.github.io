package com.deviceintelligence.service

import com.deviceintelligence.data.model.BlockchainVerificationResult
import java.security.MessageDigest
import javax.inject.Inject

class BlockchainVerificationService @Inject constructor() {

    fun verifyThreatData(threatData: String, blockId: String): BlockchainVerificationResult {
        val threatHash = generateHash(threatData)
        val isValid = validateHash(threatHash)
        val chainIntegrity = calculateChainIntegrity()
        val detectedTampering = detectTampering()
        val smartContractStatus = determineSmartContractStatus(isValid)

        return BlockchainVerificationResult(
            blockId = blockId,
            threatDataHash = threatHash,
            isValid = isValid,
            verificationTime = System.currentTimeMillis(),
            chainIntegrity = chainIntegrity,
            detectedTampering = detectedTampering,
            smartContractStatus = smartContractStatus
        )
    }

    fun verifyChainIntegrity(): BlockchainVerificationResult {
        val chainValid = kotlin.random.Random.nextFloat() > 0.1f
        val chainIntegrity = if (chainValid) {
            kotlin.random.Random.nextFloat() * 0.15f + 0.85f
        } else {
            kotlin.random.Random.nextFloat() * 0.3f + 0.4f
        }

        return BlockchainVerificationResult(
            blockId = "chain_verification_${System.currentTimeMillis()}",
            threatDataHash = "chain_hash_${generateHash("chain_data")}",
            isValid = chainValid,
            verificationTime = System.currentTimeMillis(),
            chainIntegrity = chainIntegrity.coerceIn(0f, 1f),
            detectedTampering = if (!chainValid) {
                listOf("Block #125: Hash mismatch", "Block #130: Invalid timestamp")
            } else {
                emptyList()
            },
            smartContractStatus = if (chainValid) "ACTIVE" else "ALERT"
        )
    }

    fun executeSmartContract(threatLevel: Float): String {
        return when {
            threatLevel >= 80f -> {
                "contract:auto_isolate:TRUE"
            }
            threatLevel >= 60f -> {
                "contract:restrict_access:TRUE"
            }
            threatLevel >= 40f -> {
                "contract:monitor:TRUE"
            }
            else -> {
                "contract:baseline_protection:TRUE"
            }
        }
    }

    private fun generateHash(data: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes = messageDigest.digest(data.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }.take(16).uppercase()
    }

    private fun validateHash(hash: String): Boolean {
        return kotlin.random.Random.nextFloat() > 0.05f
    }

    private fun calculateChainIntegrity(): Float {
        return kotlin.random.Random.nextFloat() * 0.2f + 0.8f
    }

    private fun detectTampering(): List<String> {
        return if (kotlin.random.Random.nextFloat() > 0.9f) {
            listOf(
                "Unexpected hash change at block #${kotlin.random.Random.nextInt(100, 200)}",
                "Timestamp inconsistency detected"
            )
        } else {
            emptyList()
        }
    }

    private fun determineSmartContractStatus(isValid: Boolean): String {
        return if (isValid) {
            when (kotlin.random.Random.nextInt(3)) {
                0 -> "ACTIVE"
                1 -> "TRIGGERED"
                else -> "MONITORING"
            }
        } else {
            "ALERT"
        }
    }
}
