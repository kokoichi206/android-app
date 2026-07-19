package jp.mydns.kokoichi206.bellog.config

import android.content.Context
import jp.mydns.kokoichi206.bellog.data.NotificationStore

class GroupingConfigRepository(private val context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun activeConfig(): GroupingConfig = GroupingConfig.parse(bundledJson())

    fun initialize(store: NotificationStore): Boolean {
        val json = bundledJson()
        val config = GroupingConfig.parse(json)
        val previous = preferences.getString(KEY_CONFIG_JSON, null)?.let(GroupingConfig::parse)
        if (previous != null && config.version != previous.version) config.validateSuccessor(previous)
        if (previous != null && config.version == previous.version) {
            require(config == previous) { "Grouping config changed without a version update" }
        }
        return activate(json, config, store)
    }

    private fun activate(json: String, config: GroupingConfig, store: NotificationStore): Boolean {
        val previousVersion = preferences.getString(KEY_ACTIVE_VERSION, null)
            ?.let(SemanticVersion::parse)
            ?: SemanticVersion(0, 0, 0)
        if (config.version == previousVersion) {
            if (!store.hasUnclassifiedRecords()) return false
            store.reclassify(config)
            return true
        }

        val reclassified = config.version.requiresReclassificationFrom(previousVersion)
        if (reclassified) store.reclassify(config)
        preferences.edit()
            .putString(KEY_CONFIG_JSON, json)
            .putString(KEY_ACTIVE_VERSION, config.version.toString())
            .apply()
        return reclassified
    }

    private fun bundledJson(): String = context.assets.open(CONFIG_ASSET).bufferedReader().use { it.readText() }

    companion object {
        private const val PREFERENCES_NAME = "grouping_config"
        private const val KEY_CONFIG_JSON = "config_json"
        private const val KEY_ACTIVE_VERSION = "active_version"
        private const val CONFIG_ASSET = "grouping-config.json"
    }
}
