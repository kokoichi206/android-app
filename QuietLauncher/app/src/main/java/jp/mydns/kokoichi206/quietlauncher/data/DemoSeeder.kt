package jp.mydns.kokoichi206.quietlauncher.data

import jp.mydns.kokoichi206.quietlauncher.model.DayType
import jp.mydns.kokoichi206.quietlauncher.model.TimeBucket

/**
 * 受け入れシナリオ（spec 8）検証用のデモデータ。
 * 値は concept-a-logic.html のシミュレータと同じ設計:
 * 朝→昼で Clock→Messages の入替、昼→夜で YouTube 入替 + Photos が θ 保留になる。
 */
object DemoSeeder {

    // package -> (frecency, 朝, 昼, 夜)  ※ 平日セルへ投入
    private val seeds: Map<String, List<Double>> = mapOf(
        "com.android.chrome" to listOf(70.0, 45.0, 80.0, 55.0),
        "com.google.android.gm" to listOf(55.0, 60.0, 70.0, 20.0),
        "com.google.android.apps.maps" to listOf(65.0, 95.0, 35.0, 15.0),
        "com.google.android.deskclock" to listOf(45.0, 90.0, 5.0, 30.0),
        "com.google.android.youtube" to listOf(65.0, 15.0, 25.0, 85.0),
        "com.google.android.apps.photos" to listOf(40.0, 10.0, 45.0, 50.0),
        "com.google.android.apps.messaging" to listOf(55.0, 55.0, 70.0, 30.0),
        "com.android.settings" to listOf(35.0, 5.0, 30.0, 10.0),
        "com.google.android.calendar" to listOf(30.0, 40.0, 35.0, 5.0),
        "com.android.vending" to listOf(50.0, 10.0, 40.0, 30.0),
    )

    /** @return 投入できたパッケージ名（インストールされていないものはスキップ） */
    fun seed(store: StatsStore, installedPackages: Set<String>): List<String> {
        val applied = mutableListOf<String>()
        seeds.forEach { (pkg, values) ->
            if (pkg !in installedPackages) return@forEach
            val (frecency, morning, day, night) = values
            store.put(
                key = pkg,
                frecency = frecency,
                cells = mapOf(
                    "${TimeBucket.MORNING.name}_${DayType.WEEKDAY.name}" to morning,
                    "${TimeBucket.DAY.name}_${DayType.WEEKDAY.name}" to day,
                    "${TimeBucket.NIGHT.name}_${DayType.WEEKDAY.name}" to night,
                ),
            )
            applied += pkg
        }
        return applied
    }
}
