package ru.yogago.exchangeratemonitor.ui.main

import android.app.Application
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
import ru.yogago.exchangeratemonitor.service.MyService
import ru.yogago.exchangeratemonitor.api.ApiFactory
import ru.yogago.exchangeratemonitor.data.AppConstants.Companion.LOG_TAG
import ru.yogago.exchangeratemonitor.data.CourseMount
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
    val courses: MutableLiveData<List<Record>> = MutableLiveData()
    val myCurrentCourse: MutableLiveData<Float> = MutableLiveData()

    fun go(){
        val context = getApplication<Application>().applicationContext
        context.startService(Intent(context, MyService::class.java))
        launch {
            getCourse()
            getCourseDaily()
        }
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
            myCurrentCourse.postValue(currentCourse)
        } catch (e: Exception) {
            Log.d(LOG_TAG, "MainViewModel - getCourseDaily - Exception: $e")
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
            courses.postValue(response.valute)
        } catch (e: Exception) {
            Log.d(LOG_TAG, "MainViewModel - getCourse - Exception: $e")
        }
    }

}