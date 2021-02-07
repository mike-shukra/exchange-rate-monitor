package ru.yogago.exchangeratemonitor.api

import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.yogago.exchangeratemonitor.data.CourseMount
import ru.yogago.exchangeratemonitor.data.Data
import ru.yogago.exchangeratemonitor.data.ValCurs


interface Api {
//    @FormUrlEncoded
//    @POST("Api/dataN")
//    fun getDataAsync(
//            @Field("id_user") idUser: String,
//            @Field("code_user") codeUser: String
//    ): Deferred<Response<Data>>
//
    @Headers("Accept: application/vnd.yourapi.v1.full+json", "User-Agent: Your-App-Name")
    @GET("/tasks/{task_id}")
    fun getAsync(@Path("task_id") taskId: Long): Deferred<Response<Data>>

    @GET("scripts/XML_daily.asp")
    fun getCourseDailyAsync(@Query("date_req") date: String): Call<ValCurs>

    @GET("/scripts/XML_dynamic.asp")
    fun getCourseMonthlyAsync(
        @Query("date_req1") dateReq1: String,
        @Query("date_req2") dateReq2: String,
        @Query("VAL_NM_RQ") valNmRq: String
    ): Call<CourseMount>

}