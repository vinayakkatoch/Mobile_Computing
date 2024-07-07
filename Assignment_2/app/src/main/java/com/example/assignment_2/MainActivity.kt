package com.example.assignment_2

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment_2.data.WeatherDao
import com.example.assignment_2.data.WeatherDatabase
import com.example.assignment_2.data.WeatherEntity
import com.example.assignment_2.data.WeatherRepositoryImpl
import com.example.assignment_2.presentation.WeatherViewModel
import com.example.assignment_2.ui.theme.Assignment_2Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


private val viewModel = WeatherViewModel(WeatherRepositoryImpl(RetrofitInstance.api))
private lateinit var weatherDao: WeatherDao

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment_2Theme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp(
    modifier: Modifier = Modifier,
) {
    val weatherList = viewModel.weather.collectAsState().value
    val context = LocalContext.current

    val weatherDatabase = WeatherDatabase.getDatabase(context)
    weatherDao = weatherDatabase.weatherDao()

    LaunchedEffect(key1 = viewModel.showErrorToastChannel) {
        viewModel.showErrorToastChannel.collectLatest { show ->
            if(show) {
                Toast.makeText(
                    context, "Error Occurred", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var minTemp by remember { mutableDoubleStateOf(0.0) }
    var maxTemp by remember { mutableDoubleStateOf(0.0) }
    var isNetworkAvailable by remember { mutableStateOf(checkNetworkConnectivity(context)) }

    val currentDate = Calendar.getInstance().time
    val threeDaysAgo = getDateDaysAgo(currentDate)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val parsedSelectedDate = if (date.isNotEmpty()) dateFormat.parse(date) else null

    LaunchedEffect(Unit) {
        while (true) {
            isNetworkAvailable = checkNetworkConnectivity(context)
            delay(1000)
        }
    }

    val statusText = if(isNetworkAvailable) "Online" else "Offline"
    val textColor = if(isNetworkAvailable) Color.Green else Color.Red

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .padding(vertical = 14.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = statusText,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = latitude,
                onValueChange = {
                    latitude = if (it.isEmpty()) { it } else {
                        when (it.toDoubleOrNull()) {
                            null -> latitude    //old value
                            else -> it          //new value
                        }
                    }
                },
                label = {
                    Text(text = "Enter Latitude")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = longitude,
                onValueChange = {
                    longitude = if (it.isEmpty()) { it } else {
                        when (it.toDoubleOrNull()) {
                            null -> longitude   //old value
                            else -> it          //new value
                        }
                    }
                },
                label = {
                    Text(text = "Enter Longitude")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                showDatePicker = true
            }) {
                Text(text = "Select Date")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = date)

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                if(latitude.isNotEmpty() && longitude.isNotEmpty() && isNetworkAvailable) {
                    viewModel.fetchData(
                        latitude = latitude.toDouble(),
                        longitude = longitude.toDouble(),
                        startDate = startDate,
                        endDate = endDate
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val maxTemperatures = weatherList.temperature_2m_max
                        val minTemperatures = weatherList.temperature_2m_min
                        val times = weatherList.time
                        println(maxTemperatures.size)
                        for (index in times.indices) {
                            val weatherEntity = WeatherEntity(
                                maxTemp = maxTemperatures[index],
                                minTemp = minTemperatures[index],
                                date = times[index]
                            )
                            weatherDao.insert(weatherEntity)
                        }
                    }
                }
//                Log.d("startDate:", startDate)
//                Log.d("endDate:", endDate)
            }) {
                Text(text = "Download Data")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    if (parsedSelectedDate != null && parsedSelectedDate >= threeDaysAgo) {
                        val parts = date.split("-")
                        val dateSuffix = "%-${parts[1]}-${parts[2]}"
                        val weatherEntityList = weatherDao.getAverageTemp(dateSuffix)
                        if(weatherEntityList.isEmpty()) {
                            minTemp = 0.0
                            maxTemp = 0.0
                        }
                        else {
                            var minTempSum = 0.0
                            var maxTempSum = 0.0
                            for(index in weatherEntityList.indices) {
                                minTempSum += weatherEntityList[index].minTemp
                                maxTempSum += weatherEntityList[index].maxTemp
                            }
                            minTemp = minTempSum / weatherEntityList.size
                            maxTemp = maxTempSum / weatherEntityList.size
                        }
                    } else {
                        val weatherEntity = weatherDao.getWeatherEntityForDate(date)
                        minTemp = weatherEntity?.minTemp ?: 0.0
                        maxTemp = weatherEntity?.maxTemp ?: 0.0
                    }
                }
            }) {
                Text(text = "Get temperature")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = if(minTemp != 0.0)"Min temperature: ${String.format("%.2f", minTemp)}\u2103" else "Min temperature: --.--\u2103")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = if(maxTemp != 0.0) "Max temperature: ${String.format("%.2f", maxTemp)}\u2103" else "Max temperature: --.--\u2103")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    weatherDao.deleteAllWeatherData()
                }
            }) {
                Text(text = "Clear Data")
            }
        }
    }

    if(showDatePicker) {
        CustomDatePickerDialog(
            onDateSelected = { date = it },
            onDismiss = { showDatePicker = false }
        )
    }

    if(parsedSelectedDate != null && parsedSelectedDate >= threeDaysAgo) {
        val tenYearsAgoCalendar = Calendar.getInstance()
        tenYearsAgoCalendar.time = currentDate
        tenYearsAgoCalendar.add(Calendar.YEAR, -10)
        startDate = dateFormat.format(tenYearsAgoCalendar.time)
        endDate = dateFormat.format(threeDaysAgo)
    } else {
        startDate = if (date.isNotEmpty()) date else dateFormat.format(currentDate)
        endDate = dateFormat.format(threeDaysAgo)
    }
}

private fun getDateDaysAgo(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.DATE, -3)
    return calendar.time
}

private fun checkNetworkConnectivity(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    return networkCapabilities != null &&
            (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
    val selectedDate = datePickerState.selectedDateMillis?.let { convertMillisToDate(it) } ?: convertMillisToDate(System.currentTimeMillis())

    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }) {
        DatePicker(state = datePickerState)
    }
}

@SuppressLint("SimpleDateFormat")
private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    return formatter.format(Date(millis))
}

@Preview(showBackground = true)
@Composable
fun WeatherPreview() {
    Assignment_2Theme {
        MyApp()
    }
}