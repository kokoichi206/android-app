package jp.mydns.kokoichi0206.data.data.remote.responses

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val abilities: List<jp.mydns.kokoichi0206.data.data.remote.responses.Ability>,
    @SerializedName("base_experience")
    val baseExperience: Int,
    val forms: List<jp.mydns.kokoichi0206.data.data.remote.responses.Form>,
    @SerializedName("game_indices")
    val gameIndices: List<jp.mydns.kokoichi0206.data.data.remote.responses.GameIndice>,
    val height: Int,
    @SerializedName("held_items")
    val heldItems: List<Any>,
    val id: Int,
    @SerializedName("is_default")
    val isDefault: Boolean,
    @SerializedName("location_area_encounters")
    val locationAreaEncounters: String,
    val moves: List<jp.mydns.kokoichi0206.data.data.remote.responses.Move>,
    val name: String,
    val order: Int,
    @SerializedName("past_types")
    val pastTypes: List<Any>,
    val species: jp.mydns.kokoichi0206.data.data.remote.responses.Species,
    val sprites: jp.mydns.kokoichi0206.data.data.remote.responses.Sprites,
    val stats: List<jp.mydns.kokoichi0206.data.data.remote.responses.Stat>,
    val types: List<jp.mydns.kokoichi0206.data.data.remote.responses.Type>,
    val weight: Int
)