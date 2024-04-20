package com.example.assignment_3

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment_3.data.OrientationDao
import com.example.assignment_3.data.OrientationDatabase
import com.example.assignment_3.data.OrientationEntity
import com.example.assignment_3.ui.theme.Assignment_3Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity :ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gravity = FloatArray(3)

    private var xValue by mutableFloatStateOf(0.0F)
    private var yValue by mutableFloatStateOf(0.0F)
    private var zValue by mutableFloatStateOf(0.0F)

    private val xValues = mutableListOf<Float>()
    private val yValues = mutableListOf<Float>()
    private val zValues = mutableListOf<Float>()
    private val timeSeries = mutableListOf<Float>()

    private val timeWindow = 60

    private var isCollectingData by mutableStateOf(false)
    private var isSaveButtonEnabled by mutableStateOf(true)

    private var lastCollectionTime = 0L

    private lateinit var orientationDao: OrientationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment_3Theme {
                MyApp()
            }
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val alpha = 0.8f

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

            xValue = event.values[0] - gravity[0]
            yValue = event.values[1] - gravity[1]
            zValue = event.values[2] - gravity[2]

            val currentTime = System.currentTimeMillis()

            if (isCollectingData && currentTime - lastCollectionTime >= 500L) {
                lastCollectionTime = currentTime

                xValues.add(xValue)
                yValues.add(yValue)
                zValues.add(zValue)
                timeSeries.add((0.5 * xValues.size).toFloat())

                if (xValues.size == 2 * timeWindow && yValues.size == 2 * timeWindow && zValues.size == 2 * timeWindow) {
                    isCollectingData = false
                    isSaveButtonEnabled = true
                }
            }
        }
    }

    @Composable
    fun MyApp(
        modifier: Modifier = Modifier
    ) {

        val context = LocalContext.current
        val orientationDatabase = OrientationDatabase.getDatabase(context)
        orientationDao = orientationDatabase.OrientationDao()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = modifier
                    .padding(vertical = 15.dp, horizontal = 4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Accelerometer",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(height = 125.dp, width = 185.dp)
                        .border(BorderStroke(4.dp, MaterialTheme.colorScheme.primary))
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column {

                        Text(
                            text = "x-axis : ${String.format("%.5f", xValue)}",
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "y-axis : ${String.format("%.5f", yValue)}",
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "z-axis : ${String.format("%.5f", zValue)}",
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        xValues.clear()
                        yValues.clear()
                        zValues.clear()
                        isCollectingData = true
                        isSaveButtonEnabled = false
                    },
                    modifier = Modifier.size(width = 135.dp, height = 40.dp),
                ) {
                    Text(text = "Start")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
//                        println("xValues    : ${xValues.size}")
//                        println("yValues    : ${yValues.size}")
//                        println("zValues    : ${zValues.size}")
//                        println("timeSeries : ${timeSeries.size}")
                        CoroutineScope(Dispatchers.IO).launch {
                            orientationDao.clearDatabase()
                            for (index in timeSeries.indices) {
                                val orientationEntity = OrientationEntity(
                                    xAxis = xValues[index],
                                    yAxis = yValues[index],
                                    zAxis = zValues[index],
                                    time = timeSeries[index]
                                )
                                orientationDao.insert(orientationEntity)
                            }
                        }

                    },
                    enabled = isSaveButtonEnabled,
                    modifier = Modifier.size(width = 135.dp, height = 40.dp),
                ) {
                    Text(text = "Save Data")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val intent = Intent(context, HistoryActivity::class.java)
                        startActivity(intent)
                    },
                    modifier = Modifier.size(width = 135.dp, height = 40.dp),
                ) {
                    Text(text = "Show Graph")
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AccelerometerPreview() {
        Assignment_3Theme {
            MyApp()
        }
    }
}
