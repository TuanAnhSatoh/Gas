package com.example.gas.data.source.network.datasource

import com.example.gas.data.source.network.model.FirebaseAlert

interface AlertDataSource {

    suspend fun loadAlerts(userId: String): List<FirebaseAlert>

    suspend fun saveAlerts(alertList: List<FirebaseAlert>)
}