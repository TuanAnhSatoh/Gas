package com.example.gas.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmergencyInfo(
    val emergencyId: String,
    val userId: String,
    val emergencyName: String,
    val emergencyPhone: String,
    val priority: Int
): Parcelable
