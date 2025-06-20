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
import com.example.gas.domain.model.EmergencyInfo
import com.example.gas.domain.repository.EmergencyInfoRepository
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
class DefaultEmergencyInfoRepository @Inject constructor(
    private val networkDataSource: EmergencyDataSource,
    private val localDataSource: EmergencyDao,
    private val authDataSource: AuthDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
    private val appDatabase: AppDatabase
) : EmergencyInfoRepository {

    private val userId: String
        get() = authDataSource.getCurrentUserId() ?: throw Exception("User not logged in")

    override suspend fun createEmergencyInfo(
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
        val emergencyInfo = EmergencyInfo(
            emergencyId = emergencyId,
            userId = userId,
            emergencyName = contactName,
            emergencyPhone = contactNumber,
            priority = priority
        )
        localDataSource.upsert(emergencyInfo.toLocal())
        saveEmergencyInfosToNetwork()
        return emergencyId
    }

    override suspend fun updateEmergencyInfo(
        emergencyInfoId: String,
        contactName: String,
        contactNumber: String,
        priority: Int
    ) {
        if (priority !in 1..5) {
            throw IllegalArgumentException("Priority must be between 1 and 5")
        }
        val emergencyInfo = getEmergencyInfo(emergencyInfoId)?.copy(
            emergencyName = contactName,
            emergencyPhone = contactNumber,
            priority = priority
        ) ?: throw Exception("EmergencyInfo (id $emergencyInfoId) not found")

        localDataSource.upsert(emergencyInfo.toLocal())
        saveEmergencyInfosToNetwork()
    }

    override suspend fun getEmergencyInfos(forceUpdate: Boolean): List<EmergencyInfo> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            Timber.tag("DefaultEmergencyInfoRep").d("Fetching emergency infos to call")
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            appDatabase.withTransaction {
                saveEmergencyInfosToNetwork()
                val remoteEmergencyInfos = networkDataSource.loadEmergencies(userId)
                localDataSource.upsertAll(remoteEmergencyInfos.toLocal())
            }
        }
    }

    override suspend fun getEmergencyInfo(
        emergencyInfoId: String,
        forceUpdate: Boolean
    ): EmergencyInfo? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(emergencyInfoId)?.toExternal()
    }

    override suspend fun refreshEmergencyInfo(emergencyInfoId: String) {
        refresh()
    }

    override fun getEmergencyInfosStream(): Flow<List<EmergencyInfo>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getEmergencyInfoStream(emergencyInfoId: String): Flow<EmergencyInfo?> {
        return localDataSource.observeById(emergencyInfoId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun activateEmergencyInfo(emergencyInfoId: String) {
        val emergencyInfo = getEmergencyInfo(emergencyInfoId)?.copy(priority = 1)
            ?: throw Exception("EmergencyInfo (id $emergencyInfoId) not found")
        localDataSource.upsert(emergencyInfo.toLocal())
        saveEmergencyInfosToNetwork()
    }

    override suspend fun deactivateEmergencyInfo(emergencyInfoId: String) {
        val emergencyInfo = getEmergencyInfo(emergencyInfoId)?.copy(priority = 0)
            ?: throw Exception("EmergencyInfo (id $emergencyInfoId) not found")
        localDataSource.upsert(emergencyInfo.toLocal())
        saveEmergencyInfosToNetwork()
    }

    override suspend fun clearInactiveEmergencyInfos() {
        localDataSource.deleteByUserId(userId)
        saveEmergencyInfosToNetwork()
    }

    override suspend fun deleteAllEmergencyInfos() {
        localDataSource.deleteAll()
        saveEmergencyInfosToNetwork()
    }

    override suspend fun deleteEmergencyInfo(emergencyInfoId: String) {
        localDataSource.deleteById(emergencyInfoId)
        withContext(dispatcher) {
            networkDataSource.deleteEmergency(emergencyInfoId)
        }
        saveEmergencyInfosToNetwork()
    }

    private fun saveEmergencyInfosToNetwork() {
        scope.launch {
            try {
                val localEmergencyInfos = localDataSource.getAll()
                val networkEmergencyInfos = withContext(dispatcher) {
                    localEmergencyInfos.toNetwork()
                }
                networkDataSource.saveEmergencies(networkEmergencyInfos)
            } catch (e: Exception) {
                Timber.tag("DefaultEmergencyInfoRep")
                    .e(e, "Failed to save emergency infos to network")
            }
        }
    }
}