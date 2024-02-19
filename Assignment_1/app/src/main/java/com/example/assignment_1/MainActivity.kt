package com.example.assignment_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.assignment_1.ui.theme.Assignment_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment_1Theme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    stopDistance: Int = 10,
    stops: List<String> = listOf(
        "IIITD", "Govind Puri", "Kalkaji Mandir", "Nehru Place", "Kailash Colony",
        "Moolchand", "Lajpat Nagar", "Jangpura", "JLN Stadium", "Khan Market",
        "Central Secretariate", "Janpath", "Mandi House", "ITO", "Delhi Gate",
        "Jama Masjid", "Lal Quila", "Kashmere Gate"
    )
//    stops: List<String> = listOf(
//        "IIITD", "Govind Puri", "Kalkaji Mandir", "Nehru Place", "Kailash Colony",
//        "Moolchand", "Lajpat Nagar", "Jangpura", "JLN Stadium", "Khan Market"
//    )
) {
    var loadProgress by rememberSaveable { mutableStateOf(false) }
    var isKm by rememberSaveable { mutableStateOf(true) }
    var stopIndex by rememberSaveable { mutableIntStateOf(0) }
    var distanceCovered by rememberSaveable { mutableIntStateOf(0) }
    var distanceLeft by rememberSaveable { mutableIntStateOf((stops.size - 1) * stopDistance) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            JourneyProgress(totalStops = stops.size - 1, stopIndex = stopIndex)
            if(loadProgress) {
                JourneyDetails(
                    stops = stops,
                    isKm = isKm,
                    stopIndex = stopIndex,
                    stopDistance = stopDistance,
                    distanceCovered = distanceCovered,
                    distanceLeft = distanceLeft,
                    totalStops = stops.size - 1
                )
            }
            MaintainStops(stops = stops, stopIndex = stopIndex)
        }
        Column(
            modifier = modifier
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AddOperations(
                isKm = isKm,
                loadProgress = loadProgress,
                stopIndex = stopIndex,
                distanceCovered = distanceCovered,
                distanceLeft = distanceLeft,
                totalStops = stops.size - 1,
                stopDistance = stopDistance,
                onIsKmChanged = { isKm = it },
                onLoadProgressChanged = { loadProgress = it },
                onStopNumberChanged = { stopIndex = it },
                onDistanceCoveredChanged = { distanceCovered = it },
                onDistanceLeftChanged = { distanceLeft = it }
            )
        }
    }
}

@Composable
fun JourneyProgress(totalStops: Int, stopIndex: Int) {
    val progressAnimate by animateFloatAsState(
        targetValue = 1f * stopIndex / totalStops,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = ""
    )
    Surface(
        color = Color(color = 0xFFD0BCFF),
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(width = 320.dp, height = 80.dp)
        ) {
            LinearProgressIndicator(
                progress = progressAnimate,
                color = Color(0xFF9CCC65),
                modifier = Modifier
                    .height(6.dp)
                    .width(256.dp)
                    .clip(RoundedCornerShape(8.dp)),
                trackColor = Color.DarkGray,
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            Text(
                text = "Progress: ${String.format("%.2f", stopIndex * 100.0 / totalStops)}%",
                fontWeight = FontWeight.Bold,
                color = Color(color = 0xFF410C5C)
            )
        }
    }
}

@Composable
fun JourneyDetails(
    stops: List<String>,
    isKm: Boolean,
    stopIndex: Int,
    stopDistance: Int,
    distanceCovered: Int,
    distanceLeft: Int,
    totalStops: Int
) {
    val distanceUnit = if(isKm) "Km" else "Miles"
    val convertDistanceUnit = if(isKm) 1.0 else 0.6213
    Surface(
        color = Color(color = 0xFFD0BCFF),
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .size(width = 320.dp, height = 124.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Current Stop: ${stops[stopIndex]}",
                fontWeight = FontWeight.Bold,
                color = Color(color = 0xFF410C5C)
            )
            Text(
                text = if(stopIndex != totalStops) "Next Stop: ${stops[stopIndex + 1]}" else "Journey",
                fontWeight = FontWeight.Bold,
                color = Color(color = 0xFF410C5C)
            )
            Text(
                text = if(stopIndex != totalStops) "Distance b/w Stops: ${String.format("%.2f", stopDistance * convertDistanceUnit)} $distanceUnit" else "Completed",
                fontWeight = FontWeight.Bold,
                color = Color(color = 0xFF410C5C)
            )
            Text(
                text = "Total Distance Covered: ${String.format("%.2f", distanceCovered * convertDistanceUnit)} $distanceUnit",
                color = Color(color = 0xFF410C5C)
            )
            Text(
                text = "Total Distance Left: ${String.format("%.2f", distanceLeft * convertDistanceUnit)} $distanceUnit",
                color = Color(color = 0xFF410C5C)
            )
        }
    }
}

@Composable
fun MaintainStops(stops: List<String>, stopIndex: Int) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .border(width = 4.dp, color = Color(color = 0xFFBBA9E5))
                .size(width = 320.dp, height = 405.dp), // 404.dp or 405.dp
//            contentAlignment = Alignment.Center,
        ) {
            if(stops.size <= 10) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    val iterator = (stops).iterator()
                    for ((idx, stopName) in iterator.withIndex()) {
                        ShowStop(stopName = stopName, idx = idx, stopIndex = stopIndex)
                    }
                }
            }
            else {
                LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                    itemsIndexed(items = stops) {
                        idx, stopName -> ShowStop(stopName = stopName, idx = idx, stopIndex = stopIndex)
                    }
                }
            }
        }
    }
}

@Composable
fun ShowStop(stopName: String, idx: Int, stopIndex: Int) {
    val stopColor =
        if(idx < stopIndex) Color(0xFFE86666)
        else if(idx == stopIndex) Color(0xFF9CCC65)
        else Color(color = 0xFFD0BCFF)
    val stopWeight =
        if(idx == stopIndex) FontWeight.Bold
        else FontWeight.Normal
    Surface(
        color = stopColor,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp), // 18.dp or 5.dp
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stopName,
                fontWeight = stopWeight,
                color = Color.Black
            )
        }
    }
}

@Composable
fun AddOperations(
    isKm: Boolean,
    loadProgress: Boolean,
    stopIndex: Int,
    distanceCovered: Int,
    distanceLeft: Int,
    totalStops: Int,
    stopDistance: Int,
    onIsKmChanged: (Boolean) -> Unit,
    onLoadProgressChanged: (Boolean) -> Unit,
    onStopNumberChanged: (Int) -> Unit,
    onDistanceCoveredChanged: (Int) -> Unit,
    onDistanceLeftChanged: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row {
            OutlinedButton(
                onClick = { onIsKmChanged(!isKm) },
                modifier = Modifier.size(width = 140.dp, height = 44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(color = 0xFFBBA9E5)
                )
            ) {
                Text(
                    text = if(isKm) "To Miles" else "To Km",
                    fontWeight = FontWeight.Bold,
                    color = Color(color = 0xFF410C5C)
                )
            }
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            OutlinedButton(
                onClick = {
                    if(stopIndex != totalStops) {
                        onStopNumberChanged(stopIndex + 1)
                        onDistanceCoveredChanged(distanceCovered + stopDistance)
                        onDistanceLeftChanged(distanceLeft - stopDistance)
                    }
                    else {
                        onStopNumberChanged(0)
                        onDistanceCoveredChanged(0)
                        onDistanceLeftChanged(totalStops * stopDistance)
                    }
                },
                modifier = Modifier.size(width = 140.dp, height = 44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(color = 0xFFBBA9E5)
                )
            ) {
                Text(
                    text = if(stopIndex != totalStops) "To Next Stop" else "Reset",
                    fontWeight = FontWeight.Bold,
                    color = Color(color = 0xFF410C5C)
                )
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        OutlinedButton(
            onClick = { onLoadProgressChanged(!loadProgress) },
            modifier = Modifier.size(width = 140.dp, height = 44.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(color = 0xFFBBA9E5)
            )
        ) {
            Text(
                text = if(loadProgress) "Hide Progress" else "Load Progress",
                fontWeight = FontWeight.Bold,
                color = Color(color = 0xFF410C5C)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JourneyPreview() {
    Assignment_1Theme {
        MyApp()
    }
}