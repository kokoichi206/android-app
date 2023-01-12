package jp.mydns.kokoichi0206.playground.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

//@AndroidEntoryPoint
class AlarmReceiver : BroadcastReceiver() {

    // @Inject

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        Log.d("hoge", "Alarm triggerd message: $message")
    }
}