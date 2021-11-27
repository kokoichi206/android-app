package com.example.splashscreen.presentation.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.presentation.EditProfileScreen
import com.example.splashscreen.presentation.activity.ActivityScreen
import com.example.splashscreen.presentation.chat.ChatScreen
import com.example.splashscreen.presentation.create_post.CreatePostScreen
import com.example.splashscreen.presentation.main_feed.MainFeedScreen
import com.example.splashscreen.presentation.login.LoginScreen
import com.example.splashscreen.presentation.login.RegisterScreen
import com.example.splashscreen.presentation.post_detail.PostDetailScreen
import com.example.splashscreen.presentation.profile.ProfileScreen
import com.example.splashscreen.presentation.search.SearchScreen
import com.example.splashscreen.presentation.splash.SplashScreen

@ExperimentalMaterialApi
@Composable
fun Navigation(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SearchScreen.route,
//        startDestination = Screen.SplashScreen.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.RegisterScreen.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.MainFeedScreen.route) {
            MainFeedScreen(navController = navController)
        }
        composable(Screen.ChatScreen.route) {
            ChatScreen(navController = navController)
        }
        composable(Screen.ActivityScreen.route) {
            ActivityScreen(navController = navController)
        }
        composable(Screen.EditProfileScreen.route) {
            EditProfileScreen(navController = navController)
        }
        composable(Screen.CreatePostScreen.route) {
            CreatePostScreen(navController = navController)
        }
        composable(Screen.CreatePostScreen.route) {
            CreatePostScreen(navController = navController)
        }
        composable(Screen.SearchScreen.route) {
            SearchScreen(navController = navController)
        }
        composable(Screen.PostDetailScreen.route) {
            PostDetailScreen(
                navController = navController,
                post = Post(
                    username = "Tamura",
                    imageUrl = "",
                    profilePictureUrl = "",
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing " +
                            "elit. Sed et efficitur orci. Mauris id elit a mauris accumsan luctus." +
                            "elit. Sed et efficitur orci. Mauris id elit a mauris accumsan ." +
                            "elit. Sed et efficitur orci. Mauris id elit a mauris accumsan .",
                    likeCount = 18023,
                    commentCount = 723
                )
            )
        }
    }
}