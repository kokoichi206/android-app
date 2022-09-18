package jp.mydns.kokoichi0206.localnotifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import jp.mydns.kokoichi0206.localnotifications.Counter
import jp.mydns.kokoichi0206.localnotifications.CounterNotificationService

class CounterNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val service = CounterNotificationService(context)
        service.showNotification(++Counter.value)
    }
}