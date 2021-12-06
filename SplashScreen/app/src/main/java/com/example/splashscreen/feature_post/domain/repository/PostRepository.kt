package com.example.splashscreen.feature_post.domain.repository

import androidx.paging.PagingData
import com.example.splashscreen.domain.models.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {

    val posts: Flow<PagingData<Post>>
}