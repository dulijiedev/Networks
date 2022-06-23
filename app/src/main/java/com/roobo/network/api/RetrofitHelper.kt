package com.roobo.network.api

import com.dol.networklib.NetUtils
import okhttp3.Interceptor
import okhttp3.Request

const val BASE_URL = "https://www.wanandroid.com/"

object RetrofitHelper {

    init {
        val interceptor = Interceptor {
            val original: Request = it.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .header("token", "xxx")
                .header("token", "yyy")
            val request: Request = requestBuilder.build()
            return@Interceptor it.proceed(request)
        }
        NetUtils.interceptors.add(interceptor)
    }
    val apiService: ApiService
        get() = NetUtils.get().getApiService(BASE_URL, ApiService::class.java)
}