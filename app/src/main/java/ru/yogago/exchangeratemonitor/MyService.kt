package ru.yogago.exchangeratemonitor

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext


class MyService : Service(), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    lateinit var nm: NotificationManager

    override fun onCreate() {
        super.onCreate()
        Log.d("myLog", "MyService - onCreate")
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

        val intent2 = createIntent("action 2", "extra 2")
        sendNotif(0, PendingIntent.getBroadcast(this, 0, intent2, 0))

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

    private fun createIntent(action: String?, extra: String?): Intent {
        val intent = Intent(this, MyReceiver::class.java)
        intent.action = action
        intent.putExtra("extra", extra)
        return intent
    }

    private fun sendNotif(id: Int, pendingIntent: PendingIntent) {
        val builder = NotificationCompat.Builder(
            applicationContext,
            "ru.yogago.exchangeratemonitor"
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("My notification: id: $id")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
        nm.notify(1234, builder.build())
    }

}