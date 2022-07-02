package jp.mydns.kokoichi0206.newsapp.data

import jp.mydns.kokoichi0206.newsapp.network.NewsManager

class Repository(
    val manager: NewsManager,
) {
    suspend fun getArticles() = manager.getArticle("us")
}