package com.example.yolofitclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.yolofitclient.ui.nav.AppNavigation
import com.example.yolofitclient.ui.theme.YoloFitClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YoloFitClientTheme {
                AppNavigation()
            }
        }
    }
}



