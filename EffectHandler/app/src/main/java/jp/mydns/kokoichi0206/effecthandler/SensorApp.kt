package jp.mydns.kokoichi0206.effecthandler

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//@HiltAndroidApp
//class SensorApp : Application()

// Singleton を作るための Application クラス。
// name attribute で Manifest に登録する必要はある。
class SensorApp : Application() {

    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}
