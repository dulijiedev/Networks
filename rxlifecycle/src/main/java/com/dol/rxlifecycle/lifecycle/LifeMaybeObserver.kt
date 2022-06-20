package com.dol.rxlifecycle.lifecycle

import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by dlj on 2019/9/17.
 * Activity Fragment生命周期观察者
 */
internal class LifeMaybeObserver<T>(val downStream: MaybeObserver<in T>, scope: Scope) :
    AbstractLifecycle<Disposable>(scope), MaybeObserver<T> {


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
        } catch (ex: Throwable) {
            Exceptions.throwIfFatal(ex)
            RxJavaPlugins.onError(ex)
        }
    }

    override fun onComplete() {
        if (isDisposed) return
        lazySet(DisposableHelper.DISPOSED)
        try {
            removeObserver()
            downStream.onComplete()
        } catch (ex: Throwable) {
            Exceptions.throwIfFatal(ex)
            RxJavaPlugins.onError(ex)
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