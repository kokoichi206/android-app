package jp.mydns.kokoichi0206.newsapp.models

data class TopNewsResponse(
    val status: String? = null,
    val totalResults : Int? = null,
    val articles: List<TopNewsArticle>? = null,
)
