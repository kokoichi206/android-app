package jp.mydns.kokoichi0206.weatherapp.domain.location

import android.location.Location

interface LocationTracker {

    // Using Location package is not SUPER GOOD
    suspend fun getCurrentLocation(): Location?
}
