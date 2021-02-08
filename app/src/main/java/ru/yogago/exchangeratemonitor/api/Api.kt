package ru.yogago.exchangeratemonitor.api

import retrofit2.Call
import retrofit2.http.*
import ru.yogago.exchangeratemonitor.data.CourseMount
import ru.yogago.exchangeratemonitor.data.ValCurs


interface Api {
    @GET("scripts/XML_daily.asp")
    fun getCourseDailyAsync(@Query("date_req") date: String): Call<ValCurs>

    @GET("/scripts/XML_dynamic.asp")
    fun getCourseMonthlyAsync(
        @Query("date_req1") dateReq1: String,
        @Query("date_req2") dateReq2: String,
        @Query("VAL_NM_RQ") valNmRq: String
    ): Call<CourseMount>

}