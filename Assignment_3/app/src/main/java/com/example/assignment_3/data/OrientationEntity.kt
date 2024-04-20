package com.example.assignment_3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orientation_data")
data class OrientationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val xAxis: Float,
    val yAxis: Float,
    val zAxis: Float,
    val time: Float
)
