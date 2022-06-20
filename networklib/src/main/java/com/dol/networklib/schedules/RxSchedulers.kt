package com.dol.networklib.schedules

import com.dol.networklib.data.BaseResponse
import com.dol.networklib.data.Optional
import com.dol.networklib.exceptions.ServerException
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by dlj on 2019/9/23.
 * thread transform
 */
fun <T> obsIO2Main(): ObservableTransformer<T, T> {
    return ObservableTransformer {
        it.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}


/**
 * http请求结果处理方法
 *
 * @param <T>
 * @return
</T> */
fun <T> handle_result(): ObservableTransformer<BaseResponse<T>, Optional<T>> {
    return ObservableTransformer{ upstream ->
        upstream
            .flatMap {
                if(it.success){
                    createHttpData(it.transform())
                }else{
                    Observable.error(ServerException(it.code,it.msg))
                }
            }
    }
}


fun <T> createHttpData(t: Optional<T>): Observable<Optional<T>> {

    return Observable.create { e ->
        try {
            e.onNext(t)
            e.onComplete()
        } catch (exc: Exception) {
            e.onError(exc)
        }
    }
}