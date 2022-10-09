package jp.mydns.kokoichi0206.encryption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.mydns.kokoichi0206.encryption.ui.theme.EncryptionTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cryptoManager = CryptoManager()

        setContent {
            EncryptionTheme {
                var messageToEncrypt by remember {
                    mutableStateOf("")
                }
                var messageToDecrypt by remember {
                    mutableStateOf("")
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    TextField(
                        value = messageToEncrypt,
                        onValueChange = { messageToEncrypt = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "Encrypt string") },
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(onClick = {
                            val bytes = messageToEncrypt.encodeToByteArray()
                            val file = File(filesDir, "secret.txt")
                            if (!file.exists()) {
                                file.createNewFile()
                            }
                            val fos = FileOutputStream(file)

                            messageToDecrypt = cryptoManager.encrypt(
                                bytes = bytes,
                                outputStream = fos,
                            ).decodeToString()
                        }) {
                            Text(text = "Encrypt")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            val file = File(filesDir, "secret.txt")
                            messageToEncrypt = cryptoManager.decrypt(
                                inputStream = FileInputStream(file)
                            ).decodeToString()
                        }) {
                            Text(text = "Decrypt")
                        }
                    }

                    Text(text = messageToDecrypt)

                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = ParagraphStyle(lineHeight = 30.sp)) {
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red
                                    )
                                ) {
                                    append("Don't touch the \"Encrypt\" twice.\n")
                                }
                                withStyle(style = SpanStyle(color = Color.Blue)) {
                                    append("The following error message shows up.\n")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        background = Color.LightGray
                                    )
                                )  {
                                    append("java.lang.IllegalStateException: IV has already been used. Reusing IV in encryption mode violates security ")
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EncryptionTheme {
        Greeting("Android")
    }
}