package com.example.gas.data.repository

import androidx.room.withTransaction
import com.example.gas.data.mapper.toExternal
import com.example.gas.data.mapper.toLocal
import com.example.gas.data.mapper.toNetwork
import com.example.gas.data.source.local.AppDatabase
import com.example.gas.data.source.local.dao.EmergencyDao
import com.example.gas.data.source.network.datasource.AuthDataSource
import com.example.gas.data.source.network.datasource.EmergencyDataSource
import com.example.gas.di.ApplicationScope
import com.example.gas.di.DefaultDispatcher
import com.example.gas.domain.model.Emergency
import com.example.gas.domain.repository.EmergencyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultEmergencyRepository @Inject constructor(
    private val networkDataSource: EmergencyDataSource,
    private val localDataSource: EmergencyDao,
    private val authDataSource: AuthDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
    private val appDatabase: AppDatabase
) : EmergencyRepository {

    private val userId: String
        get() = authDataSource.getCurrentUserId() ?: throw Exception("User not logged in")

    override suspend fun createEmergency(
        contactName: String,
        contactNumber: String,
        priority: Int
    ): String {
        if (priority !in 1..5) {
            throw IllegalArgumentException("Priority must be between 1 and 5")
        }

        val emergencyId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val emergency = Emergency(
            emergencyId = emergencyId,
            userId = userId,
            emergencyName = contactName,
            emergencyPhone = contactNumber,
            priority = priority
        )
        localDataSource.upsert(emergency.toLocal())
        saveEmergenciesToNetwork()
        return emergencyId
    }

    override suspend fun updateEmergency(
        emergencyId: String,
        contactName: String,
        contactNumber: String,
        priority: Int
    ) {
        if (priority !in 1..5) {
            throw IllegalArgumentException("Priority must be between 1 and 5")
        }
        val emergency = getEmergency(emergencyId)?.copy(
            emergencyName = contactName,
            emergencyPhone = contactNumber,
            priority = priority
        ) ?: throw Exception("Emergency (id $emergencyId) not found")

        localDataSource.upsert(emergency.toLocal())
        saveEmergenciesToNetwork()
    }

    override suspend fun getEmergencies(forceUpdate: Boolean): List<Emergency> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            Timber.tag("DefaultEmergencyRepository").d("Fetching emergencies to call")
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            appDatabase.withTransaction {
                saveEmergenciesToNetwork()
                val remoteEmergencies = networkDataSource.loadEmergencies(userId)
                localDataSource.upsertAll(remoteEmergencies.toLocal())
            }
        }
    }

    override suspend fun getEmergency(
        emergencyId: String,
        forceUpdate: Boolean
    ): Emergency? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(emergencyId)?.toExternal()
    }

    override suspend fun refreshEmergency(emergencyId: String) {
        refresh()
    }

    override fun getEmergenciesStream(): Flow<List<Emergency>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getEmergencyStream(emergencyId: String): Flow<Emergency?> {
        return localDataSource.observeById(emergencyId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun deleteAllEmergencies() {
        localDataSource.deleteAll()
        saveEmergenciesToNetwork()
    }

    override suspend fun deleteEmergency(emergencyId: String) {
        localDataSource.deleteById(emergencyId)
        withContext(dispatcher) {
            networkDataSource.deleteEmergency(emergencyId)
        }
        saveEmergenciesToNetwork()
    }

    private fun saveEmergenciesToNetwork() {
        scope.launch {
            try {
                val localEmergencies = localDataSource.getAll()
                val networkEmergencies = withContext(dispatcher) {
                    localEmergencies.toNetwork()
                }
                networkDataSource.saveEmergencies(networkEmergencies)
            } catch (e: Exception) {
                Timber.tag("DefaultEmergencyRepository")
                    .e(e, "Failed to save emergencies to network")
            }
        }
    }
}