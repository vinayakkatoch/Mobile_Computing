package com.example.assignment_2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment_2.data.Result
import com.example.assignment_2.data.WeatherRepository
import com.example.assignment_2.data.model.Daily
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    private val _weather = MutableStateFlow<Daily>(Daily())
    val weather = _weather.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    fun fetchData(latitude: Double, longitude: Double, startDate: String, endDate: String) {
        viewModelScope.launch {
            weatherRepository.getWeatherList(
                latitude = latitude,
                longitude = longitude,
                startDate = startDate,
                endDate = endDate
            ).collectLatest { result ->
                when(result) {
                    is Result.Error -> {
                        _showErrorToastChannel.send(true)
                    }
                    is Result.Success -> {
                        result.data?.let { weather ->
                            _weather.update { weather }
                        }
                    }
                }
            }
        }
    }
}