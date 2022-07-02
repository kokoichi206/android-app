package jp.mydns.kokoichi0206.newsapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomMenuScreen(
    val route: String,
    val icon: ImageVector,
    val title: String,
) {

    object TopNews: BottomMenuScreen("top_news", icon = Icons.Outlined.Home, "Top News")
    object Categories: BottomMenuScreen("categories", icon = Icons.Outlined.Category, "Categories")
    object Sources: BottomMenuScreen("sources", icon = Icons.Outlined.Home, "Sources")
}
