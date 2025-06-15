package com.example.gas.domain.usecase

import android.content.Context
import android.content.Intent
import com.example.gas.domain.model.Measurement
import com.example.gas.domain.repository.AlertRepository
import com.example.gas.domain.repository.EmergencyInfoRepository
import com.example.gas.presentation.receiver.CallAlertReceiver
import timber.log.Timber
import javax.inject.Inject

class MeasurementAlertUseCase @Inject constructor(
    private val emergencyRepository: EmergencyInfoRepository,
    private val alertRepository: AlertRepository,
    private val gasAnalysisUseCase: GasAnalysisUseCase,
) {
    suspend fun checkThresholdAndAlert(context: Context, measurement: Measurement) {
        Timber.tag("MeasurementAlertUseCase")
            .d("Checking thresholds for measurement: ${measurement.measurementId}")

        if (gasAnalysisUseCase.isAbnormal(measurement.gas)) {
            val emergencyInfos = emergencyRepository.getEmergencyInfos()
                .sortedBy { it.priority }

            val topPriorityEmergency = emergencyInfos.firstOrNull() ?: return

            alertRepository.createAlert(
                measurementId = measurement.measurementId,
                emergencyId = topPriorityEmergency.emergencyId,
                triggerReason = "Abnormal health indicators",
                contacted = false
            )

            if (!AlertThrottleManager.shouldTriggerAlert(context)) {
                Timber.tag("MeasurementAlertUseCase")
                    .d("Alert throttled - skipping alert for measurement: ${measurement.measurementId}")
                return
            }

            val intent = Intent(context, CallAlertReceiver::class.java).apply {
                action = "ACTION_CALL_ALERT"
                putExtra("phoneNumber", topPriorityEmergency.emergencyPhone)
                putExtra("title", "Health Alert")
                putExtra("message", "Abnormal health indicators! Calling ${topPriorityEmergency.emergencyName}")
            }

            context.sendBroadcast(intent)
        }
    }
}