package jp.mydns.kokoichi0206.tictactoe.data

import kotlinx.coroutines.flow.Flow

interface RealtimeMessagingClient {

    fun getGameStateStream(): Flow<GameState>

    suspend fun sendAction(action: MakeTurn)

    suspend fun close()
}