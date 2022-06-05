package jp.mydns.kokoichi0206.effecthandler

import android.media.effect.Effect
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.mydns.kokoichi0206.effecthandler.ui.theme.EffectHandlerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EffectHandlerTheme {

                val viewModel = viewModel<MainViewModel>()
                val isDark = viewModel.isDark
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isDark) Color.DarkGray else Color.White
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isDark) {
                            "It's dark outside"
                        } else {
                            "It's light outside"
                        },
                        color = if (isDark) Color.White else Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
fun Effect() {
    var text by remember {
        mutableStateOf("")
    }
    EffectHandlerTheme {
        // LaunchedEffect is also Composable
        LaunchedEffect(key1 = text) {
            delay(1000L)
            println("The text is $text")
        }
    }
}

@Composable
fun RememberCoroutineScopeDemo() {
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            delay(1000L)
            println("Hello world!")
        }
    }) {

    }
}

@Composable
fun RememberUpdatedStateDemo(
    onTimeout: () -> Unit,
) {
    val updatedOnTimeout by rememberUpdatedState(newValue = onTimeout)
    LaunchedEffect(true) {
        delay(3000L)
        onTimeout()
    }
}

@Composable
fun DisposableEffectDemo() {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                println("On pause called")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun SideEffectDemo() {
    SideEffect {
        println("Called after every successful recomposition")
    }
}

@Composable
fun DerivedStateOfD() {
    var counter by remember {
        mutableStateOf(0)
    }
    val counterText by derivedStateOf {
        "The counter is $counter"
    }
    Button(onClick = { counter++ }) {
        Text(text = counterText)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EffectHandlerTheme {
        Greeting("Android")
    }
}