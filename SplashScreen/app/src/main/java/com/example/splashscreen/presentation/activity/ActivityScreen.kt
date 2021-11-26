package com.example.splashscreen.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.splashscreen.R
import com.example.splashscreen.domain.models.Activity
import com.example.splashscreen.domain.util.ActivityAction
import com.example.splashscreen.domain.util.DateFormatUtil
import com.example.splashscreen.presentation.components.StandardToolbar
import com.example.splashscreen.presentation.ui.theme.SpaceMedium
import com.example.splashscreen.presentation.ui.theme.SpaceSmall
import kotlin.random.Random

@Composable
fun ActivityScreen(
    navController: NavController,
    viewModel: ActivityViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardToolbar(
            navController = navController,
            title = {
                Text(
                    text = stringResource(id = R.string.your_activity),
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
            contentPadding = PaddingValues(SpaceMedium),
        ) {
            items(20) {
                ActivityItem(
                    activity = Activity(
                        username = "mayu",
                        actionType = if (Random.nextInt(2) == 0) {
                            ActivityAction.LikedPost
                        } else ActivityAction.CommentedOnPost,
                        formattedTime = DateFormatUtil.timestampToFormattedString(
                            timestamp = System.currentTimeMillis(),
                            pattern = "MMM dd, HH:mm",
                        )
                    ),
                )
                if (it < 19) {
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }
            }
        }
    }
}

