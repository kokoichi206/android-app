package jp.mydns.kokoichi0206.weatherapp.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.mydns.kokoichi0206.weatherapp.data.location.DefaultLocationTracker
import jp.mydns.kokoichi0206.weatherapp.domain.location.LocationTracker
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    // Whenever inject LocationTracker, we use DefaultLocationTracker instance..
    @Binds
    @Singleton
    abstract fun bindLocationTracker(
        defaultLocationTracker: DefaultLocationTracker,
    ): LocationTracker
}