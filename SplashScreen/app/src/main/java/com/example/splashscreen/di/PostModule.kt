package com.example.splashscreen.di

import com.example.splashscreen.feature_post.data.data_source.remote.PostApi
import com.example.splashscreen.feature_post.data.repository.PostRepositoryImpl
import com.example.splashscreen.feature_post.domain.repository.PostRepository
import com.example.splashscreen.feature_post.domain.use_case.GetPostForFollowsUseCase
import com.example.splashscreen.feature_post.domain.use_case.PostUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostModule {

    @Provides
    @Singleton
    fun providePostApi(client: OkHttpClient): PostApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(PostApi.BASE_URL)
            .client(client)
            .build()
            .create(PostApi::class.java)
    }

    @Provides
    @Singleton
    fun providePostRepository(api: PostApi): PostRepository {
        return PostRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun providePostUseCase(repository: PostRepository): PostUseCase {
        return PostUseCase(
            getPostForFollowsUseCase = GetPostForFollowsUseCase(repository)
        )
    }
}