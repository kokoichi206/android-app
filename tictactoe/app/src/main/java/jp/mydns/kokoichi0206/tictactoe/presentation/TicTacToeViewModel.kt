package jp.mydns.kokoichi0206.tictactoe.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.mydns.kokoichi0206.tictactoe.data.GameState
import jp.mydns.kokoichi0206.tictactoe.data.MakeTurn
import jp.mydns.kokoichi0206.tictactoe.data.RealtimeMessagingClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class TicTacToeViewModel @Inject constructor(
    private val client: RealtimeMessagingClient,
) : ViewModel() {

    val state = client
        .getGameStateStream()
        .onStart { _isConnecting.value = true }
        .onEach { _isConnecting.value = false }
        .catch { e ->
            // catch an exception
            _showConnectionError.value = e is ConnectException
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameState())

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting = _isConnecting.asStateFlow()

    private val _showConnectionError = MutableStateFlow(false)
    val showConnectionError = _showConnectionError.asStateFlow()

    fun finishTurn(x: Int, y: Int) {
        if (state.value.field[y][x] != null || state.value.winningPlayer != null) {
            return
        }

        viewModelScope.launch {
            client.sendAction(MakeTurn(x, y))
        }
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.launch {
            client.close()
        }
    }
}
