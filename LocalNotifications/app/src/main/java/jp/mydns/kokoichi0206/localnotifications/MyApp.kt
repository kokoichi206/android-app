package jp.mydns.kokoichi0206.localnotifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES.O

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CounterNotificationService.COUNTER_CHANNEL_ID,
            "Counter",
//            NotificationManager.IMPORTANCE_HIGH,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        channel.description = "Used for the increment counter notifications."

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
//        notificationManager.deleteNotificationChannel(channel.id)
    }
}