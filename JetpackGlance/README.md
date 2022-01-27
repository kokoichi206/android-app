# Jetpack Glance
- [developer site](https://developer.android.com/jetpack/androidx/releases/glance?hl=ja)
- [How to build Modern Android App Widgets in Android 12](https://www.youtube.com/watch?v=15Q7xqxBGG0&ab_channel=AndroidDevelopers)
- [Code sample](https://github.com/android/user-interface-samples/tree/glance/AppWidget/glance-widget)
- [GlanceAppWidget](https://developer.android.com/reference/kotlin/androidx/glance/appwidget/GlanceAppWidget)

## Memo

### Better Tooling for AppWidgets
Welcome Glance to the Jetpack family!

- Build AppWidgets faster using Kotlin and Compose style code
- Android 12 imporovements out of the box
- Backwards compatible AppWidgets

No more xml AppWidget

> Similar syntax as Jetpack Compose but adapted for AppWidgets

Bye bye PendingIntents, Hello Actions


``` kotlin
Button(
    text = "Home",
    modifier = Modifier.clickable(launchActivity<NavigationActivity>(...))
)
```

### 必要だったこと

#### gradle.properties
``` 
implementation 'androidx.glance:glance-appwidget:+'
classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31"
compose_version = "1.1.0-beta03"
```

#### AndroidManifest
meta-data が必要、ってことは、resource ファイルが必要（）

``` xml
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:supportsRtl="true"
    android:theme="@style/Theme.JetpackGlance">

    <receiver
        android:name=".GreetingsWidgetReceiver"
        android:enabled="true"
        android:exported="false">
        <intent-filter>
            <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
        </intent-filter>

        <meta-data
            android:name="android.appwidget.provider"
            android:resource="@xml/first_glance_widget_info" />
    </receiver>
```

### resource meta-data
initialLayout がないと動きませんでした！！！

```xml
<!--
Metadata for the first Glance widget.
At the moment, specifying a layout from the android:initialLayout is needed.
-->
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="180dp"
    android:minHeight="50dp"
    android:resizeMode="horizontal|vertical"
    android:initialLayout="@layout/widget_loading"
    android:targetCellWidth="3"
    android:targetCellHeight="1"
    android:widgetCategory="home_screen" />
```


## Create Responsive AppWidgets

### Single
``` kotlin
val size = LocalSize.current
...
```

The content will not be refleshed ?

### Exact
``` kotlin
override val sizeMode = SizeMode.Exact

@Composable
override fun Content() {
    val size = LocalSize.current

    if (size.width > 110.dp)
}
```

### Responsive
```
companio 
```



## 注意点
- Jetpack Compose 用の API とは似ているが異なるものなので、import を慎重に選ぶこと
- 更新が効かないと思ったら Widget を作り直す(削除⇨再度Widget登録)


## Note
> 注: Glance はコンポーザブルの独自のセットを使用します。androidx.compose コンポーザブルと androidx.glance コンポーザブルを組み合わせないでください。
