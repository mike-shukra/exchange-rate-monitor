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
import ru.yogago.exchangeratemonitor.service.MyReceiver
import ru.yogago.exchangeratemonitor.service.MyService
import ru.yogago.exchangeratemonitor.R
import ru.yogago.exchangeratemonitor.service.Alarm
import java.util.*


class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    lateinit var nm: NotificationManager
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
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

        viewModel.go()

        Alarm(requireContext()).setAlarm()

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