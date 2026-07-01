package jp.mydns.kokoichi206.quietlauncher.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.mydns.kokoichi206.quietlauncher.LauncherState
import jp.mydns.kokoichi206.quietlauncher.data.AppRepository
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerSheet(
    state: LauncherState,
    repo: AppRepository,
    sheetState: SheetState,
    onClose: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    // シート展開中に IME を出すとアニメーションが競合してカクつくため、展開完了を待つ
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.currentValue }.first { it == SheetValue.Expanded }
        focusRequester.requestFocus()
    }

    val filtered = remember(query, state.apps) {
        if (query.isBlank()) {
            state.apps
        } else {
            val q = query.lowercase()
            // 前方一致を優先し、続けて部分一致 (spec 5.2)
            val prefix = state.apps.filter { it.label.lowercase().startsWith(q) }
            val partial = state.apps.filter { it.label.lowercase().contains(q) } - prefix.toSet()
            prefix + partial
        }
    }

    Column(
        Modifier
            .fillMaxHeight(0.88f)
            .padding(horizontal = 20.dp)
            .imePadding(),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("アプリを検索") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        )
        LazyColumn(Modifier.weight(1f).padding(top = 10.dp)) {
            items(filtered, key = { it.packageName }) { app ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            state.onAppClick(app)
                            onClose()
                        }
                        .padding(vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppIconImage(app.packageName, repo, 36.dp)
                    Spacer(Modifier.width(14.dp))
                    Text(app.label, fontSize = 15.sp, color = ColorText)
                }
            }
        }
    }
}
