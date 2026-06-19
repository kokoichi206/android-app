package jp.mydns.kokoichi206.oshilauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.lifecycle.lifecycleScope
import jp.mydns.kokoichi206.oshilauncher.data.AppRepository
import jp.mydns.kokoichi206.oshilauncher.data.CustomizationStore
import jp.mydns.kokoichi206.oshilauncher.ui.IconCache
import jp.mydns.kokoichi206.oshilauncher.ui.OshiLauncherApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repo: AppRepository
    private lateinit var store: CustomizationStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repo = AppRepository(applicationContext)
        store = CustomizationStore(applicationContext)
        // ドロワー初回展開時の同期ロードを避けるため、起動時に全アイコンを先読みしておく
        lifecycleScope.launch(Dispatchers.IO) {
            repo.loadApps().forEach { IconCache.load(it.packageName, repo) }
        }
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                OshiLauncherApp(repo, store)
            }
        }
    }
}
