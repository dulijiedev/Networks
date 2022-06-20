package com.dol.rxlifecycle.lifecycle

import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.subscriptions.SubscriptionHelper
import io.reactivex.plugins.RxJavaPlugins
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

/**
 * Created by dlj on 2019/9/16.
 * Activity fragment 生命周期观察者
 */
class LifeSubscriber<T>(val downStream: Subscriber<in T>, val scope: Scope) : AbstractLifecycle<Subscription>(scope),
    Subscriber<T> {

    override fun isDisposed(): Boolean {
        return get() == SubscriptionHelper.CANCELLED
    }

    override fun dispose() {
        SubscriptionHelper.cancel(this)
    }

    override fun onComplete() {
        if (isDisposed) return
        lazySet(SubscriptionHelper.CANCELLED)
        try {
            removeObserver()
            downStream.onComplete()
        } catch (ex: Throwable) {
            Exceptions.throwIfFatal(ex)
            RxJavaPlugins.onError(ex)
        }
    }

    override fun onSubscribe(s: Subscription?) {
        if (SubscriptionHelper.setOnce(this, s)) {
            try {
                addObserver()
                downStream.onSubscribe(s)
            } catch (ex: Throwable) {
                Exceptions.throwIfFatal(ex)
                s?.cancel()
                onError(ex)
            }
        }
    }

    override fun onNext(t: T) {
        if (isDisposed) return
        try {
            downStream.onNext(t)
        } catch (ex: Throwable) {
            Exceptions.throwIfFatal(ex)
            get().cancel()
            onError(ex)
        }
    }

    override fun onError(t: Throwable?) {
        if (isDisposed) {
            t?.let {
                RxJavaPlugins.onError(it)
            }
            lazySet(SubscriptionHelper.CANCELLED)
            try {
                removeObserver()
                downStream.onError(t)
            } catch (e: Throwable) {
                Exceptions.throwIfFatal(e)
                RxJavaPlugins.onError(CompositeException(t, e))
            }
        }
    }

}