package jp.mydns.kokoichi0206.effecthandler

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object SensorModule {
//
//    @LightSensorImpl
//    @Provides
//    @Singleton
//    fun provideLightSensor(app: Application): c {
//        return LightSensor(app)
//    }
//
//    @ProximitySensorImpl
//    @Provides
//    @Singleton
//    fun provideProximitySensor(app: Application): MeasurableSensor {
//        return ProximitySensor(app)
//    }
//
//    @StepCounterSensorImpl
//    @Provides
//    @Singleton
//    fun provideStepCounterSensor(app: Application): MeasurableSensor {
//        return StepCounterSensor(app)
//    }
//}
//
//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class LightSensorImpl
//
//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class ProximitySensorImpl
//
//@Qualifier
//@Retention(AnnotationRetention.BINARY)
//annotation class StepCounterSensorImpl

interface AppModule {
    val lightSensor: MeasurableSensor
    val proximitySensor: MeasurableSensor
    val stepCounterSensor: MeasurableSensor
}

class AppModuleImpl(
    private val appContext: Context,
) : AppModule {
    override val lightSensor: MeasurableSensor by lazy {
        LightSensor(appContext)
    }

    override val proximitySensor: MeasurableSensor by lazy {
        ProximitySensor(appContext)
    }

    override val stepCounterSensor: MeasurableSensor by lazy {
        StepCounterSensor(appContext)
    }
}
