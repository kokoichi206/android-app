package jp.mydns.kokoichi0206.data.data.remote.responses


import com.google.gson.annotations.SerializedName

data class GenerationI(
    @SerializedName("red-blue")
    val redBlue: jp.mydns.kokoichi0206.data.data.remote.responses.RedBlue,
    val yellow: jp.mydns.kokoichi0206.data.data.remote.responses.Yellow
)