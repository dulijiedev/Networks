package com.dol.networklib.observer

import com.dol.networklib.exceptions.ExceptionEngine
import com.dol.networklib.exceptions.GeneralException
import io.reactivex.observers.DisposableObserver

/**
 * Created by dlj on 2019/9/23.
 */
abstract class HttpRxObserver<T> : DisposableObserver<T>() {

    protected abstract fun onSuccess(t: T)

    protected abstract fun onError(e: GeneralException)

    override fun onNext(value: T) {
        onSuccess(value)
    }

    override fun onError(e: Throwable) {
        when (e) {
            is GeneralException -> {
                onError(e)
            }
            else -> {
                onError(ExceptionEngine.handleException(e))
            }
        }
    }

}