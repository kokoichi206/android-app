package jp.mydns.kokoichi206.quietlauncher.data

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import jp.mydns.kokoichi206.quietlauncher.model.AppEntry

class AppRepository(private val context: Context) {

    fun loadApps(): List<AppEntry> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(intent, 0)
            .asSequence()
            .map { it.activityInfo.packageName to it.loadLabel(pm).toString() }
            .filter { (pkg, _) -> pkg != context.packageName }
            .distinctBy { (pkg, _) -> pkg }
            .map { (pkg, label) -> AppEntry(pkg, label) }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    fun launch(packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    fun icon(packageName: String): Drawable? = try {
        context.packageManager.getApplicationIcon(packageName)
    } catch (e: Exception) {
        null
    }
}
