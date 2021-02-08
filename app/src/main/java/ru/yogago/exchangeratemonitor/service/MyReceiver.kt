package ru.yogago.exchangeratemonitor.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import ru.yogago.exchangeratemonitor.data.AppConstants.Companion.LOG_TAG
import java.util.*

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.d(LOG_TAG, "MyReceiver - onReceive");
        Log.d(LOG_TAG, "MyReceiver - onReceive - action = " + intent.action);
        Log.d(LOG_TAG, "MyReceiver - onReceive - extra = " + intent.getStringExtra("extra"));
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Log.d("myLog", "MyReceiver - android.intent.action.BOOT_COMPLETED")
            Alarm(context).setAlarm()
        }

        ContextCompat.startForegroundService(context, Intent(context, MyService::class.java))

//        context.startService(Intent(context, MyService::class.java))

    }
}