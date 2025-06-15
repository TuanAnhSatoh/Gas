package com.example.gas.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.gas.data.source.local.entity.RoomEmergency
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyDao {

    @Query("SELECT * FROM emergencies")
    fun observeAll(): Flow<List<RoomEmergency>>

    @Query("SELECT * FROM emergencies WHERE emergencyId = :emergencyId")
    fun observeById(emergencyId: String): Flow<RoomEmergency>

    @Query("SELECT * FROM emergencies WHERE userId = :userId")
    fun observeByUserId(userId: String): Flow<List<RoomEmergency>>

    @Query("SELECT * FROM emergencies")
    suspend fun getAll(): List<RoomEmergency>

    @Query("SELECT * FROM emergencies WHERE emergencyId = :emergencyId")
    suspend fun getById(emergencyId: String): RoomEmergency?

    @Query("SELECT * FROM emergencies WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<RoomEmergency>

    @Upsert
    suspend fun upsert(emergency: RoomEmergency)

    @Upsert
    suspend fun upsertAll(emergency: List<RoomEmergency>)

    @Query("DELETE FROM emergencies WHERE emergencyId = :emergencyId")
    suspend fun deleteById(emergencyId: String): Int

    @Query("DELETE FROM emergencies WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String): Int

    @Query("DELETE FROM emergencies")
    suspend fun deleteAll()
}