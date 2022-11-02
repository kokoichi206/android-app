package jp.mydns.kokoichi0206.data.data.remote.responses

import com.google.gson.annotations.SerializedName

data class GenerationIii(
    val emerald: jp.mydns.kokoichi0206.data.data.remote.responses.Emerald,
    @SerializedName("firered-leafgreen")
    val fireredLeafgreen: jp.mydns.kokoichi0206.data.data.remote.responses.FireredLeafgreen,
    @SerializedName("ruby-sapphire")
    val rubySapphire: jp.mydns.kokoichi0206.data.data.remote.responses.RubySapphire
)