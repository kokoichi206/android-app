package com.example.splashscreen.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.splashscreen.R
import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.domain.models.User
import com.example.splashscreen.presentation.components.Post
import com.example.splashscreen.presentation.profile.components.BannerSection
import com.example.splashscreen.presentation.profile.components.ProfileHeaderSection
import com.example.splashscreen.presentation.ui.theme.ProfilePictureSizeLarge
import com.example.splashscreen.presentation.ui.theme.SpaceMedium
import com.example.splashscreen.presentation.util.Screen

@Composable
fun ProfileScreen(
    navController: NavController,
) {
    val lazyListState = rememberLazyListState()
    var toolBarOffsetY by remember {
        mutableStateOf(0f)
    }
    val toolbarHeightCollapsed = 56.dp
    val bannerHeight = (LocalConfiguration.current.screenWidthDp / 2.5f).dp
    val toolbarHeightExpanded = remember {
        bannerHeight + ProfilePictureSizeLarge
    }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolBarOffsetY + delta
//                toolBarOffsetY = newOffset.coerceIn(
//                    minimumValue =
//                )

                return super.onPreScroll(available, source)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            item {
                Spacer(
                    modifier = Modifier
                        .height(toolbarHeightExpanded - ProfilePictureSizeLarge / 2f)
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
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            BannerSection(
                modifier = Modifier.height(bannerHeight)
            )
            Image(
                painter = painterResource(id = R.drawable.mayu),
                contentDescription = stringResource(id = R.string.profile_image),
                modifier = Modifier
                    .align(CenterHorizontally)
                    .graphicsLayer {
                        translationY = -ProfilePictureSizeLarge.toPx() / 2f
                    }
                    .size(ProfilePictureSizeLarge)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onSurface,
                        shape = CircleShape
                    )
            )


        }
    }
}
