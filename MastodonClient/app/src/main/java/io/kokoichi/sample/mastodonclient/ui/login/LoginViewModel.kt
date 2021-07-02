package io.kokoichi.sample.mastodonclient.ui.login

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.kokoichi.sample.mastodonclient.repository.TootRepository
import io.kokoichi.sample.mastodonclient.repository.UserCredentialRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LoginViewModel (
    private val instanceUrl: String,
    private val coroutineScope: CoroutineScope,
    application: Application
        ) : AndroidViewModel(application) {
}