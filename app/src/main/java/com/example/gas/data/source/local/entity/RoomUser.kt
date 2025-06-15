package com.example.gas.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.gas.domain.model.Gender
import com.example.gas.data.source.local.Converters
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class RoomUser(
    @PrimaryKey val userId: String,
    val password: String,
    val name: String,
    val address: String?,
    val dateOfBirth: LocalDate,
    val gender: Gender,
    val phone: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)