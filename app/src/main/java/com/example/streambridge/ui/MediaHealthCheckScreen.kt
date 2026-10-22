package com.example.streambridge.ui



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.streambridge.viewmodel.MediaViewModel
import com.example.streambridge.viewmodel.MediaViewModelFactory

@Composable
fun MediaHealthCheckScreen() {
    val vm: MediaViewModel = viewModel(
        factory = MediaViewModelFactory()
    )

    val healthStatus by vm.healthStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Media Service Test",
            style = MaterialTheme.typography.headlineSmall
        )

       Button(
                onClick = {
                    vm.checkMediaState("YOUR_EXISTING_SESSION_ID")
                }
                ) {
            Text("Check Media State")
        }

        Text(text = vm.message.collectAsState().value.toString())

        Text(text = "Status: $healthStatus")
    }
}