package jp.mydns.kokoichi0206.data.data.remote.responses

import com.google.gson.annotations.SerializedName

data class Ability(
    val ability: jp.mydns.kokoichi0206.data.data.remote.responses.AbilityX,
    @SerializedName("is_hidden")
    val isHidden: Boolean,
    val slot: Int
)