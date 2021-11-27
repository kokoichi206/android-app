package com.example.splashscreen.domain.util

sealed class ActivityAction {
    object LikedPost: ActivityAction()
    object CommentedOnPost: ActivityAction()
}
