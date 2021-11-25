package com.example.splashscreen.presentation.main_feed

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.presentation.components.Post
import com.example.splashscreen.presentation.components.StandardScaffold

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
            likeCount = 18023,
            commentCount = 723
        )
    )
}
