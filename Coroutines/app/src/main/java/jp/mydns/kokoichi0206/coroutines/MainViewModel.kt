package jp.mydns.kokoichi0206.coroutines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://kokoichi0206.mydns.jp/api/"

class MainViewModel : ViewModel() {

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }

    init {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MeAPI::class.java)

        viewModelScope.launch {
            Log.d(TAG, "MainViewModel#init() is called")
            delay(1000L)
            val response = api.getMyInfo()
            Log.d(TAG, "response.isSuccessful: " + response.isSuccessful)
            Log.d(TAG, "response.message(): " + response.message())
            if (response.isSuccessful) {
                Log.d(TAG, "response: " + response.body())
            }
        }
    }
}