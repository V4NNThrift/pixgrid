package com.kiyoshi.pixgrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kiyoshi.pixgrid.ui.PixGridTheme
import com.kiyoshi.pixgrid.view.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PixGridTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Unspecified,
                ) {
                    HomeScreen()
                }
            }
        }
    }
}
