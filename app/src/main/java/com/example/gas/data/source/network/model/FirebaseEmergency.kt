package com.example.gas.data.source.network.model

data class FirebaseEmergency(
    var emergencyId: String = "",
    var userId: String = "",
    var emergencyName: String = "",
    var emergencyPhone: String = "",
    var priority: Int = 0
)