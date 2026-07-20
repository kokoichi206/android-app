# Bellog grouping config

`grouping-config.json` は通知から会話キーとメッセージキーを生成するローカル設定です。Gradleがこのディレクトリをassetsとして扱い、同じファイルをAPKへ同梱します。

将来公開URLへ切り出す場合も、このJSON schemaとSemVerの規則を維持します。現在のアプリはネットワークへ接続しません。

標準通知で取得できる情報、package固有設定が必要になる条件、configでは取得できない情報については[`docs/notification-extraction.md`](../docs/notification-extraction.md)を参照してください。

## Versioning

- `patch`: 分類結果に影響しない変更のみ。分類ルールを変更したconfigはアプリが拒否します。
- `minor`: 分類ルールの追加・変更。保存済み通知をすべて再分類します。
- `major`: schemaやキーの意味を変更する更新。保存済み通知をすべて再分類します。

アプリ更新後の初回起動時に、APK内のconfig versionと前回適用versionを比較します。

## Candidates

`conversationKeyCandidates`と`messageKeyCandidates`は上から評価し、最初に取得できた値だけを採用します。複数の値は結合しません。

利用できる`source`は次のとおりです。

- `shortcutId`
- `conversationTitle`
- `title`
- `extra`: `key`が必要
- `messageExtra`: `key`が必要
- `fingerprint`: `fields`が必要

package固有ルールで省略したcandidate一覧には`defaults`が使われます。
