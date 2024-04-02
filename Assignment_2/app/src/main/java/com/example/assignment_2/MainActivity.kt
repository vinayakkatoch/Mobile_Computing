package com.example.assignment_2

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment_2.data.WeatherRepositoryImpl
import com.example.assignment_2.presentation.WeatherViewModel
import com.example.assignment_2.ui.theme.Assignment_2Theme
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date


private val viewModel = WeatherViewModel(WeatherRepositoryImpl(RetrofitInstance.api))
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
    var minTemp by remember { mutableStateOf("") }
    var maxTemp by remember { mutableStateOf("") }

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
                if(latitude.isNotEmpty() && longitude.isNotEmpty())
                    viewModel.fetchData(
                        latitude = latitude.toDouble(),
                        longitude = longitude.toDouble(),
//                        startDate = startDate,
//                        endDate = endDate
                        startDate = date,
                        endDate = date
                    )
            }) {
                Text(text = "Get temperature")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = if(weatherList.temperature_2m_min.isNotEmpty())"Min temperature: ${weatherList.temperature_2m_min[0]}\u2103" else "Min temperature: --.--\u2103")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = if(weatherList.temperature_2m_max.isNotEmpty()) "Max temperature: ${weatherList.temperature_2m_max[0]}\u2103" else "Max temperature: --.--\u2103")
        }
    }

//    if(date.isNotEmpty()) {
//        startDate = date
//        endDate = date
//        minTemp = weatherList.temperature_2m_min[0]
//        maxTemp = weatherList.temperature_2m_max[0]
////        if(date != endDate) {
////            minTemp = 0.0
////            maxTemp = 0.0
////        }
////        else {
////            minTemp = weatherList.temperature_2m_min[0]
////            maxTemp = weatherList.temperature_2m_max[0]
////        }
//    }

    if(showDatePicker) {
        CustomDatePickerDialog(
            onDateSelected = { date = it },
//            changeStartDate = { startDate = it},
//            changeEndDate = { endDate = it},
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    onDateSelected: (String) -> Unit,
//    changeStartDate: (String) -> Unit,
//    changeEndDate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
    val selectedDate = datePickerState.selectedDateMillis?.let { convertMillisToDate(it) } ?: convertMillisToDate(System.currentTimeMillis())

//    var startDate = selectedDate
//    var endDate = selectedDate
//    val year = selectedDate.substring(0, 4).toInt()
//
//    if(datePickerState.selectedDateMillis!! >= System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000)) {
//        startDate = "${year - 10}${selectedDate.substring(4)}"
//        endDate = "${year - 1}${selectedDate.substring(4)}"
//    }

    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
//                changeStartDate(startDate)
//                changeEndDate(endDate)
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