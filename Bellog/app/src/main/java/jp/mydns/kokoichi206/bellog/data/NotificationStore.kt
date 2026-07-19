package jp.mydns.kokoichi206.bellog.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import jp.mydns.kokoichi206.bellog.config.GroupingConfig
import jp.mydns.kokoichi206.bellog.config.GroupingEngine
import jp.mydns.kokoichi206.bellog.config.NotificationSourcePayload
import org.json.JSONObject

class NotificationStore(context: Context) : SQLiteOpenHelper(context, "bellog.db", null, 6) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                event_key TEXT NOT NULL UNIQUE,
                package_name TEXT NOT NULL,
                app_label TEXT NOT NULL,
                conversation_key TEXT NOT NULL,
                conversation_title TEXT NOT NULL,
                message_id TEXT NOT NULL,
                sender TEXT NOT NULL,
                message TEXT NOT NULL,
                summary TEXT,
                content_type TEXT NOT NULL,
                received_at INTEGER NOT NULL,
                media_uri TEXT,
                notification_key TEXT NOT NULL,
                raw_payload TEXT NOT NULL,
                source_payload TEXT NOT NULL,
                rule_version TEXT NOT NULL,
                is_read INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX notifications_received_at ON notifications(received_at DESC)")
        db.execSQL("CREATE INDEX notifications_conversation ON notifications(conversation_key, received_at)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1) {
            db.execSQL("ALTER TABLE notifications ADD COLUMN conversation_title TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE notifications ADD COLUMN message_id TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE notifications ADD COLUMN sender TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE notifications ADD COLUMN message TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE notifications ADD COLUMN summary TEXT")
            db.execSQL("ALTER TABLE notifications ADD COLUMN content_type TEXT NOT NULL DEFAULT 'TEXT'")
            db.execSQL("ALTER TABLE notifications ADD COLUMN notification_key TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE notifications ADD COLUMN raw_payload TEXT NOT NULL DEFAULT '{}'")
            db.execSQL(
                """
                UPDATE notifications SET
                    conversation_title = title,
                    message_id = event_key,
                    sender = '',
                    message = text,
                    content_type = CASE
                        WHEN text LIKE '%写真を送信しました' THEN 'IMAGE'
                        WHEN text LIKE '%スタンプを送信しました' THEN 'STICKER'
                        WHEN text LIKE '%動画を送信しました' THEN 'VIDEO'
                        ELSE 'TEXT'
                    END,
                    raw_payload = '{}'
                """.trimIndent(),
            )
            // v1ではLINEの通常通知と要約通知を別イベントとして保存していた。
            db.execSQL(
                """
                DELETE FROM notifications AS duplicate
                WHERE EXISTS (
                    SELECT 1 FROM notifications AS original
                    WHERE original.id < duplicate.id
                      AND original.package_name = duplicate.package_name
                      AND original.conversation_key = duplicate.conversation_key
                      AND original.title = duplicate.title
                      AND original.text = duplicate.text
                      AND ABS(original.received_at - duplicate.received_at) <= 1000
                )
                """.trimIndent(),
            )
        }
        if (oldVersion <= 2) {
            db.execSQL(
                """
                CREATE TABLE notifications_v3 (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    event_key TEXT NOT NULL UNIQUE,
                    package_name TEXT NOT NULL,
                    app_label TEXT NOT NULL,
                    conversation_key TEXT NOT NULL,
                    conversation_title TEXT NOT NULL,
                    message_id TEXT NOT NULL,
                    sender TEXT NOT NULL,
                    message TEXT NOT NULL,
                    summary TEXT,
                    content_type TEXT NOT NULL,
                    received_at INTEGER NOT NULL,
                    media_uri TEXT,
                    notification_key TEXT NOT NULL,
                    raw_payload TEXT NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                INSERT INTO notifications_v3
                SELECT id, event_key, package_name, app_label, conversation_key,
                    conversation_title, message_id, sender, message, summary, content_type,
                    received_at, media_uri, notification_key, raw_payload
                FROM notifications
                """.trimIndent(),
            )
            db.execSQL("DROP TABLE notifications")
            db.execSQL("ALTER TABLE notifications_v3 RENAME TO notifications")
            db.execSQL("CREATE INDEX notifications_received_at ON notifications(received_at DESC)")
            db.execSQL("CREATE INDEX notifications_conversation ON notifications(conversation_key, received_at)")
        }
        if (oldVersion <= 3) {
            db.execSQL("ALTER TABLE notifications ADD COLUMN is_read INTEGER NOT NULL DEFAULT 0")
        }
        if (oldVersion <= 4) {
            db.execSQL("ALTER TABLE notifications ADD COLUMN source_payload TEXT NOT NULL DEFAULT '{}'")
            db.execSQL("ALTER TABLE notifications ADD COLUMN rule_version TEXT NOT NULL DEFAULT '0.0.0'")
            migrateSourcePayloads(db)
        }
        if (oldVersion == 5) repairGeneratedMessageIds(db)
    }

    fun insert(record: NotificationRecord): Boolean = writableDatabase.insertWithOnConflict(
            "notifications",
            null,
            ContentValues().apply {
                put("event_key", record.eventKey)
                put("package_name", record.packageName)
                put("app_label", record.appLabel)
                put("conversation_key", record.conversationKey)
                put("conversation_title", record.conversationTitle)
                put("message_id", record.messageId)
                put("sender", record.sender)
                put("message", record.message)
                put("summary", record.summary)
                put("content_type", record.contentType.name)
                put("received_at", record.receivedAt)
                put("media_uri", record.mediaUri)
                put("notification_key", record.notificationKey)
                put("raw_payload", record.rawPayload)
                put("source_payload", record.sourcePayload)
                put("rule_version", record.ruleVersion)
                put("is_read", record.isRead)
            },
            SQLiteDatabase.CONFLICT_IGNORE,
        ) != -1L

    fun records(): List<NotificationRecord> = readableDatabase.query(
        "notifications",
        null,
        null,
        null,
        null,
        null,
        "received_at DESC",
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) {
                add(
                    NotificationRecord(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        eventKey = cursor.getString(cursor.getColumnIndexOrThrow("event_key")),
                        packageName = cursor.getString(cursor.getColumnIndexOrThrow("package_name")),
                        appLabel = cursor.getString(cursor.getColumnIndexOrThrow("app_label")),
                        conversationKey = cursor.getString(cursor.getColumnIndexOrThrow("conversation_key")),
                        conversationTitle = cursor.getString(cursor.getColumnIndexOrThrow("conversation_title")),
                        messageId = cursor.getString(cursor.getColumnIndexOrThrow("message_id")),
                        sender = cursor.getString(cursor.getColumnIndexOrThrow("sender")),
                        message = cursor.getString(cursor.getColumnIndexOrThrow("message")),
                        summary = cursor.getString(cursor.getColumnIndexOrThrow("summary")),
                        contentType = NotificationContentType.valueOf(
                            cursor.getString(cursor.getColumnIndexOrThrow("content_type")),
                        ),
                        receivedAt = cursor.getLong(cursor.getColumnIndexOrThrow("received_at")),
                        mediaUri = cursor.getString(cursor.getColumnIndexOrThrow("media_uri")),
                        notificationKey = cursor.getString(cursor.getColumnIndexOrThrow("notification_key")),
                        rawPayload = cursor.getString(cursor.getColumnIndexOrThrow("raw_payload")),
                        sourcePayload = cursor.getString(cursor.getColumnIndexOrThrow("source_payload")),
                        ruleVersion = cursor.getString(cursor.getColumnIndexOrThrow("rule_version")),
                        isRead = cursor.getInt(cursor.getColumnIndexOrThrow("is_read")) == 1,
                    ),
                )
            }
        }.distinctBy { "${it.packageName}:${it.messageId}" }
    }

    fun reclassify(config: GroupingConfig) {
        writableDatabase.runInTransaction {
            query(
                "notifications",
                arrayOf("id", "source_payload"),
                null,
                null,
                null,
                null,
                null,
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                    val payload = NotificationSourcePayload.fromJson(
                        cursor.getString(cursor.getColumnIndexOrThrow("source_payload")),
                    )
                    val classification = GroupingEngine.classify(payload, config)
                    update(
                        "notifications",
                        ContentValues().apply {
                            put("conversation_key", classification.conversationKey)
                            put("message_id", classification.messageKey)
                            put("rule_version", config.version.toString())
                        },
                        "id = ?",
                        arrayOf(id.toString()),
                    )
                }
            }
        }
    }

    fun hasUnclassifiedRecords(): Boolean = readableDatabase.rawQuery(
        "SELECT EXISTS(SELECT 1 FROM notifications WHERE rule_version = '0.0.0')",
        null,
    ).use { cursor ->
        cursor.moveToFirst()
        cursor.getInt(0) == 1
    }

    fun clear() {
        writableDatabase.delete("notifications", null, null)
    }

    fun markConversationRead(conversationKey: String) {
        writableDatabase.update(
            "notifications",
            ContentValues().apply { put("is_read", true) },
            "conversation_key = ?",
            arrayOf(conversationKey),
        )
    }

    private fun migrateSourcePayloads(db: SQLiteDatabase) {
        db.query(
            "notifications",
            null,
            null,
            null,
            null,
            null,
            null,
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val raw = JSONObject(cursor.getString(cursor.getColumnIndexOrThrow("raw_payload")))
                val packageName = cursor.getString(cursor.getColumnIndexOrThrow("package_name"))
                val notificationKey = cursor.getString(cursor.getColumnIndexOrThrow("notification_key"))
                val title = raw.stringOrNull("title")
                    ?: cursor.getString(cursor.getColumnIndexOrThrow("conversation_title"))
                val conversationTitle = cursor.getString(cursor.getColumnIndexOrThrow("conversation_title"))
                val sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"))
                val message = cursor.getString(cursor.getColumnIndexOrThrow("message"))
                val messageId = raw.stringOrNull("messageId")
                val conversationId = raw.stringOrNull("conversationId")
                val payload = NotificationSourcePayload(
                    packageName = packageName,
                    shortcutId = raw.stringOrNull("shortcutId"),
                    notificationKey = notificationKey,
                    title = title,
                    conversationTitle = conversationTitle,
                    subText = cursor.getString(cursor.getColumnIndexOrThrow("summary")),
                    sender = sender,
                    message = message,
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("received_at")),
                    mediaUri = cursor.getString(cursor.getColumnIndexOrThrow("media_uri")),
                    extras = buildMap {
                        if (conversationId != null) put("line.chat.id", conversationId)
                        if (messageId != null) put("line.message.id", messageId)
                    },
                    messageExtras = buildMap {
                        if (messageId != null && !messageId.startsWith("$notificationKey:")) {
                            put("MESSAGE_ID", messageId)
                        }
                    },
                )
                db.update(
                    "notifications",
                    ContentValues().apply { put("source_payload", payload.toJson()) },
                    "id = ?",
                    arrayOf(id.toString()),
                )
            }
        }
    }

    private fun repairGeneratedMessageIds(db: SQLiteDatabase) {
        db.query(
            "notifications",
            arrayOf("id", "source_payload"),
            null,
            null,
            null,
            null,
            null,
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val payload = NotificationSourcePayload.fromJson(
                    cursor.getString(cursor.getColumnIndexOrThrow("source_payload")),
                )
                val messageId = payload.messageExtras["MESSAGE_ID"]
                val repaired = if (messageId?.startsWith("${payload.notificationKey}:") == true) {
                    payload.copy(messageExtras = payload.messageExtras - "MESSAGE_ID")
                } else {
                    payload
                }
                db.update(
                    "notifications",
                    ContentValues().apply {
                        put("source_payload", repaired.toJson())
                        put("rule_version", "0.0.0")
                    },
                    "id = ?",
                    arrayOf(id.toString()),
                )
            }
        }
    }

    private fun JSONObject.stringOrNull(key: String): String? =
        if (!has(key) || isNull(key)) null else getString(key)

    private inline fun SQLiteDatabase.runInTransaction(block: SQLiteDatabase.() -> Unit) {
        beginTransaction()
        try {
            block()
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }
}
