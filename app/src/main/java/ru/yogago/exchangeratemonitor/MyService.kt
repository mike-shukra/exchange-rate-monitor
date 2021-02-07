package ru.yogago.exchangeratemonitor

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
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
    private val myCurrentCourse: MutableLiveData<Float> = MutableLiveData()
    private val channelId = "ru.yogago.exchangeratemonitor"
    private val myCourse = 75


    override fun onCreate() {
        super.onCreate()
        Log.d("myLog", "MyService - onCreate")
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
                channelId,
                "Exchange rate monitor",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        nm.createNotificationChannel(channel)

        myCurrentCourse.observeForever {
            Log.d(LOG_TAG, "MyService - onStartCommand - myCurrentCourse.observeForever it: $it")
            Log.d(LOG_TAG, "MyService - onStartCommand - myCurrentCourse.observeForever (it > MY_COURSE): ${(it > myCourse)}, myCourse: $myCourse")
            if (it > myCourse) {
                val input = "Course change up"
                val notificationIntent = Intent(this, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0, notificationIntent, 0
                )
                val notification: Notification = Notification.Builder(this, channelId)
                    .setContentTitle("My Service")
                    .setContentText(input)
                    .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
                    .setContentIntent(pendingIntent)
                    .build()
                startForeground(1234, notification)
            } else {
                nm.cancelAll()
                stopForeground(true)
                stopSelf()
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("myLog", "MyService - onStartCommand")
        if (intent != null) {
            launch {
                getCourseDaily()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("myLog", "MyService - onDestroy")
//        coroutineContext.cancelChildren()
//        coroutineContext.cancel()
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
//                Log.d(LOG_TAG, "MainViwModel - getCourseDaily - data: ${it.name} - ${it.value} - ${it.numCode} - ${it.nominal} - ${it.charCode}")
                if (it.charCode.equals("USD")) {
                    currentCourse = it.value!!.replace(",",".").toFloat()
                }
            }
            myCurrentCourse.postValue(currentCourse)
            Log.d(LOG_TAG, "MyService - getCourseDaily - currentCourse: $currentCourse")

        } catch (e: Exception) {
            Log.d(LOG_TAG, "MyService - getCourseDaily - Exception: $e")
        }
    }

}