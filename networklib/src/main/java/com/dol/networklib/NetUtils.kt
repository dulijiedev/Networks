package com.dol.networklib

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by dlj on 2019/9/23.
 * network request
 * NetUtils.get().getApiService("xxx",ApiService::class.java)
 */

class NetUtils {

    companion object {
        var interceptors= mutableListOf<Interceptor>()

        private var instance: NetUtils? = null
            get() {
                if (field == null) {
                    field = NetUtils()
                }
                return field
            }

        fun get(): NetUtils {
            return instance!!
        }
    }

    private val logInterceptor: HttpLoggingInterceptor
        get() {
            val it = HttpLoggingInterceptor()
            it.level = HttpLoggingInterceptor.Level.BODY
            return it
        }

    private fun getOkHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)

        if(interceptors.isNotEmpty()){
            interceptors.forEach {
                okHttpClientBuilder.addInterceptor(it)
            }
        }
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addNetworkInterceptor(logInterceptor)
        }
        return okHttpClientBuilder.build()
    }



    private fun createRetrofit(baseUrl: String, converterFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getOkHttpClient())
            .addConverterFactory(converterFactory)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    /**
     * return type Json Bean
     */
    fun <T> getApiService(baseUrl: String, clazz: Class<T>): T {
        return createRetrofit(baseUrl, GsonConverterFactory.create()).create(clazz)
    }

    /**
     * return type String not json
     */
    fun <T> getScalarsApiService(baseUrl: String, clazz: Class<T>): T {
        return createRetrofit(baseUrl, ScalarsConverterFactory.create()).create(clazz)
    }
}