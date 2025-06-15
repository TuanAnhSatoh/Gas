package com.example.gas.data.source.local

import androidx.room.TypeConverter
import com.example.gas.domain.model.Gender
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun toLocalDate(date: String): LocalDate {
        return LocalDate.parse(date)
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.toString()
    }

    @TypeConverter
    fun toLocalTime(time: String): LocalTime {
        return LocalTime.parse(time)
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String {
        return dateTime.toString()
    }

    @TypeConverter
    fun toLocalDateTime(dateTime: String): LocalDateTime {
        return LocalDateTime.parse(dateTime)
    }

    @TypeConverter
    fun fromGender(gender: Gender): String {
        return gender.name
    }

    @TypeConverter
    fun toGender(gender: String): Gender {
        return Gender.valueOf(gender)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value.split(",").map { it.trim() }
    }

    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        return value.split(",").map { it.trim().toInt() }
    }

    @TypeConverter
    fun fromFloatList(value: List<Float>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toFloatList(value: String): List<Float> {
        return value.split(",").map { it.trim().toFloat() }
    }
}