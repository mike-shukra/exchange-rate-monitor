package ru.yogago.exchangeratemonitor

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext


class MyService : Service(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


    override fun onCreate() {
        super.onCreate()
        Log.d("myLog", "MyService - onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (flags == 0 && START_FLAG_RETRY == 0) {
            Log.d("myLog", "MyService - onStartCommand - это повторный запуск")
        } else {
            Log.d("myLog", "MyService - onStartCommand")
        }

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val time: Long = SystemClock.elapsedRealtime() + 5000
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, 10)
        val time = calendar.timeInMillis

        manager.setRepeating(
            AlarmManager.RTC_WAKEUP, time,
            AlarmManager.INTERVAL_HOUR, PendingIntent.getActivity(applicationContext, 0, intent, 0)
        )
        showRandomNumber()

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")

    }

    override fun onDestroy() {
        Log.d("myLog", "MyService - onDestroy")
        coroutineContext.cancelChildren()
        coroutineContext.cancel()
        super.onDestroy()
    }

    private fun showRandomNumber() {
        val rand = Random()
        val number = rand.nextInt(100)
        launch {

//            Toast
//                .makeText(applicationContext, "Random Number : $number", Toast.LENGTH_SHORT)
//                .show()
        }
    }
}