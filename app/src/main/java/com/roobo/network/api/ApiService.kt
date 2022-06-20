package com.roobo.network.api

import com.dol.networklib.data.BaseResponse
import com.roobo.network.data.ApiResponse
import com.roobo.network.data.BannerDataItem
import com.roobo.network.data.MusicModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("app/version/list")
    fun getMusicList(@Query("type") type: Int): Observable<BaseResponse<MusicModel>>

    @GET("banner/json")
    fun getBannerJson(): Observable<ApiResponse<List<BannerDataItem>>>
}