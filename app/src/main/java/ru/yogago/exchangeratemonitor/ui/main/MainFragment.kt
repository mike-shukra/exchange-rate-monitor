package ru.yogago.exchangeratemonitor.ui.main

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.yogago.exchangeratemonitor.MyReceiver
import ru.yogago.exchangeratemonitor.MyService
import ru.yogago.exchangeratemonitor.R
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

        // Set the alarm to start at 1:5 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 5)
        }

        // setRepeating() lets you specify a precise custom interval--in this case
        val time: Long = (1000 * 60 * 60 * 24)
        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            time,
            alarmIntent
        )

        stopService()
    }

    private fun startService() {
        val input: String = ""
        val serviceIntent = Intent(context, MyService::class.java)
        serviceIntent.putExtra("inputExtra", input)
        ContextCompat.startForegroundService(requireContext(), serviceIntent)
    }
    private fun stopService() {
        val serviceIntent = Intent(context, MyService::class.java)
            context?.stopService(serviceIntent)
    }

}