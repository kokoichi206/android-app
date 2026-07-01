package jp.mydns.kokoichi206.quietlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.lifecycle.lifecycleScope
import jp.mydns.kokoichi206.quietlauncher.data.AppRepository
import jp.mydns.kokoichi206.quietlauncher.data.StatsStore
import jp.mydns.kokoichi206.quietlauncher.ui.IconCache
import jp.mydns.kokoichi206.quietlauncher.ui.LauncherApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repo: AppRepository
    private lateinit var state: LauncherState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repo = AppRepository(applicationContext)
        state = LauncherState(repo, StatsStore(applicationContext))
        // ドロワー初回展開時の同期ロードを避けるため、起動時に全アイコンを先読みしておく
        lifecycleScope.launch(Dispatchers.IO) {
            repo.loadApps().forEach { IconCache.load(it.packageName, repo) }
        }
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                LauncherApp(state, repo)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 再計算はホーム復帰時のみ (spec 3.5)
        state.refresh("画面復帰")
    }
}
