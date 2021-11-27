package com.example.splashscreen.presentation.main_feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.splashscreen.R
import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.presentation.components.Post
import com.example.splashscreen.presentation.components.StandardToolbar
import com.example.splashscreen.presentation.util.Screen

@Composable
fun MainFeedScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        StandardToolbar(
            navController = navController,
            title = {
                Text(
                    text = stringResource(id = R.string.your_feet),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            showBackArrow = true,
            navActions = {
                IconButton(onClick = {
                    navController.navigate(Screen.SearchScreen.route)
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }
        )
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
            ),
            onPostClick = {
                navController.navigate(Screen.PostDetailScreen.route)
            }
        )
    }
}
