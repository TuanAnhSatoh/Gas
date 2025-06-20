package com.example.gas.data.source.network.datasource

import com.example.gas.data.source.network.model.FirebaseMeasurement
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MeasurementFirebaseDataSource @Inject constructor(
    firebaseDatabase: FirebaseDatabase
) : MeasurementDataSource {

    private val measurementsRef = firebaseDatabase.getReference("measurements")

    override fun getMeasurementsFirebaseRealtime(
        userId: String
    ): Flow<List<FirebaseMeasurement>> = callbackFlow {
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(FirebaseMeasurement::class.java)?.let { measurement ->
                    if (measurement.userId == userId) {
                        trySend(listOf(measurement)).isSuccess
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(FirebaseMeasurement::class.java)?.let { measurement ->
                    if (measurement.userId == userId) {
                        trySend(listOf(measurement)).isSuccess
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle removal if necessary, e.g., notify the UI to remove the measurement
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle child moved if necessary, e.g., update the order of measurements
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        measurementsRef.addChildEventListener(listener)

        awaitClose { measurementsRef.removeEventListener(listener) }
    }

    override suspend fun loadMeasurements(userId: String): List<FirebaseMeasurement> = try {
        measurementsRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseMeasurement::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading measurements for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveMeasurements(measurements: List<FirebaseMeasurement>) {
        if (measurements.isEmpty()) return

        try {
            val updates = measurements.associateBy { it.measurementId }
            measurementsRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving measurements: ${e.message}", e)
        }
    }
}