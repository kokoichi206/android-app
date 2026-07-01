package com.example.appscommunications.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appscommunications.CounterApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel = remember(context) {
        (context.applicationContext as CounterApplication).counterViewModel
    }

    val count by viewModel.count.collectAsStateWithLifecycle()
    var showChat by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { showChat = true }) {
                Text(text = "Chat", modifier = Modifier.padding(horizontal = 16.dp))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Counter", style = MaterialTheme.typography.titleMedium)
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(vertical = 16.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { viewModel.decrement(1) }) { Text("−") }
                OutlinedButton(onClick = { viewModel.reset() }) { Text("Reset") }
                Button(onClick = { viewModel.increment(1) }) { Text("+") }
            }
        }
    }

    if (showChat) {
        ChatBottomSheet(onDismiss = { showChat = false })
    }
}