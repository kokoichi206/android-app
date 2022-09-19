# First KMM Project

## Installation

```
brew install kdoctor
```

この見せ方好き、v でチェックを表す

``` sh
$ kdoctor
[v] System                                           
    OS: macOS (12.4)
    CPU: Apple M1
[v] Java
    Java (openjdk version "17.0.2" 2022-01-18 LTS)
    Location: /Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home/bin/java
    
    * JAVA_HOME is not set
          Consider adding the following to ~/.zprofile for setting JAVA_HOME
          export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
    
[x] Android Studio
    * Android Studio (2021.1)
      Location: /Applications/Android Studio.app
      Bundled Java: openjdk 11.0.11 2021-04-20
      Kotlin Plugin: 211-1.6.21-release-334-AS7442.40
      Kotlin Multiplatform Mobile Plugin: not installed
          Install Kotlin Multiplatform Mobile plugin - https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile
```


### XCode なんかエラー

``` sh
[x] Xcode
    Xcode (13.4.1)
    Location: /Applications/Xcode.app
    
    * Xcode requires to perform the First Launch tasks
          Launch Xcode or execute 'xcodebuild -runFirstLaunch' in terminal
```

#### 以下リンクに従って直したら解決

https://qiita.com/eytyet/items/59c5bad1c167d5addc68


### Android Studio でプラグインインストール

https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile


### Cocoapods

```
[x] Cocoapods
    ruby (ruby 2.7.6p219 (2022-04-12 revision c9c2245c0a) [arm64-darwin21])
    
    ruby gems (3.1.6)
    
    * cocoapods not found
          Get cocoapods from https://guides.cocoapods.org/using/getting-started.html#installation
```

``` sh
sudo gem install cocoapods
gem install cocoapods-generate
```

### Ready!!

```
Fetching cocoapods-disable-podfile-validations-0.1.1.gem
Fetching cocoapods-generate-2.2.2.gem
Successfully installed cocoapods-disable-podfile-validations-0.1.1
Successfully installed cocoapods-generate-2.2.2
Parsing documentation for cocoapods-disable-podfile-validations-0.1.1
Installing ri documentation for cocoapods-disable-podfile-validations-0.1.1
Parsing documentation for cocoapods-generate-2.2.2
Installing ri documentation for cocoapods-generate-2.2.2
Done installing documentation for cocoapods-disable-podfile-validations, cocoapods-generate after 0 seconds
2 gems installed
❯ kdoctor
[v] System                                           
    OS: macOS (12.4)
    CPU: Apple M1
[v] Java
    Java (openjdk version "17.0.2" 2022-01-18 LTS)
    Location: /Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home/bin/java
    
    * JAVA_HOME is not set
          Consider adding the following to ~/.zprofile for setting JAVA_HOME
          export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
    
[v] Android Studio
    Android Studio (2021.1)
    Location: /Applications/Android Studio.app
    Bundled Java: openjdk 11.0.11 2021-04-20
    Kotlin Plugin: 211-1.6.21-release-334-AS7442.40
    Kotlin Multiplatform Mobile Plugin: 0.3.2(211-1.6.10-release-974-IJ)-91
    
[v] Xcode
    Xcode (13.4.1)
    Location: /Applications/Xcode.app
    
[v] Cocoapods
    ruby (ruby 2.7.6p219 (2022-04-12 revision c9c2245c0a) [arm64-darwin21])
    
    ruby gems (3.1.6)
    
    cocoapods (1.11.3)
    
    cocoapods-generate (2.2.2)
    
Your system is ready for Kotlin Multiplatform Mobile Development!
```


### 初回叩いたとき

``` sh
❯ kdoctor
[v] System                                           
    OS: macOS (12.4)
    CPU: Apple M1
[v] Java
    Java (openjdk version "17.0.2" 2022-01-18 LTS)
    Location: /Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home/bin/java
    
    * JAVA_HOME is not set
          Consider adding the following to ~/.zprofile for setting JAVA_HOME
          export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
    
[x] Android Studio
    * Android Studio (2021.1)
      Location: /Applications/Android Studio.app
      Bundled Java: openjdk 11.0.11 2021-04-20
      Kotlin Plugin: 211-1.6.21-release-334-AS7442.40
      Kotlin Multiplatform Mobile Plugin: not installed
          Install Kotlin Multiplatform Mobile plugin - https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile
    
[x] Xcode
    Xcode (13.4.1)
    Location: /Applications/Xcode.app
    
    * Xcode requires to perform the First Launch tasks
          Launch Xcode or execute 'xcodebuild -runFirstLaunch' in terminal
    
[x] Cocoapods
    ruby (ruby 2.7.6p219 (2022-04-12 revision c9c2245c0a) [arm64-darwin21])
    
    ruby gems (3.1.6)
    
    * cocoapods not found
          Get cocoapods from https://guides.cocoapods.org/using/getting-started.html#installation
    
Failures: 3
KDoctor has diagnosed one or more problems while checking your environment.
Please check the output for problem description and possible solutions.
```


## 文法

### expect - actual

https://kotlinlang.org/docs/multiplatform-connect-to-apis.html


## UI setup

### Jetpack Compose

https://developer.android.com/jetpack/compose/interop/adding


## Hands-on

https://play.kotlinlang.org/hands-on/Networking%20and%20Data%20Storage%20with%20Kotlin%20Multiplatfrom%20Mobile/01_Introductioni


## そのほか

``` sh
adb devices
List of devices attached
adb-8BSX1EC56-SlLKka._adb-tls-connect._tcp.	device
emulator-5554	offline

❯ scrcpy -s adb-8BSX1EC56-SlLKka._adb-tls-connect._tcp.
```

### 注意点

- Kotlin 標準ライブラリしか使えない
