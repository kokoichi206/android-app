package com.example.splashscreen.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.splashscreen.R
import com.example.splashscreen.domain.models.User
import com.example.splashscreen.presentation.components.StandardTextField
import com.example.splashscreen.presentation.components.StandardToolbar
import com.example.splashscreen.presentation.components.UserProfileItem
import com.example.splashscreen.presentation.ui.theme.IconSizeMedium
import com.example.splashscreen.presentation.ui.theme.SpaceLarge
import com.example.splashscreen.presentation.ui.theme.SpaceMedium
import com.example.splashscreen.domain.states.StandardTextFieldState
import com.example.splashscreen.presentation.edit_profile.util.EditProfileError

@ExperimentalMaterialApi
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardToolbar(
            navController = navController,
            showBackArrow = true,
            title = {
                Text(
                    text = stringResource(id = R.string.edit_your_profile),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground,
                )
            },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceLarge)
        ) {
            StandardTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                text = viewModel.searchState.value.text,
                hint = stringResource(id = R.string.search),
                error = "",
                leadingIcon = Icons.Default.Search,
                onValueChange = {
                    viewModel.setSearchState(
                        StandardTextFieldState(text = it)
                    )
                }
            )
            Spacer(modifier = Modifier.height(SpaceLarge))
            LazyColumn(
                modifier = Modifier.fillMaxSize()
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
}
