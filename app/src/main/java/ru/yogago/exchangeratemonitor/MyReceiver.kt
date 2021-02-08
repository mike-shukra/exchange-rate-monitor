package ru.yogago.exchangeratemonitor

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

            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, MyReceiver::class.java).let { mayIntent ->
                PendingIntent.getBroadcast(context, 0, mayIntent, 0)
            }

            // Set the alarm to start at 8:30 a.m.
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 22)
                set(Calendar.MINUTE, 30)
            }

            // setRepeating() lets you specify a precise custom interval--in this case
            val time: Long = (1000 * 60 * 5)
            alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                time,
                alarmIntent
            )
        }

        ContextCompat.startForegroundService(context, Intent(context, MyService::class.java))

//        context.startService(Intent(context, MyService::class.java))

    }
}