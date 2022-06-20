package com.roobo.network.observable

import com.dol.networklib.data.BaseResponse
import com.dol.networklib.data.Optional
import com.dol.networklib.exceptions.ServerException
import com.dol.networklib.functions.HttpErrorFunction
import com.dol.networklib.schedules.createHttpData
import com.dol.networklib.schedules.handle_result
import com.dol.networklib.schedules.obsIO2Main
import com.roobo.network.data.ApiResponse
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * 返回结果data null问题 防止为空时RxJava2不向下传递data  默认结构BaseResponse
 * 如果结构不是BaseResponse可重写下面方法进行自定义结构
 */
fun <T> getApiObservable(apiObservable: Observable<out ApiResponse<T>>): Observable<Optional<T>> {
    return apiObservable
        .compose(newHandleResult())
        .onErrorResumeNext(HttpErrorFunction())
        .compose(obsIO2Main())
}

internal fun <T> newHandleResult(): ObservableTransformer<ApiResponse<T>, Optional<T>> {
    return ObservableTransformer { upstream ->
        upstream
            .flatMap {
                if (it.errorCode == 0) {
                    createHttpData(it.transform())
                } else {
                    Observable.error(ServerException(it.errorCode, it.errorMsg))
                }
            }
    }
}