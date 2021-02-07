package ru.yogago.exchangeratemonitor

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.await
import ru.yogago.exchangeratemonitor.api.ApiFactory
import ru.yogago.exchangeratemonitor.data.AppConstants.Companion.LOG_TAG
import ru.yogago.exchangeratemonitor.data.ValCurs
import java.text.DateFormat
import java.text.SimpleDateFormat
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

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    "ru.yogago.exchangeratemonitor",
                    "Exchange rate monitor",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (flags == 0 && START_FLAG_RETRY == 0) {
            Log.d("myLog", "MyService - onStartCommand - это повторный запуск")
        } else {
            Log.d("myLog", "MyService - onStartCommand")
        }

        Log.d("myLog", "MyService - onStartCommand intent: $intent")

        launch {
            getCourseDaily()
        }

//        sendNotification(0, PendingIntent.getBroadcast(this, 0, intent, 0))

        val input = intent?.getStringExtra("inputExtra")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, "ru.yogago.exchangeratemonitor")
            .setContentTitle("Example Service")
            .setContentText(input)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

//        sendNotification(0, pendingIntent)
        return START_NOT_STICKY
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

    private fun sendNotification(id: Int, pendingIntent: PendingIntent) {
        Log.d(LOG_TAG, "MyService - sendNotification id: $id")
        val builder = NotificationCompat.Builder(this,"ru.yogago.exchangeratemonitor")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("My notification: id: $id")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
        nm.notify(1234, builder.build())
    }

    private fun createIntent(action: String?, extra: String?): Intent? {
        val intent = Intent(this, MyReceiver::class.java)
        intent.action = action
        intent.putExtra("extra", extra)
        return intent
    }

    private suspend fun getCourseDaily() {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val toDay: String = dateFormat.format(currentDate)

        val request: Call<ValCurs> = ApiFactory.API.getCourseDailyAsync(toDay)
        try {
            val response = request.await()
            var currentCourse: Float = 0F
            response.valute?.forEach {
//                Log.d(LOG_TAG, "MainViewModel - getCourseDaily - data: ${it.name} - ${it.value} - ${it.numCode} - ${it.nominal} - ${it.charCode}")
                if (it.charCode.equals("USD")) {
                    currentCourse = it.value!!.replace(",",".").toFloat()
                }
            }
//            myCurrentCourse.postValue(currentCourse)
            Log.d(LOG_TAG, "MyService - getCourseDaily - currentCourse: $currentCourse")

        } catch (e: Exception) {
            Log.d(LOG_TAG, "MyService - getCourseDaily - Exception: $e")
        }
    }

}