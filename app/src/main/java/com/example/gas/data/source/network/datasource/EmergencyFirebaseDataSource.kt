package com.example.gas.data.source.network.datasource

import com.example.gas.data.source.network.model.FirebaseEmergency
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmergencyFirebaseDataSource @Inject constructor(
    firebaseDatabase: FirebaseDatabase
) : EmergencyDataSource {

    private val emergenciesRef = firebaseDatabase.getReference("emergencies")

    override suspend fun loadEmergencies(userId: String): List<FirebaseEmergency> = try {
        emergenciesRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseEmergency::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading emergency infos for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveEmergencies(emergency: List<FirebaseEmergency>) {
        if (emergency.isEmpty()) return

        try {
            val updates = emergency.associateBy { it.emergencyId }
            emergenciesRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving emergency infos: ${e.message}", e)
        }
    }

    override suspend fun deleteEmergency(emergencyId: String) {
        try {
            emergenciesRef.child(emergencyId).removeValue().await()
        } catch (e: Exception) {
            throw Exception("Error deleting emergency info: ${e.message}", e)
        }
    }
}