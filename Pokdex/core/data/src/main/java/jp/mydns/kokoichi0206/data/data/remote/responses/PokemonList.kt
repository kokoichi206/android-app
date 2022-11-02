package jp.mydns.kokoichi0206.data.data.remote.responses

import com.google.gson.annotations.SerializedName

data class PokemonList(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<jp.mydns.kokoichi0206.data.data.remote.responses.Result>
)
