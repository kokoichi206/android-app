package jp.mydns.kokoichi206.g.widgetprac.di

import com.apollographql.apollo3.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.mydns.kokoichi206.g.widgetprac.BuildConfig
import jp.mydns.kokoichi206.g.widgetprac.data.ApolloGithubClient
import jp.mydns.kokoichi206.g.widgetprac.domain.GetUserUsecase
import jp.mydns.kokoichi206.g.widgetprac.domain.GithubClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApolloClient() : ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .addHttpHeader("Authorization", "bearer ${BuildConfig.ACCESS_TOKEN}")
            .build()
    }

    @Provides
    @Singleton
    fun provideGithubClient(apolloClient: ApolloClient): GithubClient {
        return ApolloGithubClient(apolloClient)
    }

    @Provides
    @Singleton
    fun provideGetUserUsecase(githubClient: GithubClient): GetUserUsecase {
        return GetUserUsecase(githubClient)
    }
}
