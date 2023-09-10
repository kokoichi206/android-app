package jp.mydns.kokoichi0206.effecthandler

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//@HiltViewModel
//class MainViewModel @Inject constructor(
//    @LightSensorImpl private val lightSensor: MeasurableSensor,
//    @ProximitySensorImpl private val proximitySensor: MeasurableSensor,
//    @StepCounterSensorImpl private val stepCounterSensor: MeasurableSensor,
//) : ViewModel() {

class MainViewModel(
    private val lightSensor: MeasurableSensor,
     private val proximitySensor: MeasurableSensor,
     private val stepCounterSensor: MeasurableSensor,
) : ViewModel() {
    var isDark by mutableStateOf(false)

    // 権限が付与されていて、使用できる状態か
    var isReady by mutableStateOf(false)

    var stepValues by mutableStateOf("")

    init {
        lightSensor.startListening()
        lightSensor.setOnSensorValuesChangedListener { values ->
            val lux = values[0]
            isDark = lux < 60f
        }

        proximitySensor.startListening()
        proximitySensor.setOnSensorValuesChangedListener { values ->
            Log.d("hoge", "proximity values: $values")
        }
    }

    fun setGranted() {
        isReady = true

        stepCounterSensor.startListening()
        stepCounterSensor.setOnSensorValuesChangedListener { values ->
            Log.d("hoge", "step counter values: $values")
            stepValues = values.toString()
        }
        Log.d("hoge", stepCounterSensor.doesSensorExist.toString());
    }
}
