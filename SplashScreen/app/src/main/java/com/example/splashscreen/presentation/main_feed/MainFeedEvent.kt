package com.example.splashscreen.presentation.main_feed

sealed class MainFeedEvent {

    object LoadMorePosts: MainFeedEvent()
    object LoadedPage: MainFeedEvent()
}
