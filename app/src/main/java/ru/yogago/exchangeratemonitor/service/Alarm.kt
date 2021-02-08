package ru.yogago.exchangeratemonitor.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class Alarm(context: Context) {
    private val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, MyReceiver::class.java).let { mayIntent ->
        PendingIntent.getBroadcast(context, 0, mayIntent, 0)
    }

    fun setAlarm(){
        // Set the alarm to start at 1:5 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 5)
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
}