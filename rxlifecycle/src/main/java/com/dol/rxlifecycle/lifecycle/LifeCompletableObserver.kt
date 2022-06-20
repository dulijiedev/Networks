package com.dol.rxlifecycle.lifecycle

import com.dol.rxlifecycle.lifecycle.AbstractLifecycle
import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by dlj on 2019/9/16.
 * Activity Fragment 生命周期观察者
 */
class LifeCompletableObserver(val downStream: CompletableObserver, val scope: Scope) :
    AbstractLifecycle<Disposable>(scope), CompletableObserver {

    override fun isDisposed(): Boolean {
        return DisposableHelper.isDisposed(get())
    }

    override fun dispose() {
        DisposableHelper.dispose(this)
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
        if (isDisposed) return
        lazySet(DisposableHelper.DISPOSED)
        try {
            removeObserver()
            downStream.onError(e)
        } catch (ex: Throwable) {
            Exceptions.throwIfFatal(ex)
            RxJavaPlugins.onError(ex)
        }
    }

}