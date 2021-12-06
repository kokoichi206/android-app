package com.example.splashscreen.feature_post.data.repository

import com.example.splashscreen.R
import com.example.splashscreen.domain.models.Post
import com.example.splashscreen.feature_auth.data.dto.request.CreateAccountRequest
import com.example.splashscreen.feature_post.data.data_source.remote.PostApi
import com.example.splashscreen.feature_post.domain.repository.PostRepository
import com.example.splashscreen.util.Resource
import com.example.splashscreen.util.UiText
import retrofit2.HttpException
import java.io.IOException

class PostRepositoryImpl(
    private val api: PostApi
) : PostRepository{

    override suspend fun getPostsForFollows(page: Int, pageSize: Int): Resource<List<Post>> {
        return try {
            val posts = api.getPostsForFollows(page, pageSize)
            Resource.Success(posts)
        } catch(e: IOException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.error_coulnt_reach_server)
            )
        }catch(e: HttpException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.oopt_something_went_wrong)
            )
        }
    }
}