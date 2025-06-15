package com.example.gas.data.source.network.datasource

import com.example.gas.data.source.network.model.FirebaseMeasurement
import kotlinx.coroutines.flow.Flow

interface MeasurementDataSource {

    fun getMeasurementsFirebaseRealtime(userId: String): Flow<List<FirebaseMeasurement>>

    suspend fun loadMeasurements(userId: String): List<FirebaseMeasurement>

    suspend fun saveMeasurements(measurements: List<FirebaseMeasurement>)
}