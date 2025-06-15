package com.example.gas.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gas.data.source.local.dao.*
import com.example.gas.data.source.local.entity.*

@Database(
    entities = [
        RoomEmergency::class,
        RoomMeasurement::class,
        RoomAlert::class,
        RoomUser::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun emergencyInfoDao(): EmergencyDao

    abstract fun measurementDao(): MeasurementDao

    abstract fun alertDao(): AlertDao

    abstract fun userDao(): UserDao
}