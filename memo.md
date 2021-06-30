# Study まとめ

## chap4
- permission: AndroidManifest.xml
- Retrofitを用いてAPI通信
  - API通信を便利に行うためのライブラリ
- [Mastodon API のドキュメント](https://docs.joinmastodon.org/client/)
- DataBindingの機能
  - 一方高データバインディング
- `Cannot fit requested classes in a single dex file` @p98
  - [このサイト](https://aqlier.com/2018/06/12/cannot-fit-requested/)通りにやる
    - まだ解決しない（下）
- `2 files found with path 'androidsupportmultidexversion.txt'. Adding a packagingOptions block may help, please refer to`
  - build.gradle -> androidのなかに、以下を記述したら解決
  ```
  packagingOptions {
      pickFirst "androidsupportmultidexversion.txt"
  }
  ```



## 疑問
- binding?.button?.text = "clicked" の ? って何？
  - nullかどうかのチェック？
  - !! ってのもある
- 下のように、１行開けるのがいい書き方なの？
  ```
  class MastodonApi {
    
    @GET("api/v1/timelines")
  }
  ```
- あれ、Viewの左と右の2つの端末なんだっけ？
  - Androidと何か？
- val, var
- `binding = bindingData ?: return`

