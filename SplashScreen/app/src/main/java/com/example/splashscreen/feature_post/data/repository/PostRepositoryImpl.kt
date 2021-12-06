package com.example.splashscreen.feature_post.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.splashscreen.R
import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.feature_auth.data.dto.request.CreateAccountRequest
import com.example.splashscreen.feature_post.data.data_source.remote.PostApi
import com.example.splashscreen.feature_post.data.paging.PostSource
import com.example.splashscreen.feature_post.domain.repository.PostRepository
import com.example.splashscreen.util.Constants
import com.example.splashscreen.util.Resource
import com.example.splashscreen.util.UiText
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class PostRepositoryImpl(
    private val api: PostApi
) : PostRepository {

    override val posts: Flow<PagingData<Post>>
        get() = Pager(PagingConfig(pageSize = Constants.PAGE_SIZE_POSTS)) {
            PostSource(api)
        }.flow
}