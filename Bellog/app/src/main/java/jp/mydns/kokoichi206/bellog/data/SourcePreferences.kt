package jp.mydns.kokoichi206.bellog.data

import android.content.Context

class SourcePreferences(context: Context) {
    private val preferences = context.getSharedPreferences("notification_sources", Context.MODE_PRIVATE)

    fun selectedPackages(): Set<String> = preferences.getStringSet(KEY_SELECTED, emptySet()).orEmpty()

    fun isSelected(packageName: String): Boolean = packageName in selectedPackages()

    fun setSelected(packageName: String, selected: Boolean) {
        val packages = selectedPackages().toMutableSet()
        if (selected) packages += packageName else packages -= packageName
        preferences.edit().putStringSet(KEY_SELECTED, packages).apply()
    }

    private companion object {
        const val KEY_SELECTED = "selected_packages"
    }
}
