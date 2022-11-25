package jp.mydns.kokoichi0206.tictactoe.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import jp.mydns.kokoichi0206.tictactoe.data.KtorRealtimeMessagingClient
import jp.mydns.kokoichi0206.tictactoe.data.RealtimeMessagingClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(Logging)
            install(WebSockets)
        }
    }

    @Singleton
    @Provides
    fun provideRealtimeMessagingClient(httpClient: HttpClient): RealtimeMessagingClient {
        return KtorRealtimeMessagingClient(httpClient)
    }
}
