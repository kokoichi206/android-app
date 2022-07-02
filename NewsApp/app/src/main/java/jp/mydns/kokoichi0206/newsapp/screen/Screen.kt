package jp.mydns.kokoichi0206.newsapp.screen

sealed class Screen(val route: String) {
    object TopNews : Screen("top_news")
    object Detailed : Screen("detailed_news")
}
