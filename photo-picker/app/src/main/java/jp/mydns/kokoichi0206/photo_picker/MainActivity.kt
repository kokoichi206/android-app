package jp.mydns.kokoichi0206.photo_picker

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import jp.mydns.kokoichi0206.photo_picker.ui.theme.PhotopickerTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotopickerTheme {
                val date = remember {
                    // 2022-12-24T05:21:03.512451+09:00[Asia/Tokyo]
                    ZonedDateTime.now()
//                        .toEpochSecond() * 1000
//                    ZonedDateTime.ofInstant(
//                        Instant.ofEpochMilli(System.currentTimeMillis()),
//                        ZoneId.of("Asia/Tokyo"),
//                    )

                //                    LocalDateTime.now()
//                        .plusDays(13)
                }
                Log.d("hoge", date.toString())

                val formattedDateTime = remember {
                    DateTimeFormatter
                        .ofPattern("EEE dd MM yyy HH:mm:ss")
                        .format(date)
                }
                Log.d("hoge", formattedDateTime.toString())

                var selectedImageUri by remember {
                    mutableStateOf<Uri?>(null)
                }
                var selectedImageUris by remember {
                    mutableStateOf<List<Uri>>(emptyList())
                }

                val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
                    // https://developer.android.com/reference/androidx/activity/result/contract/ActivityResultContracts.PickVisualMedia
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri -> selectedImageUri = uri },
                )
                val multiplePhotosPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickMultipleVisualMedia(),
                    onResult = { uris -> selectedImageUris = uris },
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                        ) {
                            Button(
                                onClick = {
                                    singlePhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            ) {
                                Text(text = "Pick one photo")
                            }

                            Button(
                                onClick = {
                                    multiplePhotosPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            ) {
                                Text(text = "Pick multiple photos")
                            }
                        }
                    }

                    item {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    items(selectedImageUris) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                        )
                    }
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
    PhotopickerTheme {
        Greeting("Android")
    }
}