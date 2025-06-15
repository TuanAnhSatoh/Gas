package com.example.gas.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.gas.data.source.local.Converters

@Entity(
    tableName = "emergencies",
    foreignKeys = [
        ForeignKey(
            entity = RoomUser::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
@TypeConverters(Converters::class)
data class RoomEmergency(
    @PrimaryKey val emergencyId: String,
    val userId: String,
    val emergencyName: String,
    val emergencyPhone: String,
    val priority: Int
)