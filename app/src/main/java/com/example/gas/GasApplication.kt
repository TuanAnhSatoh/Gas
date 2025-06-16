package com.example.gas

import android.app.Application
import com.example.gas.data.worker.WorkerScheduler
import com.example.gas.presentation.util.AuthUtil
import com.example.gas.presentation.util.NetworkMonitor
import com.example.gas.presentation.util.NotificationUtil
import com.example.gas.presentation.util.SessionManagerUtil
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class GasApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        AuthUtil.init(this)

        NotificationUtil.createAlertNotificationChannel(this)

        NetworkMonitor.init(applicationContext)

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        val userId = SessionManagerUtil.currentUserId
        if (userId != null) {
            WorkerScheduler.scheduleNetworkSyncWorker(this)
            NetworkMonitor.startMonitoring()
        } else {
            Timber.tag("AppStart").d("No user logged in -> Worker not scheduled")
        }
    }
}