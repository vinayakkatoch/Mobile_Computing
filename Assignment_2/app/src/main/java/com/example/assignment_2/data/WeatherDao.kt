package com.example.assignment_2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherEntity: WeatherEntity)

    @Query("SELECT * FROM weather_data WHERE date = :dateToCheck")
    suspend fun getWeatherEntityForDate(dateToCheck: String): WeatherEntity?

    @Query("SELECT * FROM weather_data WHERE date LIKE :dateSuffix ORDER BY id DESC LIMIT 10")
    suspend fun getAverageTemp(dateSuffix: String): List<WeatherEntity>

    @Query("SELECT * FROM weather_data")
    suspend fun getAllWeatherData(): List<WeatherEntity>

    @Query("DELETE FROM weather_data")
    suspend fun deleteAllWeatherData()
}