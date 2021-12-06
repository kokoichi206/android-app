package com.example.splashscreen.feature_post.domain.use_case

import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.feature_post.domain.repository.PostRepository
import com.example.splashscreen.util.Resource

class GetPostForFollowsUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(
        page: Int,
        pageSize: Int
    ): Resource<List<Post>> {
        return repository.getPostsForFollows(page, pageSize)
    }
}