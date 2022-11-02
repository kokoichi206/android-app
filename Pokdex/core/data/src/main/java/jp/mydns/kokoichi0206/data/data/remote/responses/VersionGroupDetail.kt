package jp.mydns.kokoichi0206.data.data.remote.responses

import com.google.gson.annotations.SerializedName

data class VersionGroupDetail(
    @SerializedName("level_learned_at")
    val levelLearnedAt: Int,
    @SerializedName("move_learn_method")
    val moveLearnMethod: jp.mydns.kokoichi0206.data.data.remote.responses.MoveLearnMethod,
    @SerializedName("version_group")
    val versionGroup: jp.mydns.kokoichi0206.data.data.remote.responses.VersionGroup
)