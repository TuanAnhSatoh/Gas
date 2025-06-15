package com.example.gas.di

import com.example.gas.domain.repository.*
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepositoryEntryPoint {

    fun alertRepository(): AlertRepository

    fun emergencyRepository(): EmergencyRepository

    fun userRepository(): UserRepository
}