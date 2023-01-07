package jp.mydns.kokoichi0206.playground

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        // 体感よりは遅れてやってくる！
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("hoge", "Boot completed")
            println("Boot completed")
        }

        context?.let {
            val builder = NotificationCompat.Builder(it, "boot_completed")
                .setSmallIcon(R.drawable.playground)
                .setContentTitle("Boot Completed!")
                .setContentText("main text")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line...!!!!!")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            with(NotificationManagerCompat.from(it)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
            }
        }
    }

}
