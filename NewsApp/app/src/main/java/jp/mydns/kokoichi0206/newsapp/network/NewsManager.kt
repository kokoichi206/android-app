package jp.mydns.kokoichi0206.newsapp.network

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import jp.mydns.kokoichi0206.newsapp.models.TopNewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Separate logic from UI
class NewsManager(
    private val service: NewsService,
) {

    private val _newsResponse = mutableStateOf(TopNewsResponse())
    val newsResponse: State<TopNewsResponse>
        @Composable get() = remember {
            _newsResponse
        }

    suspend fun getArticle(country: String): TopNewsResponse = withContext(Dispatchers.IO) {
        service.getTopArticles(country = country, apiKey = "")

//        val service = Api.retrofitService.getTopArticles("us", Api.API_KEY)

//        service.enqueue(object: Callback<TopNewsResponse> {
//            override fun onResponse(call: Call<TopNewsResponse>, response: Response<TopNewsResponse>) {
//                if(response.isSuccessful) {
//                    _newsResponse.value = response.body()!!
//                    Log.d("news", "${_newsResponse.value}")
//                } else {
//                    Log.d("news", "${response.errorBody()}")
//                }
//            }
//
//            override fun onFailure(call: Call<TopNewsResponse>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//        })
    }
}