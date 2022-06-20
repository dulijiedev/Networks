package com.roobo.network.api

import com.dol.networklib.data.Optional
import com.dol.networklib.observable.getObservable
import com.roobo.network.data.BannerDataItem
import com.roobo.network.data.MusicModel
import com.roobo.network.observable.getApiObservable
import io.reactivex.Observable


fun getMusicList(): Observable<Optional<MusicModel>> {
    return getObservable(RetrofitHelper.apiService.getMusicList(5))
}

fun getBannerJson():Observable<Optional<List<BannerDataItem>>>{
    return getApiObservable(RetrofitHelper.apiService.getBannerJson())
}