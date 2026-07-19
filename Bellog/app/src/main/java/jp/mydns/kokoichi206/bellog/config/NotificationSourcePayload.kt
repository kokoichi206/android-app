package jp.mydns.kokoichi206.bellog.config

import org.json.JSONObject

data class NotificationSourcePayload(
    val packageName: String,
    val shortcutId: String?,
    val notificationKey: String,
    val title: String,
    val conversationTitle: String,
    val subText: String?,
    val sender: String,
    val message: String,
    val timestamp: Long,
    val mediaUri: String?,
    val extras: Map<String, String>,
    val messageExtras: Map<String, String>,
) {
    fun field(name: String): String? = when (name) {
        "packageName" -> packageName
        "shortcutId" -> shortcutId
        "notificationKey" -> notificationKey
        "title" -> title
        "conversationTitle" -> conversationTitle
        "subText" -> subText
        "sender" -> sender
        "message" -> message
        "timestamp" -> timestamp.toString()
        "mediaUri" -> mediaUri
        else -> error("Unknown fingerprint field: $name")
    }

    fun toJson(): String = JSONObject()
        .put("schemaVersion", 1)
        .put("packageName", packageName)
        .put("shortcutId", shortcutId)
        .put("notificationKey", notificationKey)
        .put("title", title)
        .put("conversationTitle", conversationTitle)
        .put("subText", subText)
        .put("sender", sender)
        .put("message", message)
        .put("timestamp", timestamp)
        .put("mediaUri", mediaUri)
        .put("extras", JSONObject(extras))
        .put("messageExtras", JSONObject(messageExtras))
        .toString()

    companion object {
        fun fromJson(json: String): NotificationSourcePayload {
            val root = JSONObject(json)
            return NotificationSourcePayload(
                packageName = root.getString("packageName"),
                shortcutId = root.optionalString("shortcutId"),
                notificationKey = root.getString("notificationKey"),
                title = root.getString("title"),
                conversationTitle = root.getString("conversationTitle"),
                subText = root.optionalString("subText"),
                sender = root.getString("sender"),
                message = root.getString("message"),
                timestamp = root.getLong("timestamp"),
                mediaUri = root.optionalString("mediaUri"),
                extras = root.getJSONObject("extras").stringMap(),
                messageExtras = root.getJSONObject("messageExtras").stringMap(),
            )
        }

        private fun JSONObject.optionalString(key: String): String? =
            if (isNull(key)) null else getString(key)

        private fun JSONObject.stringMap(): Map<String, String> =
            keys().asSequence().associateWith(::getString)
    }
}
