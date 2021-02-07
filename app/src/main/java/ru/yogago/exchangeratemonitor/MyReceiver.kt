package ru.yogago.exchangeratemonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ru.yogago.exchangeratemonitor.data.AppConstants.Companion.LOG_TAG

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.d(LOG_TAG, "MyReceiver - onReceive");
        Log.d(LOG_TAG, "MyReceiver - onReceive - action = " + intent.action);
        Log.d(LOG_TAG, "MyReceiver - onReceive - extra = " + intent.getStringExtra("extra"));
        context.startService(Intent(context, MyService::class.java))
    }
}