package jp.mydns.kokoichi206.bellog.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GroupingEngineTest {
    private val defaults = GroupingRule(
        conversationKeyCandidates = listOf(
            KeyCandidate(KeySource.SHORTCUT_ID),
            KeyCandidate(KeySource.TITLE),
        ),
        messageKeyCandidates = listOf(
            KeyCandidate(KeySource.MESSAGE_EXTRA, "MESSAGE_ID"),
            KeyCandidate(KeySource.FINGERPRINT, fields = listOf("notificationKey", "timestamp", "message")),
        ),
    )

    @Test
    fun firstAvailableCandidateIsUsedWithoutJoiningValues() {
        val classification = GroupingEngine.classify(
            payload(shortcutId = "channel-123", title = "Channel title"),
            GroupingConfig(SemanticVersion(1, 0, 0), defaults, emptyMap()),
        )

        assertEquals("com.example:channel-123", classification.conversationKey)
    }

    @Test
    fun packageRuleCanUseAnAppSpecificExtra() {
        val lineRule = defaults.copy(
            conversationKeyCandidates = listOf(
                KeyCandidate(KeySource.EXTRA, "line.chat.id"),
                KeyCandidate(KeySource.SHORTCUT_ID),
            ),
        )
        val classification = GroupingEngine.classify(
            payload(
                packageName = "jp.naver.line.android",
                shortcutId = "shortcut",
                extras = mapOf("line.chat.id" to "chat-456"),
            ),
            GroupingConfig(
                SemanticVersion(1, 0, 0),
                defaults,
                mapOf("jp.naver.line.android" to lineRule),
            ),
        )

        assertEquals("jp.naver.line.android:chat-456", classification.conversationKey)
    }

    @Test
    fun messageKeyIdentifiesAMessageIndependentlyFromItsConversation() {
        val classification = GroupingEngine.classify(
            payload(
                shortcutId = "channel-123",
                messageExtras = mapOf("MESSAGE_ID" to "message-789"),
            ),
            GroupingConfig(SemanticVersion(1, 0, 0), defaults, emptyMap()),
        )

        assertEquals("com.example:channel-123", classification.conversationKey)
        assertEquals("com.example:message-789", classification.messageKey)
    }

    @Test
    fun patchUpdateCannotChangeGroupingRules() {
        val previous = GroupingConfig(SemanticVersion(1, 2, 3), defaults, emptyMap())
        val changedDefaults = defaults.copy(
            conversationKeyCandidates = listOf(KeyCandidate(KeySource.TITLE)),
        )
        val patch = GroupingConfig(SemanticVersion(1, 2, 4), changedDefaults, emptyMap())

        assertThrows(IllegalArgumentException::class.java) {
            patch.validateSuccessor(previous)
        }
    }

    @Test
    fun minorUpdateCanChangeGroupingRules() {
        val previous = GroupingConfig(SemanticVersion(1, 2, 3), defaults, emptyMap())
        val changedDefaults = defaults.copy(
            conversationKeyCandidates = listOf(KeyCandidate(KeySource.TITLE)),
        )
        val minor = GroupingConfig(SemanticVersion(1, 3, 0), changedDefaults, emptyMap())

        minor.validateSuccessor(previous)
    }

    private fun payload(
        packageName: String = "com.example",
        shortcutId: String? = null,
        title: String = "Title",
        extras: Map<String, String> = emptyMap(),
        messageExtras: Map<String, String> = emptyMap(),
    ) = NotificationSourcePayload(
        packageName = packageName,
        shortcutId = shortcutId,
        notificationKey = "notification-key",
        title = title,
        conversationTitle = title,
        subText = null,
        sender = "Sender",
        message = "Message",
        timestamp = 1234,
        mediaUri = null,
        extras = extras,
        messageExtras = messageExtras,
    )
}
