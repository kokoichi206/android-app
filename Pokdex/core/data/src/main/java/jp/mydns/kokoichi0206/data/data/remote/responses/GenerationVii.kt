package jp.mydns.kokoichi0206.data.data.remote.responses


import com.google.gson.annotations.SerializedName

data class GenerationVii(
    val icons: jp.mydns.kokoichi0206.data.data.remote.responses.Icons,
    @SerializedName("ultra-sun-ultra-moon")
    val ultraSunUltraMoon: jp.mydns.kokoichi0206.data.data.remote.responses.UltraSunUltraMoon
)