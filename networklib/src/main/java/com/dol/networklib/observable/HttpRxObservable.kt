package com.dol.networklib.observable

import com.dol.networklib.data.BaseResponse
import com.dol.networklib.data.Optional
import com.dol.networklib.functions.HttpErrorFunction
import com.dol.networklib.schedules.handle_result
import com.dol.networklib.schedules.obsIO2Main
import io.reactivex.Observable

/**
 * Created by dlj
 */

/**
 * 返回结果data null问题 防止为空时RxJava2不向下传递data
 * 如果修改了BaseResponse可重写此方法
 */
fun <T> getObservable(apiObservable: Observable<out BaseResponse<T>>): Observable<Optional<T>> {
    return apiObservable
        .compose(handle_result())
        .onErrorResumeNext(HttpErrorFunction())
        .compose(obsIO2Main())
}