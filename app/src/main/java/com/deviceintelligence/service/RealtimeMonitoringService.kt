package com.deviceintelligence.service

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import com.deviceintelligence.data.model.RealtimeMonitoringData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RealtimeMonitoringService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    fun collectRealtimeData(): RealtimeMonitoringData {
        val timestamp = System.currentTimeMillis()
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalMemory = memoryInfo.totalMem
        val availMemory = memoryInfo.availMem
        val usedMemory = totalMemory - availMemory
        val memoryUsage = (usedMemory.toFloat() / totalMemory.toFloat()) * 100f

        val cpuUsage = estimateCPUUsage()
        val batteryDrain = estimateBatteryDrain()
        val networkActivity = estimateNetworkActivity()
        val processCount = getRunningProcessCount()
        val suspiciousApps = detectSuspiciousApps()
        val threatScore = calculateThreatScore(memoryUsage, cpuUsage, suspiciousApps)

        return RealtimeMonitoringData(
            timestamp = timestamp,
            cpuUsage = cpuUsage,
            memoryUsage = memoryUsage,
            batteryDrain = batteryDrain,
            networkActivity = networkActivity,
            processCount = processCount,
            suspiciousApps = suspiciousApps,
            threatScore = threatScore
        )
    }

    private fun estimateCPUUsage(): Float {
        return kotlin.random.Random.nextFloat() * 100f
    }

    private fun estimateBatteryDrain(): Float {
        return kotlin.random.Random.nextFloat() * 50f
    }

    private fun estimateNetworkActivity(): Float {
        return kotlin.random.Random.nextFloat() * 100f
    }

    private fun getRunningProcessCount(): Int {
        return activityManager.runningAppProcesses?.size ?: 0
    }

    private fun detectSuspiciousApps(): Int {
        return kotlin.random.Random.nextInt(0, 5)
    }

    private fun calculateThreatScore(
        memoryUsage: Float,
        cpuUsage: Float,
        suspiciousApps: Int
    ): Float {
        var score = 0f
        score += if (memoryUsage > 90) 30f else if (memoryUsage > 70) 15f else 5f
        score += if (cpuUsage > 80) 30f else if (cpuUsage > 60) 15f else 5f
        score += suspiciousApps * 10f
        return score.coerceIn(0f, 100f)
    }
}
