package com.example.gas.data.mapper

import com.example.gas.data.source.local.entity.RoomMeasurement
import com.example.gas.data.source.network.model.FirebaseMeasurement
import com.example.gas.domain.model.Measurement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// External to Local
fun Measurement.toLocal() = RoomMeasurement(
    measurementId = measurementId,
    userId = userId,
    gas = gas,
    dateTime = dateTime
)

fun List<Measurement>.toLocal() = map(Measurement::toLocal)

// Local to External
fun RoomMeasurement.toExternal() = Measurement(
    measurementId = measurementId,
    userId = userId,
    gas = gas,
    dateTime = dateTime
)

@JvmName("localToExternal")
fun List<RoomMeasurement>.toExternal() = map(RoomMeasurement::toExternal)

// Network to Local
fun FirebaseMeasurement.toLocal() = RoomMeasurement(
    measurementId = measurementId,
    userId = userId,
    gas = gas,
    dateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
)

@JvmName("networkToLocal")
fun List<FirebaseMeasurement>.toLocal() = map(FirebaseMeasurement::toLocal)

// Local to Network
fun RoomMeasurement.toNetwork() = FirebaseMeasurement(
    measurementId = measurementId,
    userId = userId,
    gas = gas,
    dateTime = dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
)

fun List<RoomMeasurement>.toNetwork() = map(RoomMeasurement::toNetwork)

// External to Network
fun Measurement.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Measurement>.toNetwork() = map(Measurement::toNetwork)

// Network to External
fun FirebaseMeasurement.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<FirebaseMeasurement>.toExternal() = map(FirebaseMeasurement::toExternal)