package com.example.gas.data.mapper

import com.example.gas.data.source.local.entity.RoomEmergency
import com.example.gas.data.source.network.model.FirebaseEmergency
import com.example.gas.domain.model.EmergencyInfo

// External to Local
fun EmergencyInfo.toLocal() = RoomEmergency(
    emergencyId = emergencyId,
    userId = userId,
    emergencyName = emergencyName,
    emergencyPhone = emergencyPhone,
    priority = priority
)

fun List<EmergencyInfo>.toLocal() = map(EmergencyInfo::toLocal)

// Local to External
fun RoomEmergency.toExternal() = EmergencyInfo(
    emergencyId = emergencyId,
    userId = userId,
    emergencyName = emergencyName,
    emergencyPhone = emergencyPhone,
    priority = priority
)

@JvmName("localToExternal")
fun List<RoomEmergency>.toExternal() = map(RoomEmergency::toExternal)

// Network to Local
fun FirebaseEmergency.toLocal() = RoomEmergency(
    emergencyId = emergencyId,
    userId = userId,
    emergencyName = emergencyName,
    emergencyPhone = emergencyPhone,
    priority = priority
)

@JvmName("networkToLocal")
fun List<FirebaseEmergency>.toLocal() = map(FirebaseEmergency::toLocal)

// Local to Network
fun RoomEmergency.toNetwork() = FirebaseEmergency(
    emergencyId = emergencyId,
    userId = userId,
    emergencyName = emergencyName,
    emergencyPhone = emergencyPhone,
    priority = priority
)

fun List<RoomEmergency>.toNetwork() = map(RoomEmergency::toNetwork)

// External to Network
fun EmergencyInfo.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<EmergencyInfo>.toNetwork() = map(EmergencyInfo::toNetwork)

// Network to External
fun FirebaseEmergency.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseEmergency>.toExternal() = map(FirebaseEmergency::toExternal)