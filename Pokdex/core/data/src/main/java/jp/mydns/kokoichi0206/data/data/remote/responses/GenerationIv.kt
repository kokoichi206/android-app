package jp.mydns.kokoichi0206.data.data.remote.responses


import com.google.gson.annotations.SerializedName

data class GenerationIv(
    @SerializedName("diamond-pearl")
    val diamondPearl: jp.mydns.kokoichi0206.data.data.remote.responses.DiamondPearl,
    @SerializedName("heartgold-soulsilver")
    val heartgoldSoulsilver: jp.mydns.kokoichi0206.data.data.remote.responses.HeartgoldSoulsilver,
    val platinum: jp.mydns.kokoichi0206.data.data.remote.responses.Platinum
)