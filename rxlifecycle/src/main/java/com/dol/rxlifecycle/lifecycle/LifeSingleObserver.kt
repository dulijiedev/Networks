package com.dol.rxlifecycle.lifecycle

import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by dlj on 2019/9/16.
 * 感知Activity.fragment 生命周期观察者
 */
internal class LifeSingleObserver<T>(private val downStream: SingleObserver<in T>, scope: Scope) :
    AbstractLifecycle<Disposable>(scope),
    SingleObserver<T> {

    override fun isDisposed(): Boolean {
        return DisposableHelper.isDisposed(get())
    }

    override fun dispose() {
        DisposableHelper.dispose(this)
    }

    override fun onSuccess(t: T) {
        if (isDisposed) return
        lazySet(DisposableHelper.DISPOSED)
        try {
            removeObserver()
            downStream.onSuccess(t)
        } catch (e: Throwable) {
            Exceptions.throwIfFatal(e)
            RxJavaPlugins.onError(e)
        }
    }

    override fun onSubscribe(d: Disposable) {
        if (DisposableHelper.setOnce(this, d)) {
            try {
                addObserver()
                downStream.onSubscribe(d)
            } catch (ex: Throwable) {
                Exceptions.throwIfFatal(ex)
                d.dispose()
                onError(ex)
            }
        }
    }

    override fun onError(e: Throwable) {
        if (isDisposed) {
            RxJavaPlugins.onError(e)
            return
        }
        lazySet(DisposableHelper.DISPOSED)
        try {
            removeObserver()
            downStream.onError(e)
        } catch (ex: Throwable) {
            Exceptions.throwIfFatal(ex)
            RxJavaPlugins.onError(CompositeException(e, ex))
        }
    }

}