package com.example.splashscreen.feature_post.domain.repository

import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.util.Constants
import com.example.splashscreen.util.Resource

interface PostRepository {

    suspend fun getPostsForFollows(
        page: Int = 0,
        pageSize: Int = Constants.PAGE_SIZE_POSTS
    ): Resource<List<Post>>
}