package ru.yogago.exchangeratemonitor.api

import ru.yogago.exchangeratemonitor.data.AppConstants

object ApiFactory {
    val API : Api = RetrofitFactory.retrofit(AppConstants.BASE_URL)
            .create(Api::class.java)
}