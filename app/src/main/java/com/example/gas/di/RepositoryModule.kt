package com.example.gas.di

import com.example.gas.data.repository.*
import com.example.gas.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserRepository(repository: DefaultUserRepository): UserRepository

    @Singleton
    @Binds
    abstract fun bindEmergencyInfoRepository(repository: DefaultEmergencyInfoRepository): EmergencyInfoRepository

    @Singleton
    @Binds
    abstract fun bindMeasurementRepository(repository: DefaultMeasurementRepository): MeasurementRepository

    @Singleton
    @Binds
    abstract fun bindAlertRepository(repository: DefaultAlertRepository): AlertRepository
}