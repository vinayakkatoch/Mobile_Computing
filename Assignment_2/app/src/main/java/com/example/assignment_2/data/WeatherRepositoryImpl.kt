package com.example.assignment_2.data

import com.example.assignment_2.data.model.Daily
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class WeatherRepositoryImpl(
    private val api: WeatherApi
): WeatherRepository {
    override suspend fun getWeatherList(
        latitude: Double,
        longitude: Double,
        startDate: String,
        endDate: String
    ): Flow<Result<Daily>> {
        return flow {
            val weatherFromApi = try {
                api.getWeatherList(latitude = latitude, longitude = longitude, startDate = startDate, endDate = endDate)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error(message = "Error loading weather"))
                return@flow
            }

            emit(Result.Success(weatherFromApi.daily))
        }
    }
}