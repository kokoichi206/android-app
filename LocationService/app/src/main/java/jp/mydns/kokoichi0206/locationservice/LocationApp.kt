package jp.mydns.kokoichi0206.locationservice

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class LocationApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW,
            )
            val notificationmanage = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationmanage.createNotificationChannel(channel)
        }
    }
}