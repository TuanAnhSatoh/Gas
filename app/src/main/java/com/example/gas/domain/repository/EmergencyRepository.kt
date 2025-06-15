package com.example.gas.domain.repository

import com.example.gas.domain.model.Emergency
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the data layer for emergency information.
 */
interface EmergencyRepository {

    suspend fun createEmergency(
        contactName: String,
        contactNumber: String,
        priority : Int
    ): String

    suspend fun updateEmergency(
        emergencyId: String,
        contactName: String,
        contactNumber: String,
        priority : Int
    )

    suspend fun getEmergencies(forceUpdate: Boolean = false): List<Emergency>

    suspend fun refresh()

    suspend fun getEmergency(emergencyId: String, forceUpdate: Boolean = false): Emergency?

    suspend fun refreshEmergency(emergencyId: String)

    fun getEmergenciesStream(): Flow<List<Emergency>>

    fun getEmergencyStream(emergencyId: String): Flow<Emergency?>

    suspend fun deleteAllEmergencies()

    suspend fun deleteEmergency(emergencyId: String)
}