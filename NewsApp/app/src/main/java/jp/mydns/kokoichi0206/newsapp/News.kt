package jp.mydns.kokoichi0206.newsapp

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.mydns.kokoichi0206.newsapp.components.BottomMenu
import jp.mydns.kokoichi0206.newsapp.screen.*

@Composable
fun News() {
    val scrollState = rememberScrollState()
    val navController = rememberNavController()
    MainScreen(navController = navController, scrollState = scrollState)
}

@Composable
fun MainScreen(
    navController: NavHostController,
    scrollState: ScrollState,
) {
    Scaffold(
        bottomBar = {
            BottomMenu(navController = navController)
        },
    ) {
        Navigation(
            navController = navController,
            scrollState = scrollState,
        )
    }
}

@Composable
fun Navigation(
    navController: NavHostController,
    scrollState: ScrollState,
) {

    NavHost(
        navController = navController,
        startDestination = BottomMenuScreen.TopNews.route,
    ) {
        // Use extended one !
        bottomNavigation(navController = navController)

        composable(Screen.TopNews.route) {
            TopNews(
                navController = navController,
            )
        }

        composable(
            route = "${Screen.Detailed.route}/{newsId}",
            arguments = listOf(navArgument("newsId") { type = NavType.IntType }),
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getInt("newsId")
            val newsData = MockData.getNewsById(id)

            DetailScreen(
                newsData = newsData,
                scrollState = scrollState,
                navController = navController,
            )
        }
    }
}

// Extend navgraphbuilder !!!!
fun NavGraphBuilder.bottomNavigation(navController: NavController) {
    composable(BottomMenuScreen.TopNews.route) {
        TopNews(navController = navController)
    }

    composable(BottomMenuScreen.Categories.route) {
        Categories()
    }

    composable(BottomMenuScreen.Sources.route) {
        Sources()
    }
}
