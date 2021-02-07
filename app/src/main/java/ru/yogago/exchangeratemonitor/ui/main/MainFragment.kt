package ru.yogago.exchangeratemonitor.ui.main

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.yogago.exchangeratemonitor.MyReceiver
import ru.yogago.exchangeratemonitor.MyService
import ru.yogago.exchangeratemonitor.R
import ru.yogago.exchangeratemonitor.data.AppConstants.Companion.LOG_TAG
import java.util.*


class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    lateinit var nm: NotificationManager
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        nm = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, MyReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    @SuppressLint("ShortAlarm")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv)
        val message = view.findViewById<TextView>(R.id.message)

        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.myCurrentCourse.observe(viewLifecycleOwner, {
            message.text = it.toString()
        })

        viewModel.courses.observe(viewLifecycleOwner, {
            recyclerView.adapter = CustomRecyclerAdapter(it)
        })

        viewModel.data.observe(viewLifecycleOwner, {
            Toast
                .makeText(context, it.s, Toast.LENGTH_SHORT)
                .show()
        })

        viewModel.go()

        // Set the alarm to start at 8:30 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 30)
        }

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 1,
            alarmIntent
        )

    }


}