package jp.mydns.kokoichi206.bellog.data

data class NotificationTask(
    val id: Long = 0,
    val sourceConversationKey: String,
    val packageName: String,
    val appLabel: String,
    val title: String,
    val body: String,
    val notificationReceivedAt: Long,
    val createdAt: Long,
)
