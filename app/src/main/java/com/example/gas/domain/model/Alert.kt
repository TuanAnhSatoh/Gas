package com.example.gas.domain.model

import java.time.LocalDateTime

data class Alert(
    val alertId: String,
    val userId: String,
    val measurementId: String? = null,
    val emergencyId: String? = null,
    val triggerReason: String,
    val contacted: Boolean,
    val timestamp: LocalDateTime
)