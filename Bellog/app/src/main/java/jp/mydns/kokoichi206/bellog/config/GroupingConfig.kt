package jp.mydns.kokoichi206.bellog.config

import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

data class GroupingConfig(
    val version: SemanticVersion,
    val defaults: GroupingRule,
    val packages: Map<String, GroupingRule>,
) {
    fun ruleFor(packageName: String): GroupingRule {
        val packageRule = packages[packageName] ?: return defaults
        return GroupingRule(
            conversationKeyCandidates = packageRule.conversationKeyCandidates.ifEmpty {
                defaults.conversationKeyCandidates
            },
            messageKeyCandidates = packageRule.messageKeyCandidates.ifEmpty {
                defaults.messageKeyCandidates
            },
        )
    }

    fun validateSuccessor(previous: GroupingConfig) {
        require(version > previous.version) { "Config version must increase" }
        if (!version.requiresReclassificationFrom(previous.version)) {
            require(defaults == previous.defaults && packages == previous.packages) {
                "Grouping rules cannot change in a patch update"
            }
        }
    }

    companion object {
        fun parse(json: String): GroupingConfig {
            val root = JSONObject(json)
            val defaults = parseRule(root.getJSONObject("defaults"))
            require(defaults.conversationKeyCandidates.isNotEmpty())
            require(defaults.messageKeyCandidates.isNotEmpty())
            val packagesJson = root.getJSONObject("packages")
            val packages = packagesJson.keys().asSequence().associateWith { packageName ->
                parseRule(packagesJson.getJSONObject(packageName))
            }
            return GroupingConfig(
                version = SemanticVersion.parse(root.getString("version")),
                defaults = defaults,
                packages = packages,
            )
        }

        private fun parseRule(json: JSONObject): GroupingRule = GroupingRule(
            conversationKeyCandidates = parseCandidates(json.optJSONArray("conversationKeyCandidates")),
            messageKeyCandidates = parseCandidates(json.optJSONArray("messageKeyCandidates")),
        )

        private fun parseCandidates(json: JSONArray?): List<KeyCandidate> = buildList {
            if (json == null) return@buildList
            repeat(json.length()) { index ->
                val candidate = json.getJSONObject(index)
                val source = KeySource.entries.firstOrNull { it.jsonName == candidate.getString("source") }
                    ?: error("Unknown key source: ${candidate.getString("source")}")
                val key = candidate.optString("key").ifBlank { null }
                val fields = candidate.optJSONArray("fields")?.let { fieldArray ->
                    List(fieldArray.length()) { fieldArray.getString(it) }
                }.orEmpty()
                require(source !in setOf(KeySource.EXTRA, KeySource.MESSAGE_EXTRA) || key != null)
                require(source != KeySource.FINGERPRINT || fields.isNotEmpty())
                add(KeyCandidate(source, key, fields))
            }
        }
    }
}

data class GroupingRule(
    val conversationKeyCandidates: List<KeyCandidate>,
    val messageKeyCandidates: List<KeyCandidate>,
)

data class KeyCandidate(
    val source: KeySource,
    val key: String? = null,
    val fields: List<String> = emptyList(),
)

enum class KeySource(val jsonName: String) {
    SHORTCUT_ID("shortcutId"),
    CONVERSATION_TITLE("conversationTitle"),
    TITLE("title"),
    EXTRA("extra"),
    MESSAGE_EXTRA("messageExtra"),
    FINGERPRINT("fingerprint"),
}

data class Classification(
    val conversationKey: String,
    val messageKey: String,
)

object GroupingEngine {
    fun classify(payload: NotificationSourcePayload, config: GroupingConfig): Classification {
        val rule = config.ruleFor(payload.packageName)
        val conversationValue = firstValue(rule.conversationKeyCandidates, payload)
        val messageValue = firstValue(rule.messageKeyCandidates, payload)
        return Classification(
            conversationKey = "${payload.packageName}:$conversationValue",
            messageKey = "${payload.packageName}:$messageValue",
        )
    }

    private fun firstValue(candidates: List<KeyCandidate>, payload: NotificationSourcePayload): String =
        candidates.firstNotNullOf { candidate -> resolve(candidate, payload)?.takeIf(String::isNotBlank) }

    private fun resolve(candidate: KeyCandidate, payload: NotificationSourcePayload): String? = when (candidate.source) {
        KeySource.SHORTCUT_ID -> payload.shortcutId
        KeySource.CONVERSATION_TITLE -> payload.conversationTitle
        KeySource.TITLE -> payload.title
        KeySource.EXTRA -> payload.extras[candidate.key]
        KeySource.MESSAGE_EXTRA -> payload.messageExtras[candidate.key]
        KeySource.FINGERPRINT -> fingerprint(candidate.fields.map(payload::field))
    }

    private fun fingerprint(parts: List<String?>): String {
        val bytes = parts.joinToString(separator = "\u001f") { it.orEmpty() }.toByteArray()
        return MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString(separator = "") { "%02x".format(it) }
    }
}
