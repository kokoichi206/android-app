package com.example.splashscreen.presentation.main_feed

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.presentation.components.Post

@Composable
fun MainFeedScreen(
    navController: NavController
) {
    Post(
        post = Post(
            username = "Tamura",
            imageUrl = "",
            profilePictureUrl = "",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing " +
                    "elit. Sed et efficitur orci. Mauris id elit a mauris accumsan luctus." +
                    "elit. Sed et efficitur orci. Mauris id elit a mauris accumsan .",
            likeCount = 18,
            commentCount = 7
        )
    )
}
