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


## chap6
- [Mastodon](https://androidbook2020.keiji.io/web/timelines/home)
  - id: kokoichi
  - pw: hogehoge
  - Access Token
- この辺を書いたメモを、`instance.properties`にまとめる
  - `.gitignore`に追加
  - 階層に注意

## chap7
- [material designs](https://material.io/resources/icons/?style=baseline)
- p186の`android:id=@+id/root`は、`id/fragment_container`にした
- ActivityはAndroidのシステムコンポーネント
  - →システムコンポーネントは`Manifest.xml`に登録する必要がある！
- 暗黙的インテントの例
```kotlin
val intent = Intent(
  Intent.ACTION_VIEW,
  Uri.parse("geo:34.6960586,135.5126556)
)
startActivity(intent)
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

