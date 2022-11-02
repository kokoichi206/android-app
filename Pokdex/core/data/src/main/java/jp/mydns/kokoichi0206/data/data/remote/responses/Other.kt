package jp.mydns.kokoichi0206.data.data.remote.responses

import com.google.gson.annotations.SerializedName

data class Other(
    @SerializedName("dream_world")
    val dreamWorld: jp.mydns.kokoichi0206.data.data.remote.responses.DreamWorld,
    @SerializedName("official-artwork")
    val officialArtwork: jp.mydns.kokoichi0206.data.data.remote.responses.OfficialArtwork
)