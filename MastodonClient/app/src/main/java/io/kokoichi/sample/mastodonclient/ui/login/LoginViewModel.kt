package io.kokoichi.sample.mastodonclient.ui.login

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.kokoichi.sample.mastodonclient.repository.TootRepository
import io.kokoichi.sample.mastodonclient.entity.UserCredential
import io.kokoichi.sample.mastodonclient.repository.UserCredentialRepository
import io.kokoichi.sample.mastodonclient.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LoginViewModel(
    private val instanceUrl: String,
    private val coroutineScope: CoroutineScope,
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private val TAG = AndroidViewModel::class.java.simpleName
    }

    private val authRepository = AuthRepository(instanceUrl)
    private val userCredentialRepository = UserCredentialRepository(
        application
    )

    val accessTokenSaved = MutableLiveData<UserCredential>()

    fun requestAccessToken(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        scopes: String,
        code: String
    ) {
        coroutineScope.launch {
            val responseToken = authRepository.token(
                clientId,
                clientSecret,
                redirectUri,
                scopes,
                code
            )

            Log.d(TAG, responseToken.accessToken)

            val userCredential = UserCredential(
                instanceUrl = instanceUrl,
                accessToken = responseToken.accessToken
            )
            userCredentialRepository.set(userCredential)

            accessTokenSaved.postValue(userCredential)
        }
    }
}