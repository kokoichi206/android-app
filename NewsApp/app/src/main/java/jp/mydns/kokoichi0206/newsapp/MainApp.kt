package jp.mydns.kokoichi0206.newsapp

import android.app.Application
import jp.mydns.kokoichi0206.newsapp.data.Repository
import jp.mydns.kokoichi0206.newsapp.network.Api
import jp.mydns.kokoichi0206.newsapp.network.NewsManager

class MainApp: Application() {

    private val manager by lazy {
        NewsManager(Api.retrofitService)
    }

    val repository by lazy {
        Repository(manager = manager)
    }
}