# Quiet Launcher — A-1 PoC

案 A「Quiet Context」のロジック検証用ランチャー。

- 仕様: `til/android-launcher/spec-a1-poc.md`
- コンセプト: `til/android-launcher/launcher-concepts.html`（案 A）
- ロジック解説: `til/android-launcher/concept-a-logic.html`

## 構成

| パス | 役割 |
|---|---|
| `logic/ScoreEngine.kt` | スコア式 + ヒステリシス。Android 非依存・ユニットテスト対象 |
| `logic/CalendarDictionary.kt` | A-1 のキーワード辞書（A-3 で置換予定） |
| `data/StatsStore.kt` | 減衰カウンタの永続化（PoC: SharedPreferences。本実装で Room 化） |
| `data/DemoSeeder.kt` | 受け入れシナリオ用デモデータ |
| `LauncherState.kt` | 状態管理・再計算トリガ |
| `ui/` | ホーム / ドロワー / デバッグパネル |

## 動かし方

```sh
./gradlew :app:installDebug
# ランチャーとして設定（エミュレータ）
adb shell cmd package set-home-activity jp.mydns.kokoichi206.quietlauncher/.MainActivity
```

ホーム右上の ⚙ からデバッグパネルを開き、「デモデータ投入」→ 時間帯バケットを
朝 → 昼 → 夜と切り替えると、入替（swap）と θ 保留（hold）が再計算ログで確認できる。

既定のランチャーに戻す:

```sh
adb shell cmd package set-home-activity com.google.android.apps.nexuslauncher/com.google.android.apps.nexuslauncher.NexusLauncherActivity
```

## テスト

```sh
./gradlew :app:testDebugUnitTest
```
