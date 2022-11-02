package jp.mydns.kokoichi0206.data.data.remote.responses

import com.google.gson.annotations.SerializedName

data class GenerationV(
    @SerializedName("black-white")
    val blackWhite: jp.mydns.kokoichi0206.data.data.remote.responses.BlackWhite
)