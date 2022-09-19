package jp.mydns.kokoichi0206.kmmtest.android

import jp.mydns.kokoichi0206.kmmtest.entity.RocketLaunch

data class MainState(
    val launcher: List<RocketLaunch>? = null,
    val isLoading: Boolean = false
)
