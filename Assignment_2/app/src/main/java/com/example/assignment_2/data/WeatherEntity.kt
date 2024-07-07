package com.example.assignment_2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_data")
data class WeatherEntity(
    val minTemp: Double,
    val maxTemp: Double,
    val date: String,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)
