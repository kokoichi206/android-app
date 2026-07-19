package jp.mydns.kokoichi206.bellog.data

data class NotificationRecord(
    val id: Long = 0,
    val eventKey: String,
    val packageName: String,
    val appLabel: String,
    val conversationKey: String,
    val conversationTitle: String,
    val messageId: String,
    val sender: String,
    val message: String,
    val summary: String?,
    val contentType: NotificationContentType,
    val receivedAt: Long,
    val mediaUri: String? = null,
    val notificationKey: String,
    val rawPayload: String,
    val sourcePayload: String,
    val ruleVersion: String,
    val isRead: Boolean = false,
)

enum class NotificationContentType { TEXT, IMAGE, STICKER, VIDEO, FILE, OTHER }

data class NotificationGroup(
    val conversationKey: String,
    val packageName: String,
    val appLabel: String,
    val title: String,
    val records: List<NotificationRecord>,
) {
    val receivedAt: Long = records.maxOf { it.receivedAt }
    val isRead: Boolean = records.all { it.isRead }
}
