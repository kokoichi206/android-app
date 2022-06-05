package jp.mydns.kokoichi0206.effecthandler

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorModule {

    @LightSensorImpl
    @Provides
    @Singleton
    fun provideLightSensor(app: Application): MeasurableSensor {
        return LightSensor(app)
    }

    @ProximitySensorImpl
    @Provides
    @Singleton
    fun provideProximitySensor(app: Application): MeasurableSensor {
        return ProximitySensor(app)
    }

    @StepCounterSensorImpl
    @Provides
    @Singleton
    fun provideStepCounterSensor(app: Application): MeasurableSensor {
        return StepCounterSensor(app)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LightSensorImpl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProximitySensorImpl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StepCounterSensorImpl
