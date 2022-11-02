package jp.mydns.kokoichi0206.data.data.remote.responses

import com.google.gson.annotations.SerializedName

data class GenerationVi(
    @SerializedName("omegaruby-alphasapphire")
    val omegarubyAlphasapphire: jp.mydns.kokoichi0206.data.data.remote.responses.OmegarubyAlphasapphire,
    @SerializedName("x-y")
    val xY: jp.mydns.kokoichi0206.data.data.remote.responses.XY
)