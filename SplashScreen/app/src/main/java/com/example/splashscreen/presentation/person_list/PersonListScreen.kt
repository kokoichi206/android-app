package com.example.splashscreen.presentation.person_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.splashscreen.R
import com.example.splashscreen.domain.models.User
import com.example.splashscreen.presentation.components.StandardToolbar
import com.example.splashscreen.presentation.components.UserProfileItem
import com.example.splashscreen.presentation.ui.theme.IconSizeMedium
import com.example.splashscreen.presentation.ui.theme.SpaceLarge
import com.example.splashscreen.presentation.ui.theme.SpaceMedium

@ExperimentalMaterialApi
@Composable
fun PersonListScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardToolbar(navController = navController,
            showBackArrow = true,
            title = {
                Text(
                    text = stringResource(id = R.string.liked_by),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground
                )
            },
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(SpaceLarge)
        ) {
            items(10) {
                UserProfileItem(
                    user = User(
                        profilePictureUrl = "",
                        username = "mayu",
                        description = "Lorem ipsum dolor sit amet, consectetur adipiscing " +
                                "elit. Sed et efficitur orci. Mauris id elit a mauris accumsan luctus." +
                                "elit. Sed et efficitur orci. Mauris id elit a mauris accumsan .",
                        followerCount = 234,
                        followingCount = 424,
                        postCount = 32432,
                    ),
                    actionIcon = {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onBackground,
                            modifier = Modifier.size(IconSizeMedium)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
            }
        }
    }
}
