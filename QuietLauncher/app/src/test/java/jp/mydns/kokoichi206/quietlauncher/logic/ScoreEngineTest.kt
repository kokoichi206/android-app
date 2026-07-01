package jp.mydns.kokoichi206.quietlauncher.logic

import jp.mydns.kokoichi206.quietlauncher.model.AppEntry
import jp.mydns.kokoichi206.quietlauncher.model.AppStats
import jp.mydns.kokoichi206.quietlauncher.model.CalEvent
import jp.mydns.kokoichi206.quietlauncher.model.DayType
import jp.mydns.kokoichi206.quietlauncher.model.LauncherContext
import jp.mydns.kokoichi206.quietlauncher.model.TimeBucket
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoreEngineTest {

    private val morningWeekday = LauncherContext(TimeBucket.MORNING, DayType.WEEKDAY, null)

    private fun app(name: String) = AppEntry("pkg.$name", name)

    private fun stats(
        frecency: Map<String, Double> = emptyMap(),
        cells: Map<String, Double> = emptyMap(),
    ) = AppStats(frecency, cells)

    @Test
    fun コールドスタートは辞書順で配置しデータ不足を明示する() {
        val apps = listOf(app("delta"), app("alpha"), app("charlie"), app("bravo"), app("echo"))
        val result = ScoreEngine.recompute(apps, stats(), morningWeekday, emptyList())

        assertTrue(result.coldStart)
        assertEquals(listOf("pkg.alpha", "pkg.bravo", "pkg.charlie", "pkg.delta"), result.slots)
        assertTrue(result.messages.any { it.contains("データ不足") })
    }

    @Test
    fun 差がθを超えると入れ替わる() {
        val apps = listOf(app("a"), app("b"), app("c"), app("d"), app("e"))
        // 重み付け後: a=35.0, b=31.5, c=28.0, e=24.5, d=3.5 → e と d の差 21.0 > θ
        val frecency = mapOf(
            "pkg.a" to 100.0, "pkg.b" to 90.0, "pkg.c" to 80.0,
            "pkg.d" to 10.0, "pkg.e" to 70.0,
        )
        val result = ScoreEngine.recompute(
            apps, stats(frecency), morningWeekday,
            currentSlots = listOf("pkg.a", "pkg.b", "pkg.c", "pkg.d"),
        )

        assertEquals(listOf("pkg.a", "pkg.b", "pkg.c", "pkg.e"), result.slots)
        assertTrue("pkg.e" in result.swappedIn)
        assertTrue(result.messages.any { it.startsWith("入替") })
    }

    @Test
    fun 差がθ以下なら保留する() {
        val apps = listOf(app("a"), app("b"), app("c"), app("d"), app("e"))
        // 重み付け後: a=35.0, b=31.5, c=29.75, e=28.0, d=21.0 → e と d の差 7.0 ≤ θ
        val frecency = mapOf(
            "pkg.a" to 100.0, "pkg.b" to 90.0, "pkg.c" to 85.0,
            "pkg.d" to 60.0, "pkg.e" to 80.0,
        )
        val result = ScoreEngine.recompute(
            apps, stats(frecency), morningWeekday,
            currentSlots = listOf("pkg.a", "pkg.b", "pkg.c", "pkg.d"),
        )

        assertEquals(listOf("pkg.a", "pkg.b", "pkg.c", "pkg.d"), result.slots)
        assertTrue(result.swappedIn.isEmpty())
        assertTrue(result.messages.any { it.startsWith("保留") })
    }

    @Test
    fun 入れ替えは退場枠の位置を保つ() {
        val apps = listOf(app("a"), app("b"), app("c"), app("d"), app("e"))
        val frecency = mapOf(
            "pkg.a" to 100.0, "pkg.b" to 90.0, "pkg.c" to 80.0,
            "pkg.d" to 10.0, "pkg.e" to 70.0,
        )
        val result = ScoreEngine.recompute(
            apps, stats(frecency), morningWeekday,
            currentSlots = listOf("pkg.a", "pkg.d", "pkg.b", "pkg.c"),
        )

        assertEquals(listOf("pkg.a", "pkg.e", "pkg.b", "pkg.c"), result.slots)
    }

    @Test
    fun 時間帯セルがランキングを変える() {
        val apps = listOf(app("work"), app("fun"))
        val cells = mapOf(
            "pkg.work|MORNING_WEEKDAY" to 100.0,
            "pkg.fun|NIGHT_WEEKDAY" to 100.0,
        )

        val morning = ScoreEngine.recompute(
            apps, stats(cells = cells), morningWeekday, emptyList(), slotCount = 1,
        )
        assertEquals(listOf("pkg.work"), morning.slots)

        val nightCtx = LauncherContext(TimeBucket.NIGHT, DayType.WEEKDAY, null)
        val night = ScoreEngine.recompute(
            apps, stats(cells = cells), nightCtx, emptyList(), slotCount = 1,
        )
        assertEquals(listOf("pkg.fun"), night.slots)
    }

    @Test
    fun 提案枠はスロット外の予定マッチアプリを出しスロット内なら抑制する() {
        val event = CalEvent("golf", "⛳ ゴルフ 15:30", "golf")
        val ctx = LauncherContext(TimeBucket.DAY, DayType.WEEKDAY, event)
        val golfApp = AppEntry("com.example.golfscore", "Golf Score")
        val others = listOf(app("a"), app("b"), app("c"), app("d"))
        val frecency = mapOf(
            "pkg.a" to 100.0, "pkg.b" to 90.0, "pkg.c" to 80.0, "pkg.d" to 70.0,
        )

        val visible = ScoreEngine.recompute(
            others + golfApp, stats(frecency), ctx,
            currentSlots = listOf("pkg.a", "pkg.b", "pkg.c", "pkg.d"),
        )
        assertEquals("com.example.golfscore", visible.suggestion)

        val suppressed = ScoreEngine.recompute(
            others + golfApp, stats(frecency), ctx,
            currentSlots = listOf("pkg.a", "pkg.b", "pkg.c", "com.example.golfscore"),
        )
        assertNull(suppressed.suggestion)
        assertTrue(suppressed.messages.any { it.contains("全てスロット内") })
    }

    @Test
    fun 予定にマッチするアプリが無ければ提案枠は出ない() {
        val event = CalEvent("golf", "⛳ ゴルフ 15:30", "golf")
        val ctx = LauncherContext(TimeBucket.DAY, DayType.WEEKDAY, event)
        val result = ScoreEngine.recompute(listOf(app("a"), app("b")), stats(), ctx, emptyList())

        assertNull(result.suggestion)
        assertTrue(result.messages.any { it.contains("マッチするアプリなし") })
    }
}
