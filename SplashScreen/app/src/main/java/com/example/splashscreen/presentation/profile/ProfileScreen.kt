package com.example.splashscreen.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.splashscreen.R
import com.example.splashscreen.domain.models.Activity
import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.domain.models.User
import com.example.splashscreen.domain.util.ActivityAction
import com.example.splashscreen.domain.util.DateFormatUtil
import com.example.splashscreen.presentation.activity.ActivityItem
import com.example.splashscreen.presentation.components.Post
import com.example.splashscreen.presentation.components.StandardScaffold
import com.example.splashscreen.presentation.components.StandardToolbar
import com.example.splashscreen.presentation.profile.components.BannerSection
import com.example.splashscreen.presentation.profile.components.ProfileHeaderSection
import com.example.splashscreen.presentation.ui.theme.ProfilePictureSizeLarge
import com.example.splashscreen.presentation.ui.theme.SpaceMedium
import com.example.splashscreen.presentation.util.Screen
import kotlin.random.Random

@Composable
fun ProfileScreen(
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        StandardToolbar(
            navController = navController,
            title = {
                Text(
                    text = stringResource(id = R.string.your_profile),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            showBackArrow = false,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            item {
                BannerSection(
                    modifier = Modifier
                        .aspectRatio(2.5f)
                )
            }
            item {
                ProfileHeaderSection(
                    user = User(
                        profilePictureUrl = "",
                        username = "mayu",
                        description = "Lorem ipsum dolor sit amet, consectetur adipiscing " +
                                "elit. Sed et efficitur orci. Mauris id elit a mauris accumsan luctus." +
                                "elit. Sed et efficitur orci. Mauris id elit a mauris accumsan .",
                        followerCount = 234,
                        followingCount = 424,
                        postCount = 32432,
                    )
                )
            }
            items(20) {
                Spacer(
                    modifier = Modifier
                        .height(SpaceMedium)
                        .offset(y = -ProfilePictureSizeLarge / 2f)
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
                    },
                    modifier = Modifier
                        .offset(y = -ProfilePictureSizeLarge / 2f)
                )
            }
        }
    }
}
