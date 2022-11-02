package jp.mydns.kokoichi0206.data.data.remote.responses

import com.google.gson.annotations.SerializedName

data class Versions(
    @SerializedName("generation-i")
    val generationI: jp.mydns.kokoichi0206.data.data.remote.responses.GenerationI,
    @SerializedName("generation-ii")
    val generationIi: jp.mydns.kokoichi0206.data.data.remote.responses.GenerationIi,
    @SerializedName("generation-iii")
    val generationIii: jp.mydns.kokoichi0206.data.data.remote.responses.GenerationIii,
    @SerializedName("generation-iv")
    val generationIv: jp.mydns.kokoichi0206.data.data.remote.responses.GenerationIv,
    @SerializedName("generation-v")
    val generationV: jp.mydns.kokoichi0206.data.data.remote.responses.GenerationV,
    @SerializedName("generation-vi")
    val generationVi: jp.mydns.kokoichi0206.data.data.remote.responses.GenerationVi,
    @SerializedName("generation-vii")
    val generationVii: jp.mydns.kokoichi0206.data.data.remote.responses.GenerationVii,
    @SerializedName("generation-viii")
    val generationViii: jp.mydns.kokoichi0206.data.data.remote.responses.GenerationViii
)