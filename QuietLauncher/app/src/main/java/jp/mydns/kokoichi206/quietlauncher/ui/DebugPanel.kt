package jp.mydns.kokoichi206.quietlauncher.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.mydns.kokoichi206.quietlauncher.LauncherState
import jp.mydns.kokoichi206.quietlauncher.logic.CalendarDictionary
import jp.mydns.kokoichi206.quietlauncher.model.DayType
import jp.mydns.kokoichi206.quietlauncher.model.TimeBucket
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DebugPanel(state: LauncherState) {
    Column(
        Modifier
            .fillMaxHeight(0.92f)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
    ) {
        Text("デバッグパネル（PoC 専用）", fontSize = 16.sp, color = ColorText)

        SectionLabel("時間帯バケット")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.bucketOverride == null,
                onClick = { state.setBucket(null) },
                label = { Text("自動") },
            )
            TimeBucket.entries.forEach { bucket ->
                FilterChip(
                    selected = state.bucketOverride == bucket,
                    onClick = { state.setBucket(bucket) },
                    label = { Text(bucket.label) },
                )
            }
        }

        SectionLabel("曜日タイプ")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.dayOverride == null,
                onClick = { state.setDayType(null) },
                label = { Text("自動") },
            )
            DayType.entries.forEach { dayType ->
                FilterChip(
                    selected = state.dayOverride == dayType,
                    onClick = { state.setDayType(dayType) },
                    label = { Text(dayType.label) },
                )
            }
        }

        SectionLabel("ダミー予定")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalendarDictionary.events.forEach { event ->
                FilterChip(
                    selected = state.event.id == event.id,
                    onClick = { state.selectEvent(event) },
                    label = { Text(event.label) },
                )
            }
        }

        SectionLabel("操作")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { state.seedDemo() }) { Text("デモデータ投入") }
            OutlinedButton(onClick = { state.resetStats() }) { Text("統計リセット") }
            OutlinedButton(onClick = { state.recompute("手動") }) { Text("再計算") }
        }

        SectionLabel("再計算ログ")
        state.messages.forEach { message ->
            val color = when {
                message.startsWith("入替") -> ColorGreen
                message.startsWith("保留") -> ColorAmber
                message.startsWith("提案枠") -> ColorAccent
                else -> ColorSub
            }
            Text(message, fontSize = 12.sp, color = color, modifier = Modifier.padding(vertical = 2.dp))
        }

        SectionLabel("スコア内訳（上位 12）")
        state.scores.take(12).forEach { s ->
            Text(
                String.format(
                    Locale.US,
                    "%-14s f=%5.1f t=%5.1f c=%5.1f → %5.1f",
                    s.label.take(14),
                    s.freq,
                    s.time,
                    s.cal,
                    s.total,
                ),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = ColorSub,
            )
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        fontSize = 11.sp,
        color = ColorAccent,
        modifier = Modifier.padding(top = 18.dp, bottom = 6.dp),
    )
}
