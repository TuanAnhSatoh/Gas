package com.example.gas.di

import com.example.gas.data.source.network.datasource.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserDataSource(dataSource: UserFirebaseDataSource): UserDataSource

    @Singleton
    @Binds
    abstract fun bindAlertDataSource(dataSource: AlertFirebaseDataSource): AlertDataSource

    @Singleton
    @Binds
    abstract fun bindEmergencyDataSource(dataSource: EmergencyFirebaseDataSource): EmergencyDataSource

    @Singleton
    @Binds
    abstract fun bindMeasurementDataSource(dataSource: MeasurementFirebaseDataSource): MeasurementDataSource

    @Singleton
    @Binds
    abstract fun bindAuthDataSource(dataSource: AuthFirebaseDataSource): AuthDataSource
}