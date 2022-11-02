package jp.mydns.kokoichi0206.data.data.remote.responses

import com.google.gson.annotations.SerializedName

data class Move(
    val move: jp.mydns.kokoichi0206.data.data.remote.responses.MoveX,
    @SerializedName("version_group_details")
    val versionGroupDetails: List<jp.mydns.kokoichi0206.data.data.remote.responses.VersionGroupDetail>
)