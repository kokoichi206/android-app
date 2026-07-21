# CLAUDE.md — android-app

Android の学習用サンプルと自作アプリを 1 リポにまとめた置き場。個々のコマンドは各アプリ直下の
`README.md` / `build.gradle.kts` を参照。ここには横断で非自明な構成と踏み抜きやすい罠だけを書く。

## リポの構造（先に理解する）

- **ルートに単一の Gradle ビルドは無い。** 各ディレクトリ（`QuietLauncher/` `OshiLauncher/`
  `OshiCompanion/` ほか多数のサンプル）が**それぞれ独立した Gradle プロジェクト**（自前の
  `gradlew` / `settings.gradle.kts` / `applicationId`）。作業対象のディレクトリに入ってから
  `./gradlew` を叩く。ルートから一括ビルドはできない。
- 現在の主開発はランチャー 3 アプリ:
  - **QuietLauncher**（`jp.mydns.kokoichi206.quietlauncher`）= 最小コンテキスト系ランチャー。
    スコアリング等のロジック検証用の PoC（仕様は `til/android-launcher/`）。
  - **OshiLauncher**（`jp.mydns.kokoichi206.oshilauncher`）= 装飾系ランチャー
    （アイコン自由配置・推し/通常モード・ウィジェットホスト）。
  - **OshiCompanion**（`jp.mydns.kokoichi206.oshicompanion`）= companion 方式の検証用 PoC。

## 検証（実機 / エミュレータ）

- 検証は `adb` + 実機 **Pixel6** / エミュレータで行う。コードを「直した」と言う前に、実際に
  install して動作を確認する。
- ビルド & 導入は対象アプリ直下で `./gradlew :app:installDebug`、ユニットテストは
  `./gradlew :app:testDebugUnitTest`。`adb devices` で対象を確認し、複数接続時は `adb -s <serial>` で指定。

## ランチャーの HOME ロール（踏み抜きやすい罠）

`CATEGORY_HOME` を宣言しただけでは既定ホームにならない。**HOME ロールの取得が必要**で、未取得だと
「必要な権限が足りません」系で操作が通らず、不具合と誤認しやすい。

- **実機（Android 10+）**: `RoleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)` でシステム
  ダイアログを開いて取得する。Q 未満 / ROLE_HOME 非対応時は `Settings.ACTION_HOME_SETTINGS` へ
  フォールバック。実装の範例は `OshiLauncher/app/src/main/java/.../ui/DefaultLauncher.kt`。
- **エミュレータ**: ダイアログを待たず `adb shell cmd package set-home-activity <pkg>/<activity>` で
  直接切替できる。既定ランチャーに戻すコマンドを含め、具体例は `QuietLauncher/README.md` にある。
