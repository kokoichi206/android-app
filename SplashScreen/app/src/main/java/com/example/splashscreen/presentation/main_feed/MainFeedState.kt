package com.example.splashscreen.presentation.main_feed

import com.example.splashscreen.domain.models.Post

data class MainFeedState(
    val isLoadingFirstTime: Boolean = true,
    val isLoadingNewPosts: Boolean = false,
    val page: Int = 0,
)
