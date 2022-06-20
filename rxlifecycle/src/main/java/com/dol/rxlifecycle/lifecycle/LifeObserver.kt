package com.dol.rxlifecycle.lifecycle

import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by dlj on 2019/9/16.
 */
internal class LifeObserver<T>(private val downstream: Observer<in T>, scope: Scope) :
    AbstractLifecycle<Disposable>(scope),
    Observer<T> {

    override fun isDisposed(): Boolean {
        return DisposableHelper.isDisposed(get())
    }

    override fun onSubscribe(d: Disposable) {
        if (DisposableHelper.setOnce(this, d)) {
            try {
                addObserver()
                downstream.onSubscribe(d)
            } catch (ex: Throwable) {
                Exceptions.throwIfFatal(ex)
                d.dispose()
                onError(ex)
            }
        }
    }

    override fun onNext(t: T) {
        if (isDisposed) return
        try {
            downstream.onNext(t)
        } catch (e: Throwable) {
            Exceptions.throwIfFatal(e)
            get().dispose()
            onError(e)
        }

    }

    override fun onError(t: Throwable) {
        if (isDisposed) {
            RxJavaPlugins.onError(t)
            return
        }
        lazySet(DisposableHelper.DISPOSED)
        try {
            removeObserver()
            downstream.onError(t)
        } catch (e: Throwable) {
            Exceptions.throwIfFatal(e)
            RxJavaPlugins.onError(CompositeException(t, e))
        }

    }

    override fun onComplete() {
        if (isDisposed) return
        lazySet(DisposableHelper.DISPOSED)
        try {
            removeObserver()
            downstream.onComplete()
        } catch (e: Throwable) {
            Exceptions.throwIfFatal(e)
            RxJavaPlugins.onError(e)
        }

    }

    override fun dispose() {
        DisposableHelper.dispose(this)
    }
}
