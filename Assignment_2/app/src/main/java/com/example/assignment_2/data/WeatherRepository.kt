package com.example.assignment_2.data

import com.example.assignment_2.data.model.Daily
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherList(
        latitude: Double,
        longitude: Double,
        startDate: String,
        endDate: String
    ): Flow<Result<Daily>>
}