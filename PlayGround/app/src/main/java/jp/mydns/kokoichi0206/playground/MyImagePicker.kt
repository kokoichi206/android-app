package jp.mydns.kokoichi0206.playground

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import java.io.File

@Composable
fun MyImagePickerWithScaffold() {
    var backPressedCount by remember {
        mutableStateOf(0)
    }
    BackHandler(enabled = true) {
        backPressedCount++
        Log.d("hoge", "backPressedCount $backPressedCount")
    }
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
//                    .statusBarsPadding()
                    .navigationBarsPadding(),
                onClick = {
                    // Trigger a call to the currently added callbacks.
                    dispatcher.onBackPressed()
                },
                contentColor = Color.Cyan,
                shape = RoundedCornerShape(10.dp),
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Floating action button icon")
            }
        },
    ) {
        MyImagePicker(it)
    }
}

@Composable
fun MyImagePicker(
    paddingValues: PaddingValues,
) {

    val context = LocalContext.current
    val fileName = "test_file.jpg"
    val defaultUri = "android.resource://${context.packageName}/drawable/ic_person"

    var selectedImage by remember {
        mutableStateOf(Uri.EMPTY)
//        mutableStateOf(Uri.parse(defaultUri))
    }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        Log.d("hoge", "uri: $uri")
        uri?.let {
            selectedImage = uri
            // If you need access to a URI even after app is closed.
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )

            val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                fos.write(inputStream.readBytes())
            }
        }
    }
    LaunchedEffect(key1 = true) {
        context.getFileStreamPath(fileName)?.let { file ->
            Log.d("hoge", "file.isFile: ${file.isFile}")
            Log.d("hoge", "file.freeSpace: ${file.freeSpace}")
            if (file.isFile) {
                selectedImage = Uri.fromFile(file)
            }
        }
    }
//    try {
//        val file = context.getFileStreamPath(fileName)
//        selectedImage = Uri.fromFile(file)
//    } catch (e: Exception) {
//        e.localizedMessage?.let {
//            Log.d("hoge", it)
//        }
//    }

//    val defaultUri = "content://media/picker/0/com.android.providers.media.photopicker/media/1000000029"
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Red),
            painter = painterResource(id = R.drawable.background),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Button(
                    onClick = {
                        selectedImage = Uri.EMPTY
                        context.deleteFile(fileName)
                    },
                ) {
                    Text(
                        text = "Reset Image"
                    )
                }
            }
            Image(
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colors.secondary, CircleShape)
                    .background(Color.White)
                    .clickable {
                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                painter = rememberAsyncImagePainter(
                    if (Uri.EMPTY.equals(selectedImage)) {
                        Uri.parse(defaultUri)
                    } else {
                        selectedImage
                    }
//                    selectedImage
                ),
                contentDescription = "",
                contentScale = ContentScale.Crop,
            )
        }
    }
}
