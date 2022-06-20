package com.roobo.network.api

import com.dol.networklib.NetUtils

const val BASE_URL = "https://www.wanandroid.com/"

object RetrofitHelper {

    val apiService: ApiService
        get() = NetUtils.get().getApiService(BASE_URL, ApiService::class.java)
}