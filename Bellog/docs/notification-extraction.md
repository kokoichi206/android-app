# 通知の抽出とグルーピング

Bellogは`NotificationListenerService`が受け取った通知だけを保存する。通知ドロワーに見えている情報の多くは取得できるが、表示できることと、履歴を会話単位で永続的に分類できることは同じではない。

## 通知が表示されるまで

1. アプリがFCMや自社APIなどからデータを受け取る。
2. アプリが公開する情報を選び、Androidの`Notification`を作る。
3. SystemUIが`Notification`を通知ドロワーへ描画する。
4. Bellogが同じ`Notification`を`NotificationListenerService`で受け取る。

BellogとSystemUIが参照する通知オブジェクトは同じである。そのため、標準テンプレートに入ったタイトル、本文、送信者、時刻などはBellogでも取得できる。一方、通知を作る前のPush payload、アプリの内部DB、サーバー上のデータまでは渡されない。

## Android標準で取得できる情報

### `StatusBarNotification`

- package name
- 通知を識別する`key`
- 通知時刻
- 通知IDとtag
- `Notification`

`key`は投稿された通知インスタンスを識別するもので、会話IDではない。通知の更新追跡には使えるが、通知が消えた後も維持される会話キーとしては扱わない。

### `Notification`

- `shortcutId`
- `group`
- category
- channel ID
- small icon、large icon
- 標準extras
- custom extras
- `RemoteViews`
- actionと`PendingIntent`

### `MessagingStyle`

標準のメッセージ通知では次を取得できる。

- conversation title
- group conversationかどうか
- メッセージ本文
- 送信者名と`Person`
- メッセージ時刻
- 添付データのMIME typeとURI
- message extras
- historic messages

Android標準の`MessagingStyle.Message`にはメッセージIDがない。Bellogでは、標準IDがない場合に会話キー、送信者、時刻、本文、メディアURIからfingerprintを作り、再投稿された同じメッセージを重複表示しない。

### `NotificationListenerService.Ranking`

Rankingからは次を取得できる。

- conversation notificationかどうか
- conversation shortcut
- notification channel
- conversation channel ID
- importance

Bellogの現在の実装は`Notification.shortcutId`を利用している。`Ranking.getConversationShortcutInfo()`と`Ranking.getChannel().getConversationId()`は、標準処理をさらに強化するときの候補である。

## 「グループ」の違い

通知には意味の異なるグループ情報がある。

| 情報 | 目的 | 会話キーにできるか |
| --- | --- | --- |
| `Notification.group` | 複数の通知カードをドロワー上で束ねる | 原則できない |
| group summary | 束ねた通知の要約カード | 保存対象にしない |
| `shortcutId` | 通知と会話ショートカットを関連付ける | 安定していれば利用できる |
| conversation channel ID | 会話ごとの通知設定 | 値があれば利用できる |
| conversation title | 画面に表示する会話名 | fallbackにはできるが衝突・変更がある |
| notification key | 投稿された通知インスタンス | 会話キーにはしない |

例えばLINEの`Notification.group`は全メッセージ通知で共通であり、トーク単位のIDではない。SystemUIは同じgroupの子通知を並べれば表示できるが、Bellogが過去分をトーク単位で保存するには、別途安定した会話IDが必要になる。

## 標準処理で足りるケース

次の条件を満たすアプリは、package固有設定なしで処理できる。

1. `MessagingStyle`に本文、送信者、時刻が入っている。
2. `shortcutId`またはconversation channel IDが会話ごとに安定している。
3. 通知の再投稿でも各メッセージの時刻と本文が維持される。
4. group summaryと子通知を区別できる。

標準処理の会話キー候補は、強い順に次のとおりである。

1. Rankingのconversation shortcut ID
2. `Notification.shortcutId`
3. conversation channel ID
4. `MessagingStyle.conversationTitle`
5. 通知タイトル
6. 通知インスタンス単位のfingerprint

4以降は人間向け表示名を使うため、名前の変更や同名会話による誤分類があり得る。

## 標準処理で足りないケース

### 本文を通知へ載せていない

通知に「新着メッセージがあります」という定型文しかない場合、Bellogは実本文を取得できない。configでも復元できない。

### 安定した会話IDがない

`shortcutId`もconversation channel IDもなく、タイトルに送信者名しかない場合、送信者単位まではまとめられても、会話やメールスレッド単位とは限らない。

### custom `RemoteViews`だけに情報がある

`RemoteViews`は別プロセスでUI操作を再生するための構造であり、タイトル、送信者、本文といった意味を汎用的に抽出する公開APIではない。アプリ更新でlayout IDも変わり得るため、Bellogの標準抽出元にはしない。

### 通知が最新状態しか保持しない

アプリが新着のたびに同じ通知を「最新1件だけ」で置き換える場合、Bellog導入前の履歴は復元できない。Bellogが受信中なら、更新ごとに保存できる。

### 表示名しかない

conversation titleや送信者名は表示には十分だが、改名や同名ユーザーを区別できない。表示名を会話キーに使うのはfallbackとする。

## 標準以外の情報が存在する場所

| 場所 | Bellogからのアクセス | configで利用できるか |
| --- | --- | --- |
| `Notification.extras`の独自キー | 可能 | `extra`で指定可能 |
| `MessagingStyle.Message.extras`の独自キー | 可能 | `messageExtra`で指定可能 |
| `PendingIntent`内部のIntent extras | 不可 | 不可 |
| アプリが受信したFCM data payload | 不可 | 不可 |
| 他アプリのDB、files、preferences | Android sandboxにより不可 | 不可 |
| アプリの認証済みAPI、サーバー | 不可 | 不可 |
| custom `RemoteViews` | オブジェクトは取得可能だが意味解析は非標準 | 原則使わない |
| 実際に開いたアプリ画面 | Accessibilityなら読み取れる場合がある | 通知収集とは分離する |

configは、通知オブジェクトへ既に公開されている独自extrasの選び方を指定するためのものである。通知に存在しない本文を取得したり、Android sandboxを越えたりする機能ではない。

## 実機で確認したアプリ

### LINE

Pixel 6とPixel 9aの現行LINEでは、通常メッセージ通知が`MessagingStyle`を使用している。

- `shortcutId`と`line.chat.id`は同値
- message extrasの`MESSAGE_ID`と`line.message.id`は同値
- 本文、送信者、時刻は`MessagingStyle.Message`に存在
- `Notification.group`は全LINEメッセージで共通

現行版ではLINE独自キーを使わなくても標準情報で同じ分類ができる。LINE固有configは、標準フィールドが欠ける旧版や将来版へのoverrideとしてのみ意味を持つ。

### Slack

チャンネル、DM、スレッドごとの`shortcutId`と`MessagingStyle`があり、標準処理で分類できる。

### Gmail

メール通知に`shortcutId`がなく、通知タイトルは主に送信者である。本文は取得できるが、標準情報だけではメールスレッドを安定して識別できない。

### with

通知は通常形式で、本文は「誰から新着メッセージが届いたか」という定型文である。`MessagingStyle`、`shortcutId`、会話ID、実メッセージ本文は通知に存在しない。表示名単位の近似分類は可能だが、実本文の取得はできない。

## config overrideを追加する判断

package固有設定は、次をすべて確認してから追加する。

1. 標準の`MessagingStyle`、shortcut、conversation channelで不足する。
2. 必要な値が通知またはmessageのextrasに実在する。
3. 同じ会話・メッセージで値が安定することを複数通知で確認した。
4. 値が表示名ではなく識別子であることを確認した。
5. config変更時に既存原本を再分類できる。

分類ルールを変更するときはconfigのminor version以上を更新する。version規則と候補の記法は[`config/README.md`](../config/README.md)を参照する。

## Android公式資料

- [People and conversations](https://developer.android.com/develop/ui/views/notifications/conversations)
- [Notification.MessagingStyle](https://developer.android.com/reference/android/app/Notification.MessagingStyle)
- [Notification](https://developer.android.com/reference/android/app/Notification)
- [NotificationListenerService.Ranking](https://developer.android.com/reference/android/service/notification/NotificationListenerService.Ranking)
- [Create a group of notifications](https://developer.android.com/develop/ui/views/notifications/group)
- [Create a custom notification layout](https://developer.android.com/develop/ui/views/notifications/custom-notification)
