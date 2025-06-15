package com.example.gas.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gas.data.source.local.AppDatabase
import com.example.gas.data.source.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "AppDatabase.db"
        )
            .fallbackToDestructiveMigration(true)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }
            })
            .build()
    }

    @Provides
    fun provideEmergencyInfoDao(database: AppDatabase): EmergencyDao = database.emergencyInfoDao()

    @Provides
    fun provideMeasurementDao(database: AppDatabase): MeasurementDao = database.measurementDao()

    @Provides
    fun provideAlertDao(database: AppDatabase): AlertDao = database.alertDao()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}