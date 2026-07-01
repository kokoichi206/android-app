package com.example.appscommunications

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.example.appscommunications.ui.CounterScreen
import com.example.appscommunications.ui.theme.AppsCommunicationsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppsCommunicationsTheme {
                CounterScreen(modifier = Modifier)
            }
        }
    }
}