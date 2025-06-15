package com.example.gas.domain.model

import java.time.LocalDateTime

data class Measurement(
    val measurementId: String,
    val userId: String,
    val gas: Float,
    val dateTime: LocalDateTime
)