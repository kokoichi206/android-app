I learned this from youtube. Thank you!

https://www.youtube.com/watch?v=bTgyDqBoZ_o&ab_channel=PhilippLackner


## how to scrollable ?

FlowRow に渡す modifier に `verticalScroll(rememberScrollState())` をセットするが、スクロールが１番下で終わってくれない。

constraints の値を見ると、Height が無限に広がってしまってそう。。。
（FlowRow の中身で決まるから仕方ないのかな？ただ、デバイスのサイズで fix とかされないんだっけ？）

```
constraints.maxWidth
1080

constraints.maxHeight
2147483647
```
