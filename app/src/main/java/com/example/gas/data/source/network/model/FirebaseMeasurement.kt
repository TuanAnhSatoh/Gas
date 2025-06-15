package com.example.gas.data.source.network.model

data class FirebaseMeasurement(
    var measurementId: String = "",
    var userId: String = "",
    var gas: Float = 0.0f,
    var dateTime: String = ""
)