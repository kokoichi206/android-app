package jp.mydns.kokoichi0206.datapicker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import jp.mydns.kokoichi0206.datapicker.ui.theme.DataPickerTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DataPickerTheme {
                var pickedDate by remember {
                    mutableStateOf(LocalDate.now())
                }
                var pickedTime by remember {
                    mutableStateOf(LocalTime.NOON)
                }

                val formattedDate by remember {
                    derivedStateOf {
                        DateTimeFormatter
                            .ofPattern("MMM dd yyyy")
                            .format(pickedDate)
                    }
                }
                val formattedTime by remember {
                    derivedStateOf {
                        DateTimeFormatter
                            .ofPattern("hh:mm")
                            .format(pickedTime)
                    }
                }

                val dateDialogState = rememberMaterialDialogState()
                val timeDialogState = rememberMaterialDialogState()

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Button(onClick = {
                        dateDialogState.show()
                    }) {
                        Text(text = "Pick Date")
                    }
                    Text(text = formattedDate)

                    Button(onClick = {
                        timeDialogState.show()
                    }) {
                        Text(text = "Pick Time")
                    }
                    Text(text = formattedTime)
                }

                MaterialDialog(
                    dialogState = dateDialogState,
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                    ),
                    onCloseRequest = {},
                    buttons = {
                        positiveButton(text= "Ok") {
                            Toast.makeText(
                                applicationContext,
                                "Clicked ok",
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                        negativeButton(text = "Cancel")
                    }
                ) {
//                    this.
                    datepicker(
                        initialDate = LocalDate.now(),
                        title = "Pick a date",
                        allowedDateValidator = {
                            it.dayOfMonth % 2 == 1
                        }
                    ) {
                        pickedDate = it
                    }
                }

                MaterialDialog(
                    dialogState = timeDialogState,
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                    ),
                    onCloseRequest = {},
                    buttons = {
                        positiveButton(text= "Ok") {
                            Toast.makeText(
                                applicationContext,
                                "Clicked ok",
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                        negativeButton(text = "Cancel")
                    }
                ) {
//                    this.
                    timepicker(
                        initialTime = LocalTime.NOON,
                        title = "Pick a time",
                        timeRange = LocalTime.MIDNIGHT..LocalTime.NOON
                    ) {
                        pickedTime = it
                    }
                }
            }
        }
    }
}
