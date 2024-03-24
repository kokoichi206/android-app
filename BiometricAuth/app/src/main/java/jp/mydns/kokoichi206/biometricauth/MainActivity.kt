package jp.mydns.kokoichi206.biometricauth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jp.mydns.kokoichi206.biometricauth.ui.theme.BiometricAuthTheme

class MainActivity : AppCompatActivity() {
    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometricAuthTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val biometricResult by promptManager.promptResults.collectAsState(
                        initial = null,
                    )

                    // 認証なしの場合、以下 intent で登録させる。
                    val enrollLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = {
                            println("Activity RESULT: $it")
                        },
                    )
                    LaunchedEffect(biometricResult) {
                        if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
                            if (Build.VERSION.SDK_INT >= 30) {
                                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL,
                                    )
                                }
                                enrollLauncher.launch(enrollIntent)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(
                            onClick = {
                                promptManager.showBiometricPrompt(
                                    title = "pien",
                                    description = "paon",
                                )
                            },
                        ) {
                            Text(text = "authenticate!")
                        }

                        biometricResult?.let { result ->
                            Text(
                                text = when (result) {
                                    is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                                        result.error
                                    }

                                    BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                                        "Authentication Failed"
                                    }

                                    BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                                        "Authentication not set"
                                    }

                                    BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                                        "Authentication Success"
                                    }

                                    BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                                        "Feature Unavailable"
                                    }

                                    BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                                        "Hardware Unavailable"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BiometricAuthTheme {
        Greeting("Android")
    }
}