package jp.mydns.kokoichi0206.newsapp.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import jp.mydns.kokoichi0206.newsapp.MainApp
import jp.mydns.kokoichi0206.newsapp.models.TopNewsResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = getApplication<MainApp>().repository

    private val _newsResponse = MutableStateFlow(TopNewsResponse())
    val newsResponse: StateFlow<TopNewsResponse>
        get() = _newsResponse

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    val errorHandler = CoroutineExceptionHandler { _, error ->
        if (error is Exception) {
            _isError.value = true
//            _errorMessage.value = error.localizedMessage ?: "An Error occurred"
            _errorMessage.value = "An Error occurred"
            Log.d("hoge", "error.localizedMessage: ${error.localizedMessage}")
            Log.d("hoge", "error.message: ${error.message}")
            Log.d("hoge", "error.cause: ${error.cause}")
            Log.d("hoge", "isError: ${isError.value}")
            _isLoading.value = false
        }
    }

    fun getTopArticles() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            delay(1200L)
            _newsResponse.value = repository.getArticles()
            _isLoading.value = false
        }
    }
}