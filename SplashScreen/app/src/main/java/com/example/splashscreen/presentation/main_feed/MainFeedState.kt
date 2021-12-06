package com.example.splashscreen.presentation.main_feed

import com.example.splashscreen.domain.models.Post

data class MainFeedState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val page: Int = 0,
)
