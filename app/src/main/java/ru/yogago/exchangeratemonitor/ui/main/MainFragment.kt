package ru.yogago.exchangeratemonitor.ui.main

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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


class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    lateinit var nm: NotificationManager
    lateinit var am: AlarmManager
    lateinit var intent1: Intent
    lateinit var intent2: Intent
    lateinit var pIntent1: PendingIntent
    lateinit var pIntent2: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        nm = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        am = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    }

    private fun createIntent(action: String?, extra: String?): Intent {
        val intent = Intent(context, MyReceiver::class.java)
        intent.action = action
        intent.putExtra("extra", extra)
        return intent
    }

    private fun compare() {
        Log.d(LOG_TAG, "intent1 = intent2: " + intent1.filterEquals(intent2))
        Log.d(LOG_TAG, "pIntent1 = pIntent2: " + (pIntent1 == pIntent2))
    }

    private fun sendNotif(id: Int, pendingIntent: PendingIntent) {
        val builder = NotificationCompat.Builder(requireContext(), "ru.yogago.exchangeratemonitor")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("My notification: id: $id")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
        nm.notify(1234, builder.build())
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(id: String, name: String,
//                                          description: String) {
//
//        val importance = NotificationManager.IMPORTANCE_LOW
//        val channel = NotificationChannel(id, name, importance)
//
//        channel.description = description
//        channel.enableLights(true)
//        channel.lightColor = Color.RED
//        channel.enableVibration(true)
//        channel.vibrationPattern =
//            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
//        nm.createNotificationChannel(channel)
//    }

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

        val serviceClass = MyService::class.java
        val intent = Intent(context, serviceClass)

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

        intent1 = createIntent("action 1", "extra 1")
        pIntent1 = PendingIntent.getBroadcast(context, 0, intent1, 0)

        intent2 = createIntent("action 2", "extra 2")
        pIntent2 = PendingIntent.getBroadcast(context, 0, intent2, 0)

        compare()


//        createNotificationChannel(
//            "ru.yogago.exchangeratemonitor",
//            "NotifyDemo News",
//            "Example News Channel")

        sendNotif(1, pIntent1)
        sendNotif(2, pIntent2)
    }

}