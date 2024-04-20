package com.example.assignment_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.assignment_3.data.OrientationDao
import com.example.assignment_3.data.OrientationDatabase
import com.example.assignment_3.data.OrientationEntity
import com.example.assignment_3.ui.theme.Assignment_3Theme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.io.File
import java.io.FileWriter

class HistoryActivity: ComponentActivity() {

    private lateinit var orientationDao: OrientationDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment_3Theme {
                MyApp()
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

        var orientationData by remember { mutableStateOf<List<OrientationEntity>>(emptyList()) }

        LaunchedEffect(true) { orientationData = orientationDao.getAllOrientationData() }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = modifier
                    .padding(vertical = 15.dp, horizontal = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if(orientationData.isNotEmpty()) {
                        val time = orientationData.map { it.time }
                        val xValues = orientationData.map { it.xAxis }
                        val yValues = orientationData.map { it.yAxis }
                        val zValues = orientationData.map { it.zAxis }

                        LazyColumn {
                            item {
                                DrawLineChart(time, xValues, "X")
                            }
                            item {
                                DrawLineChart(time, yValues, "Y")
                            }
                            item {
                                DrawLineChart(time, zValues, "Z")
                            }
                        }
                    }
                }
                Button(
                    onClick = { exportToCSV(orientationData) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .size(width = 135.dp, height = 40.dp),
                ) {
                    Text(text = "Export CSV")
                }
            }
        }
    }

    @Composable
    fun DrawLineChart(xValues: List<Float>, yValues: List<Float>, label: String) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AndroidView(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .align(Alignment.BottomCenter)
                    .aspectRatio(1f),
                factory = { context ->
                    LineChart(context).apply {
                        setNoDataText("No data available")
                        setTouchEnabled(true)
                        isDragEnabled = true
                        setScaleEnabled(true)
                        setPinchZoom(true)
                        legend.textColor = Color.White.toArgb()
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.setDrawGridLines(false)

//                        xAxis.valueFormatter = IndexAxisValueFormatter(xValues.map { it.toString() })
//                        print(xAxis.valueFormatter)

                        xAxis.granularity = 1f
                        xAxis.apply {// Set text color for X-axis labels
                            textColor = Color.White.toArgb()
                            axisLineWidth = 2f
                            // Set grid line width
                            gridLineWidth = 1.5f
                            axisLineColor = Color.White.toArgb()
                            gridColor = Color.White.toArgb()
                        }
                        legend.apply {
                            // Set legend text color to white
                            textColor = Color.White.toArgb()
                            // Increase legend text size
                            textSize = 14f // Adjust the size as needed
                        }
                        axisLeft.apply {
                            // Set text color for Y-axis labels
                            textColor = Color.White.toArgb()
                            axisLineWidth = 2f
                            // Set grid line width
                            gridLineWidth = 1.5f
                            axisLineColor = Color.White.toArgb()
                            gridColor = Color.White.toArgb()
                        }
                        // Set the x-axis description label
                        description.text = "Time (seconds)"
                        description.textColor = Color.White.toArgb()

                        axisRight.isEnabled = false
                        val leftAxis: YAxis = axisLeft
                        leftAxis.setDrawGridLines(true)

                        // Find the minimum and maximum values for x and y axes
                        val xMax = xValues.maxOrNull() ?: 0f
                        val xMin = xValues.minOrNull() ?: 0f
                        val yMax = yValues.maxOrNull() ?: 0f
                        val yMin = yValues.minOrNull() ?: 0f

//                        println("---> $xMax $xMin $yMax $yMin")

                        // Set minimum and maximum values for the axes with some padding
                        axisLeft.axisMinimum = yMin - 1f
                        axisLeft.axisMaximum = yMax + 1f
                        xAxis.axisMinimum = 0f
                        xAxis.axisMaximum = xMax + 0.5f
                        xAxis.granularity = 0.5f

                        val pointsList = ArrayList<Entry>()

                        for(index in xValues.indices) {
                            pointsList.add(Entry(xValues[index], yValues[index]))
                        }

                        val set = LineDataSet(pointsList, "$label Values").apply {
                            color = ColorTemplate.getHoloBlue()
                            setCircleColor(ColorTemplate.getHoloBlue())
                            lineWidth = 2f
                            circleRadius = 3f
                            setDrawCircleHole(false)
                            valueTextSize = 9f
                            valueTextColor = Color.White.toArgb()
                            setDrawFilled(true)
                            fillColor = ColorTemplate.getHoloBlue()
                            // Set the color of the legend text to white
                            setDrawValues(true)
                            setValueTextColor(Color.White.toArgb())
                        }

                        val data = LineData(set)
                        setData(data)
                    }
                }
            )
        }
    }

    private fun exportToCSV(orientationData: List<OrientationEntity>) {
        val csvFile = File(getExternalFilesDir(null), "orientation_data.csv")
        val writer = FileWriter(csvFile)

        writer.apply {
            append("Time,X-Axis,Y-Axis,Z-Axis\n")
            orientationData.forEach { orientation ->
                append("${orientation.time},${orientation.xAxis},${orientation.yAxis},${orientation.zAxis}\n")
            }
            flush()
            close()
        }
    }
}