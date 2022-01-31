## Play Store に申請するまで





## コード保存メモ
``` kotlin
val startedAt = "2022-01-29 18:00"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val started = LocalDateTime.parse(startedAt, formatter);
        val deadlineStr = "2022-01-31 24:00"
        val deadline = LocalDateTime.parse(deadlineStr, formatter);
        setContent {
            CountDownTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Home(
                        presenter = presenter,
                        startedTime = started,
                        deadline = deadline,
                    )
                }
            }
        }
```


``` bash
$ echo "ho\n\nge" > 1
ho

ge

$ echo `echo "ho\n\nge"` > 1
ho ge

```




