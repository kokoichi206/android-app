package jp.mydns.kokoichi0206.newsapp

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.mydns.kokoichi0206.newsapp.components.BottomMenu
import jp.mydns.kokoichi0206.newsapp.models.TopNewsArticle
import jp.mydns.kokoichi0206.newsapp.network.Api
import jp.mydns.kokoichi0206.newsapp.network.NewsManager
import jp.mydns.kokoichi0206.newsapp.screen.*
import jp.mydns.kokoichi0206.newsapp.ui.MainViewModel

@Composable
fun News(
    mainViewModel: MainViewModel,
) {
    val scrollState = rememberScrollState()
    val navController = rememberNavController()
    MainScreen(navController = navController, scrollState = scrollState, mainViewModel = mainViewModel)
}

@Composable
fun MainScreen(
    navController: NavHostController,
    scrollState: ScrollState,
    mainViewModel: MainViewModel,
) {
    Scaffold(
        bottomBar = {
            BottomMenu(navController = navController)
        },
    ) {
        Navigation(
            navController = navController,
            scrollState = scrollState,
            paddingValues = it,
            viewModel = mainViewModel
        )
    }
}

@Composable
fun Navigation(
    navController: NavHostController,
    scrollState: ScrollState,
    newsManager: NewsManager = NewsManager(Api.retrofitService),
    paddingValues: PaddingValues,
    viewModel: MainViewModel,
) {
    val loading by viewModel.isLoading.collectAsState()
    val error by viewModel.isError.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState()
    val articles = mutableListOf(TopNewsArticle())
    val topArticles = viewModel.newsResponse.collectAsState().value.articles
    articles.addAll(topArticles ?: listOf())

    articles.let {
        NavHost(
            navController = navController,
            startDestination = BottomMenuScreen.TopNews.route,
            modifier = Modifier
                .padding(paddingValues = paddingValues)
        ) {
            val isLoading = mutableStateOf(loading)
            val isError = mutableStateOf(error)
            val errorMessage = mutableStateOf(errorMsg)
            // Use extended one !
            bottomNavigation(
                navController = navController,
                articles = articles,
                isLoading = isLoading,
                isError = isError,
                errorMessage = errorMessage,
            )

            composable(
                route = "${Screen.Detailed.route}/{index}",
                arguments = listOf(navArgument("index") { type = NavType.IntType }),
            ) { navBackStackEntry ->
                val index = navBackStackEntry.arguments?.getInt("index")
                index?.let {
                    val article = articles[index]
                    DetailScreen(
                        article = article,
                        scrollState = scrollState,
                        navController = navController,
                    )
                }
            }
        }
    }
}

// Extend navgraphbuilder !!!!
fun NavGraphBuilder.bottomNavigation(
    navController: NavController,
    articles: List<TopNewsArticle>,
    isLoading: MutableState<Boolean>,
    isError: MutableState<Boolean>,
    errorMessage: MutableState<String>,
) {
    composable(BottomMenuScreen.TopNews.route) {
        TopNews(
            navController = navController,
            articles = articles,
            isLoading = isLoading,
            isError = isError,
            errorMessage = errorMessage,
        )
    }

    composable(BottomMenuScreen.Categories.route) {
        Categories()
    }

    composable(BottomMenuScreen.Sources.route) {
        Sources()
    }
}
