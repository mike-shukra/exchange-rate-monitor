package ru.yogago.exchangeratemonitor.ui.main

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.await
import ru.yogago.exchangeratemonitor.MyService
import ru.yogago.exchangeratemonitor.api.ApiFactory
import ru.yogago.exchangeratemonitor.data.AppConstants.Companion.LOG_TAG
import ru.yogago.exchangeratemonitor.data.CourseMount
import ru.yogago.exchangeratemonitor.data.Data
import ru.yogago.exchangeratemonitor.data.Record
import ru.yogago.exchangeratemonitor.data.ValCurs
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    val data: MutableLiveData<Data> = MutableLiveData()
    val course: MutableLiveData<List<Record>> = MutableLiveData()

    fun go(){
        val context = getApplication<Application>().applicationContext
        context.startService(Intent(context, MyService::class.java))
        launch {
            data.postValue(Data(s = "Service already running."))
            getCourse()

        }
    }

    private suspend fun getRemoteData() {
        val request: Call<ValCurs> = ApiFactory.API.getXmlAsync()
        try {
            val response = request.await()
            response.valute?.forEach {
                Log.d(
                    LOG_TAG,
                    "MainViewModel - getRemoteData - data: ${it.name} - ${it.value} - ${it.numCode} - ${it.nominal} - ${it.charCode}"
                )
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "MainViewModel - getRemoteData - Exception: $e")
        }
    }

    private suspend fun getCourse() {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        calendar.add(Calendar.DATE, -30)
        val beforeDate = calendar.time
        val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val toDay: String = dateFormat.format(currentDate)
        val before: String = dateFormat.format(beforeDate)

        val request: Call<CourseMount> = ApiFactory.API.getCourseMonthlyAsync(before, toDay, "R01235")
        try {
            val response = request.await()
            course.postValue(response.valute)
            response.valute?.forEach {
                Log.d(LOG_TAG, "MainViewModel - getRemoteData - CourseMount: value: ${it.value}, nominal: ${it.nominal}")
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "MainViewModel - getRemoteData - Exception: $e")
        }
    }


    // Custom method to determine whether a service is running
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val context = getApplication<Application>().applicationContext
        val activityManager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Loop through the running services
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                // If the service is running then return true
                return true
            }
        }
        return false
    }

}