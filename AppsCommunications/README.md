``` sh
❯ adb devices

List of devices attached
1C221FDF600AW6  device
emulator-5554   device
```
                                                                                        ``` sh
❯ adb -s emulator-5554 shell "cmd app_function execute-app-function --package com.example.appscommunications --function 'com.example.appscommunic
ations.appfunctions.CounterFunctions#incrementCounter' --parameters '{\"by\": 5}'" 
{
  "androidAppfunctionsReturnValue": [
    "Counter increased by 5. It is now 5."
  ]
}
```

## Links

- Appfunctions
  - https://developer.android.com/ai/appfunctions?hl=ja
