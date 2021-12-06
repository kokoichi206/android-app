package com.example.splashscreen.feature_post.domain.use_case

import androidx.paging.PagingData
import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.feature_post.domain.repository.PostRepository
import com.example.splashscreen.util.Resource
import kotlinx.coroutines.flow.Flow

class GetPostForFollowsUseCase(
    private val repository: PostRepository
) {

    operator fun invoke(): Flow<PagingData<Post>> {
        return repository.posts
    }
}