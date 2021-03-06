package ru.yogago.exchangeratemonitor.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import ru.yogago.exchangeratemonitor.BuildConfig
import java.util.concurrent.TimeUnit


object RetrofitFactory{

    private val authInterceptor = Interceptor { chain->
        val newUrl = chain
                .request()
                .url
                .newBuilder()
                .build()

        val newRequest = chain
                .request()
                .newBuilder()
                .url(newUrl)
                .build()

        chain.proceed(newRequest)
    }

    private val loggingInterceptor =  HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client =
            if (BuildConfig.DEBUG) {
                OkHttpClient()
                        .newBuilder()
                        .connectTimeout(7, TimeUnit.SECONDS)
                        .readTimeout(7, TimeUnit.SECONDS)
                        .writeTimeout(7, TimeUnit.SECONDS)
                        .addInterceptor(authInterceptor)
                        .addInterceptor(loggingInterceptor)
                        .build()
            } else {
                OkHttpClient()
                        .newBuilder()
                        .connectTimeout(7, TimeUnit.SECONDS)
                        .readTimeout(7, TimeUnit.SECONDS)
                        .writeTimeout(7, TimeUnit.SECONDS)
                        .addInterceptor(loggingInterceptor)
                        .addInterceptor(authInterceptor)
                        .build()
            }

    fun retrofit(baseUrl: String) : Retrofit = Retrofit
            .Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(Persister(AnnotationStrategy())))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
}