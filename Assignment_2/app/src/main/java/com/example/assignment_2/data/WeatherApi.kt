package com.example.assignment_2.data

import com.example.assignment_2.data.model.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/archive?daily=temperature_2m_max,temperature_2m_min")
    suspend fun getWeatherList(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
        ): Weather

    companion object {
        const val BASE_URL = "https://archive-api.open-meteo.com/"
    }
}