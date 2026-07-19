package jp.mydns.kokoichi206.bellog.notification

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import jp.mydns.kokoichi206.bellog.config.GroupingConfigRepository
import jp.mydns.kokoichi206.bellog.config.GroupingEngine
import jp.mydns.kokoichi206.bellog.config.NotificationSourcePayload
import jp.mydns.kokoichi206.bellog.data.NotificationRecord
import jp.mydns.kokoichi206.bellog.data.NotificationContentType
import jp.mydns.kokoichi206.bellog.data.NotificationStore
import jp.mydns.kokoichi206.bellog.data.SourcePreferences
import org.json.JSONObject
import java.security.MessageDigest

const val ACTION_NOTIFICATIONS_CHANGED = "jp.mydns.kokoichi206.bellog.NOTIFICATIONS_CHANGED"
private const val LOG_TAG = "BellogCollector"

class BellogNotificationListener : NotificationListenerService() {
    private val store by lazy { NotificationStore(applicationContext) }
    private val sources by lazy { SourcePreferences(applicationContext) }
    private val configRepository by lazy { GroupingConfigRepository(applicationContext) }

    override fun onListenerConnected() {
        activeNotifications.forEach(::onNotificationPosted)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (!sources.isSelected(sbn.packageName)) return
        if (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return

        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val summary = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
        val appLabel = packageManager.getApplicationLabel(
            packageManager.getApplicationInfo(sbn.packageName, 0),
        ).toString()
        val style = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(notification)
        val messages = style?.messages.orEmpty()
        val conversationTitle = style?.conversationTitle?.toString().orEmpty().ifBlank { title }
        val config = configRepository.activeConfig()
        val notificationExtras = extras.stringValues()

        if (messages.isNotEmpty()) {
            messages.forEach { message ->
                val text = message.text?.toString().orEmpty()
                if (text.isBlank()) return@forEach
                val messageId = message.extras.getString("MESSAGE_ID")
                    ?: extras.getString("line.message.id")
                    ?: "${sbn.key}:${message.timestamp}:${text.hashCode()}"
                val sender = message.person?.name?.toString().orEmpty().ifBlank { title }
                val mediaUri = message.dataUri?.toString()
                val payload = NotificationSourcePayload(
                    packageName = sbn.packageName,
                    shortcutId = notification.shortcutId,
                    notificationKey = sbn.key,
                    title = title,
                    conversationTitle = conversationTitle,
                    subText = summary,
                    sender = sender,
                    message = text,
                    timestamp = message.timestamp,
                    mediaUri = mediaUri,
                    extras = notificationExtras,
                    messageExtras = message.extras.stringValues(),
                )
                val classification = GroupingEngine.classify(payload, config)
                save(
                    NotificationRecord(
                        eventKey = captureKey(payload),
                        packageName = sbn.packageName,
                        appLabel = appLabel,
                        conversationKey = classification.conversationKey,
                        conversationTitle = conversationTitle.ifBlank { sender },
                        messageId = classification.messageKey,
                        sender = sender,
                        message = text,
                        summary = summary,
                        contentType = contentType(text, message.dataMimeType),
                        receivedAt = message.timestamp,
                        mediaUri = mediaUri,
                        notificationKey = sbn.key,
                        rawPayload = rawPayload(sbn, title, text, summary, messageId, sender, message.dataMimeType, mediaUri),
                        sourcePayload = payload.toJson(),
                        ruleVersion = config.version.toString(),
                    ),
                )
            }
        } else {
            val text = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
                ?: extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
                ?: return
            val messageId = extras.getString("line.message.id")
            val payload = NotificationSourcePayload(
                packageName = sbn.packageName,
                shortcutId = notification.shortcutId,
                notificationKey = sbn.key,
                title = title,
                conversationTitle = conversationTitle,
                subText = summary,
                sender = "",
                message = text,
                timestamp = sbn.postTime,
                mediaUri = null,
                extras = notificationExtras,
                messageExtras = emptyMap(),
            )
            val classification = GroupingEngine.classify(payload, config)
            save(
                NotificationRecord(
                    eventKey = captureKey(payload),
                    packageName = sbn.packageName,
                    appLabel = appLabel,
                    conversationKey = classification.conversationKey,
                    conversationTitle = conversationTitle,
                    messageId = classification.messageKey,
                    sender = "",
                    message = text,
                    summary = summary,
                    contentType = contentType(text, null),
                    receivedAt = sbn.postTime,
                    notificationKey = sbn.key,
                    rawPayload = rawPayload(sbn, title, text, summary, messageId, "", null, null),
                    sourcePayload = payload.toJson(),
                    ruleVersion = config.version.toString(),
                ),
            )
        }

        sendBroadcast(Intent(ACTION_NOTIFICATIONS_CHANGED).setPackage(packageName))
    }

    private fun save(record: NotificationRecord) {
        val inserted = store.insert(record)
        Log.d(
            LOG_TAG,
            "inserted=$inserted app=${record.packageName} conversation=${record.conversationKey} " +
                "event=${record.eventKey}",
        )
    }

    private fun contentType(text: String, mimeType: String?): NotificationContentType = when {
        mimeType?.startsWith("image/") == true -> NotificationContentType.IMAGE
        mimeType?.startsWith("video/") == true -> NotificationContentType.VIDEO
        "写真を送信しました" in text -> NotificationContentType.IMAGE
        "スタンプを送信しました" in text -> NotificationContentType.STICKER
        "動画を送信しました" in text -> NotificationContentType.VIDEO
        "ファイルを送信しました" in text -> NotificationContentType.FILE
        else -> NotificationContentType.TEXT
    }

    private fun captureKey(payload: NotificationSourcePayload): String {
        val source = listOf(
            payload.packageName,
            payload.notificationKey,
            payload.timestamp.toString(),
            payload.sender,
            payload.message,
            payload.mediaUri.orEmpty(),
        ).joinToString("\u001f")
        val digest = MessageDigest.getInstance("SHA-256").digest(source.toByteArray())
        return digest.joinToString(separator = "") { "%02x".format(it) }
    }

    private fun Bundle.stringValues(): Map<String, String> = buildMap {
        keySet().forEach { key ->
            val value: Any? = get(key)
            when (value) {
                is CharSequence -> put(key, value.toString())
                is Number, is Boolean -> put(key, value.toString())
            }
        }
    }

    private fun rawPayload(
        sbn: StatusBarNotification,
        title: String,
        text: String,
        summary: String?,
        messageId: String?,
        sender: String,
        mimeType: String?,
        mediaUri: String?,
    ): String = JSONObject()
        .put("notificationKey", sbn.key)
        .put("packageName", sbn.packageName)
        .put("title", title)
        .put("text", text)
        .put("summary", summary)
        .put("shortcutId", sbn.notification.shortcutId)
        .put("conversationId", sbn.notification.extras.getString("line.chat.id"))
        .put("messageId", messageId)
        .put("sender", sender)
        .put("timestamp", sbn.postTime)
        .put("mimeType", mimeType)
        .put("mediaUri", mediaUri)
        .toString()
}
