package com.example.gas.data.repository

import com.example.gas.data.mapper.toExternal
import com.example.gas.data.mapper.toLocal
import com.example.gas.data.mapper.toNetwork
import com.example.gas.data.source.local.dao.MeasurementDao
import com.example.gas.data.source.network.datasource.AuthDataSource
import com.example.gas.data.source.network.datasource.MeasurementDataSource
import com.example.gas.di.ApplicationScope
import com.example.gas.di.DefaultDispatcher
import com.example.gas.domain.model.Measurement
import com.example.gas.domain.repository.MeasurementRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMeasurementRepository @Inject constructor(
    private val networkDataSource: MeasurementDataSource,
    private val localDataSource: MeasurementDao,
    private val authDataSource: AuthDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : MeasurementRepository {

    private val userId: String
        get() = authDataSource.getCurrentUserId() ?: throw Exception("User not logged in")

    override suspend fun createMeasurement(
        gas: Float,
    ): String {
        val measurementId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val measurement = Measurement(
            measurementId = measurementId,
            userId = userId,
            gas = gas,
            dateTime = LocalDateTime.now()
        )
        localDataSource.upsert(measurement.toLocal())
        saveMeasurementsToNetwork()
        return measurementId
    }

    override suspend fun updateMeasurement(
        measurementId: String,
        gas: Float,
    ) {
        val measurement = getMeasurement(measurementId)?.copy(
            gas = gas,
        ) ?: throw Exception("Measurement (id $measurementId) not found")

        localDataSource.upsert(measurement.toLocal())
        saveMeasurementsToNetwork()
    }

    override fun getMeasurementsRealtime(): Flow<List<Measurement>> {
        return networkDataSource.getMeasurementsFirebaseRealtime(userId)
            .map { firebaseMeasurements ->

                val localMeasurements = firebaseMeasurements.toLocal()

                val existingIds = withContext(dispatcher) {
                    localDataSource.getAll().map { it.measurementId }
                }

                val newMeasurements = localMeasurements.filter { it.measurementId !in existingIds }

                if (newMeasurements.isNotEmpty()) {
                    scope.launch {
                        try {
                            localDataSource.upsertAll(newMeasurements)
                        } catch (e: Exception) {
                            Timber.tag("DefaultMeasurementRepository")
                                .e(e, "Failed to upsert new measurements")
                        }
                    }
                }

                localMeasurements.toExternal()
            }
    }

    override fun getMeasurementsStream(): Flow<List<Measurement>> {
        return localDataSource.observeAll().map { measurements ->
            withContext(dispatcher) {
                measurements.toExternal()
            }
        }
    }

    override fun getMeasurementStream(measurementId: String): Flow<Measurement?> {
        return localDataSource.observeById(measurementId).map { it.toExternal() }
    }

    override suspend fun getMeasurements(forceUpdate: Boolean): List<Measurement> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            networkDataSource.loadMeasurements(userId)
                .let { remoteMeasurements ->
                    localDataSource.upsertAll(remoteMeasurements.toLocal())
                }
        }
    }

    override suspend fun getMeasurement(measurementId: String, forceUpdate: Boolean): Measurement? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(measurementId)?.toExternal()
    }

    override suspend fun refreshMeasurement(measurementId: String) {
        refresh()
    }

    override suspend fun deleteAllMeasurements() {
        localDataSource.deleteAll()
        saveMeasurementsToNetwork()
    }

    override suspend fun deleteMeasurement(measurementId: String) {
        localDataSource.deleteById(measurementId)
        saveMeasurementsToNetwork()
    }

    private fun saveMeasurementsToNetwork() {
        scope.launch {
            try {
                val localMeasurements = localDataSource.getAll()
                val networkMeasurements = withContext(dispatcher) {
                    localMeasurements.toNetwork()
                }
                networkDataSource.saveMeasurements(networkMeasurements)
            } catch (e: Exception) {
                Timber.tag("DefaultMeasurementRepository")
                    .e(e, "Failed to save measurements to network")
            }
        }
    }
}