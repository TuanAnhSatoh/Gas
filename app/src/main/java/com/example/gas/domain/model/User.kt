package com.example.gas.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class User(
    val userId: String,
    val password: String,
    val name: String,
    val address: String?,
    val dateOfBirth: LocalDate,
    val gender: Gender,
    val phone: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)