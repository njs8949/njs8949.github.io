package com.deviceintelligence.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DeviceIntelligenceApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
