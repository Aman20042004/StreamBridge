package com.example.streambridge



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.streambridge.navigation.AppNavGraph
import com.example.streambridge.ui.screens.splash.SplashScreen
import com.example.streambridge.ui.theme.StreamBridgeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            StreamBridgeTheme {
                AppNavGraph()
            }
        }
    }
}


///// Aman dubey