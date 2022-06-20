package com.dol.networklib.functions

import android.util.Log
import com.dol.networklib.BuildConfig
import com.dol.networklib.exceptions.ExceptionEngine
import io.reactivex.Observable
import io.reactivex.functions.Function

/**
 * Created by dlj on 2019/9/23.
 * operator exceptions  
 */
const val TAG = "DLJ"

class HttpErrorFunction<T> : Function<Throwable, Observable<T>> {

    override fun apply(t: Throwable): Observable<T> {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "${t.toString()}")
        }
        return Observable.error(ExceptionEngine.handleException(t))
    }

}