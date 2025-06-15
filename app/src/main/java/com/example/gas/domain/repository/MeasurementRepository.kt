package com.example.gas.domain.repository

import com.example.gas.domain.model.Measurement
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {

    suspend fun createMeasurement(
        gas: Float,
    ): String

    suspend fun updateMeasurement(
        measurementId: String,
        gas: Float,
    )

    fun getMeasurementsRealtime(): Flow<List<Measurement>>

    fun getMeasurementsStream(): Flow<List<Measurement>>

    fun getMeasurementStream(measurementId: String): Flow<Measurement?>

    suspend fun getMeasurements(forceUpdate: Boolean): List<Measurement>

    suspend fun refresh()

    suspend fun getMeasurement(measurementId: String, forceUpdate: Boolean = true): Measurement?

    suspend fun refreshMeasurement(measurementId: String)

    suspend fun deleteAllMeasurements()

    suspend fun deleteMeasurement(measurementId: String)
}