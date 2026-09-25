package com.deviceintelligence.data.model

// AI Tool 공격 위협 모델
data class AIToolThreat(
    val id: String,
    val threatType: String, // ADVERSARIAL, PROMPT_INJECTION, MODEL_EXTRACTION, POISONING, EVASION, DEEPFAKE, ZERO_DAY, SUPPLY_CHAIN
    val description: String,
    val severity: Int, // 1-10
    val detectionMethod: String,
    val timestamp: Long,
    val source: String,
    val riskScore: Float, // 0-1
    val isResolved: Boolean = false
)

// 동적 코드 분석 결과
data class DynamicAnalysisResult(
    val appName: String,
    val packageName: String,
    val memoryWrites: List<MemoryWrite>,
    val systemCalls: List<SystemCall>,
    val networkPackets: List<NetworkPacket>,
    val fileIOOperations: List<FileIO>,
    val suspiciousScore: Float, // 0-100
    val verdict: String // SAFE, SUSPICIOUS, MALICIOUS
)

data class MemoryWrite(
    val address: String,
    val size: Long,
    val content: String,
    val timestamp: Long,
    val isSuspicious: Boolean
)

data class SystemCall(
    val name: String,
    val arguments: List<String>,
    val returnValue: String,
    val timestamp: Long,
    val isSuspicious: Boolean
)

data class NetworkPacket(
    val sourceIP: String,
    val destIP: String,
    val protocol: String,
    val payload: String,
    val timestamp: Long,
    val isMalicious: Boolean
)

data class FileIO(
    val filePath: String,
    val operation: String, // READ, WRITE, DELETE
    val size: Long,
    val timestamp: Long,
    val isSuspicious: Boolean
)

// 암호화 기반 검증 결과
data class CryptographicVerification(
    val packageName: String,
    val apkHash: String, // SHA-256
    val signatureValid: Boolean,
    val certificateInfo: String,
    val binaryIntegrity: Boolean,
    val patchesVerified: List<PatchVerification>,
    val lastVerifiedTime: Long
)

data class PatchVerification(
    val patchId: String,
    val hash: String,
    val isValid: Boolean,
    val appliedTime: Long
)

// 자적응 학습 모델
data class AdaptiveLearningModel(
    val modelId: String,
    val version: Int,
    val threatPatterns: List<ThreatPattern>,
    val thresholds: ThreatThresholds,
    val lastUpdated: Long,
    val accuracy: Float // 0-1
)

data class ThreatPattern(
    val patternId: String,
    val description: String,
    val indicators: List<String>,
    val weight: Float,
    val detectionRate: Float
)

data class ThreatThresholds(
    val memoryWriteThreshold: Long,
    val systemCallThreshold: Int,
    val networkTrafficThreshold: Long,
    val fileIOThreshold: Int,
    val cpuUsageThreshold: Float,
    val batteryDrainThreshold: Float
)

// 모델 보호 정보
data class ModelSecurityInfo(
    val modelName: String,
    val isEncrypted: Boolean,
    val encryptionKey: String,
    val accessLog: List<ModelAccess>,
    val unauthorizedAttempts: Int,
    val lastModified: Long
)

data class ModelAccess(
    val appName: String,
    val packageName: String,
    val accessTime: Long,
    val accessType: String, // READ, WRITE
    val isAuthorized: Boolean
)

// Supply Chain 공격 감지
data class DependencyAnalysis(
    val packageName: String,
    val dependencies: List<Dependency>,
    val vulnerabilities: List<Vulnerability>,
    val suspiciousPackages: List<SuspiciousPackage>,
    val overallRisk: String // LOW, MEDIUM, HIGH, CRITICAL
)

data class Dependency(
    val name: String,
    val version: String,
    val verificationHash: String,
    val isVerified: Boolean,
    val source: String
)

data class Vulnerability(
    val cveId: String,
    val severity: String,
    val description: String,
    val fixAvailable: Boolean
)

data class SuspiciousPackage(
    val name: String,
    val reason: String,
    val riskScore: Float,
    val recommendation: String
)

// Deep Fake 감지 결과
data class DeepFakeAnalysisResult(
    val mediaPath: String,
    val mediaType: String, // IMAGE, VIDEO, AUDIO
    val isDeepFake: Boolean,
    val confidence: Float, // 0-1
    val artifacts: List<String>,
    val biometricAnomaly: Float,
    val timestamp: Long
)

// Zero-Day 공격 감지
data class ZeroDayThreat(
    val threatId: String,
    val unknownVulnerability: String,
    val exploitSignature: String,
    val targetComponent: String,
    val severity: Int, // 1-10
    val mitigationStrategy: String,
    val timestamp: Long
)

// 프롬프트 인젝션 감지
data class PromptInjectionDetection(
    val detectedPrompt: String,
    val injectionPattern: String,
    val targetModel: String,
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val blockAction: Boolean,
    val timestamp: Long
)

// 모델 추출 공격 감지
data class ModelExtractionThreat(
    val threatId: String,
    val targetModel: String,
    val extractionMethod: String,
    val queriesUsed: Int,
    val successRate: Float,
    val severity: String,
    val blockAction: Boolean,
    val timestamp: Long
)
