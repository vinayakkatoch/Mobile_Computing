package com.example.assignment_2.data.model

data class Daily(
    val temperature_2m_max: List<Double> = emptyList(),
    val temperature_2m_min: List<Double> = emptyList(),
    val time: List<String> = emptyList()
)