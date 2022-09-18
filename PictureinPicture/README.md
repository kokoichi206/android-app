# Picture in Picture

https://www.youtube.com/watch?v=_CDxUCHaQWM&ab_channel=PhilippLackner

## メモ

pip に入ると、configuration change が呼ばれてしまうが、Manifest に `android:configChanges="screenSize|smallestScreenSize|screenLayout|orientation"` を加えることで、これを Activity が扱わないようにできる（Activity の再生成を行わせない！）。
