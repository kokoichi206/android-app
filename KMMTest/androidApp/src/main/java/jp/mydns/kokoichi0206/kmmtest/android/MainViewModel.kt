package jp.mydns.kokoichi0206.kmmtest.android

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.mydns.kokoichi0206.kmmtest.SpaceXSDK
import jp.mydns.kokoichi0206.kmmtest.cache.DatabaseDriverFactory
import kotlinx.coroutines.launch

class MainViewModel(
    context: Context,
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    private val sdk = SpaceXSDK(DatabaseDriverFactory(context))

    init {
        loadLaunches(context, false)
    }

    fun loadLaunches(context: Context, needReload: Boolean) {
        state = state.copy(
            launcher = null,
            isLoading = true,
        )
        viewModelScope.launch {
            kotlin.runCatching {
                sdk.getLaunches(needReload)
            }.onSuccess {
                state = state.copy(launcher = it)
            }.onFailure {
                // FIXME: ここだけ直接このブロックの中で処理をおこなってしまっていて密。
                // FIXME: エラーが起こったことだけを残して（発信して）、別機構で受け取ってエラー表示にしたい。
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            state = state.copy(isLoading = false)
        }
    }
}