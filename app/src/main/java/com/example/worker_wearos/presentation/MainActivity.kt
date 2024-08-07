import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.worker_wearos.presentation.theme.Worker_wearOSTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    Worker_wearOSTheme {
        var isPermissionGranted by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            if (isPermissionGranted) {
                HeartRateScreen(
                    name = "홍길동",       // Dummy data
                    location = "서울",   // Dummy data
                    initialHeartRate = 75L // Initial heart rate value
                )
            } else {
                GreetingScreen(
                    onAllow = { isPermissionGranted = true },
                    onDeny = { /* Handle denial if necessary */ }
                )
            }
        }
    }
}

@Composable
fun GreetingScreen(onAllow: () -> Unit, onDeny: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "권한 인증이 필요합니다.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Row for Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            // Allow Button
            Button(
                onClick = onAllow,
                shape = RectangleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .width(80.dp)
            ) {
                Text("권한 허용")
            }

            // Deny Button
            Button(
                onClick = onDeny,
                shape = RectangleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .width(80.dp)
            ) {
                Text("권한 거부")
            }
        }
    }
}

@Composable
fun HeartRateScreen(name: String, location: String, initialHeartRate: Long) {
    val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }
    val heartRate = remember { mutableStateOf(initialHeartRate) }
    val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = System.currentTimeMillis()
            delay(1000L) // Delay 1 second for time update
        }
    }

    // Update heart rate every 10 minutes
    LaunchedEffect(Unit) {
        while (true) {
            heartRate.value = generateRandomHeartRate()
            delay(10 * 60 * 1000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "근무자: $name",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.body1
        )
        Text(
            text = "위치: $location",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.body1
        )
        Text(
            text = "현재 심박수는 ${heartRate.value} 입니다.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.body1
        )
        if (heartRate.value > 150) {
            Text(
                text = "위급상황입니다 휴식을 취하세요",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Text(
            text = "현재 시간: ${timeFormatter.format(currentTime.value)}",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.body1
        )
    }
}

fun generateRandomHeartRate(): Long {
    return (60..150).random().toLong()
}

@Preview
@Composable
fun DefaultPreview() {
    WearApp()
}

