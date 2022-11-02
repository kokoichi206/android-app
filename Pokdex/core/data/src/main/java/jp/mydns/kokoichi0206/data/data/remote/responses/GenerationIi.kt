package jp.mydns.kokoichi0206.data.data.remote.responses


import com.google.gson.annotations.SerializedName

data class GenerationIi(
    val crystal: jp.mydns.kokoichi0206.data.data.remote.responses.Crystal,
    val gold: jp.mydns.kokoichi0206.data.data.remote.responses.Gold,
    val silver: jp.mydns.kokoichi0206.data.data.remote.responses.Silver
)