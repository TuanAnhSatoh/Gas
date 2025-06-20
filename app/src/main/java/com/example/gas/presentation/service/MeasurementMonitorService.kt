package com.example.gas.presentation.service

import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.gas.R
import com.example.gas.domain.repository.MeasurementRepository
import com.example.gas.domain.usecase.MeasurementAlertUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MeasurementMonitorService : LifecycleService() {

    @Inject
    lateinit var measurementRepository: MeasurementRepository
    @Inject
    lateinit var alertUseCase: MeasurementAlertUseCase

    override fun onCreate() {
        super.onCreate()

        if (!isUserLoggedIn()) {
            Timber.tag("MeasurementMonitorService").e("User not logged in, stopping service")
            stopSelf()
            return
        }

        startForegroundService()
        observeMeasurements()
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "alert_channel")
            .setContentTitle("Monitoring Measurements")
            .setSmallIcon(R.drawable.ic_gas)
            .build()
        startForeground(1, notification)
        Timber.tag("MeasurementMonitorService").d("Foreground service started")
    }

    private fun observeMeasurements() {
        lifecycleScope.launch {
            measurementRepository.getMeasurementsRealtime().collect { measurements ->
                measurements.forEach { measurement ->
                    alertUseCase.checkThresholdAndAlert(this@MeasurementMonitorService, measurement)
                    Timber.tag("MeasurementMonitorService").d("Measurement: ${measurement.measurementId}")
                }
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}