package jp.mydns.kokoichi0206.canvas

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import jp.mydns.kokoichi0206.canvas.ui.theme.CanvasTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE = 1

    @OptIn(ExperimentalTextApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val GradientColors = listOf(Color.Red, Color.Yellow, Color.Blue)

            CanvasTheme {
                val snapShot = CaptureBitmap {
                    EditPictureView()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Button(
                        onClick = {
                            MainScope().launch {
                                val bitmap = snapShot.invoke()
                                onClick(bitmap)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                        contentPadding = PaddingValues(4.dp),
                    ) {
                        Text(
                            text = "Capture",
                            fontSize = 20.sp,
                            style = TextStyle(
                                brush = Brush.linearGradient(
                                    colors = GradientColors
                                )
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun onClick(bitmap: Bitmap) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            saveImageToGallery(bitmap)
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_CODE
            )
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap) {

        val contentResolver = contentResolver

        val images = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/")

        val uri = contentResolver.insert(images, contentValues)

        try {
            uri?.let {
                val outputStream = contentResolver.openOutputStream(uri)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                Toast.makeText(this, "Images Saved Successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Images not Saved", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
