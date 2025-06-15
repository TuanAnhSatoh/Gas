package com.example.gas.data.source.network.datasource

import com.example.gas.data.source.network.model.FirebaseEmergency

interface EmergencyDataSource {

    suspend fun loadEmergencies(userId: String): List<FirebaseEmergency>

    suspend fun saveEmergencies(emergency: List<FirebaseEmergency>)

    suspend fun deleteEmergency(emergencyId: String)
}