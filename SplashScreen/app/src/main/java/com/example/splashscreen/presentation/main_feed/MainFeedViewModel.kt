package com.example.splashscreen.presentation.main_feed

import androidx.lifecycle.ViewModel
import com.example.splashscreen.feature_post.domain.use_case.PostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainFeedViewModel @Inject constructor(
    private val postUseCase: PostUseCase
): ViewModel() {


}
