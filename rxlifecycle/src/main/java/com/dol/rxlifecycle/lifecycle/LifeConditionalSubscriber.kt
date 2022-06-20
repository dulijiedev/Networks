package com.dol.rxlifecycle.lifecycle

import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.fuseable.ConditionalSubscriber
import io.reactivex.internal.subscriptions.SubscriptionHelper
import io.reactivex.plugins.RxJavaPlugins
import org.reactivestreams.Subscription

/**
 * Created by dlj on 2019/9/20.
 */
class LifeConditionalSubscriber<T>(val downStream: ConditionalSubscriber<in T>, scope: Scope) :
    AbstractLifecycle<Subscription>(scope), ConditionalSubscriber<T> {

    override fun isDisposed(): Boolean {
        return get() == SubscriptionHelper.CANCELLED
    }

    override fun dispose() {
        SubscriptionHelper.cancel(this)
    }

    override fun onComplete() {
        if (isDisposed) return
        try {
            removeObserver()
            downStream.onComplete()
        } catch (e: Throwable) {
            Exceptions.throwIfFatal(e)
            RxJavaPlugins.onError(e)
        }
    }

    override fun tryOnNext(t: T): Boolean {
        if (!isDisposed) {
            return downStream.tryOnNext(t)
        }
        return false
    }

    override fun onSubscribe(s: Subscription) {
        if (SubscriptionHelper.setOnce(this, s)) {
            try {
                addObserver()
                downStream.onSubscribe(s)
            } catch (ex: Throwable) {
                Exceptions.throwIfFatal(ex)
                s.cancel()
                onError(ex)
            }
        }
    }

    override fun onNext(t: T) {
        if (isDisposed) return
        try {
            downStream.onNext(t)
        } catch (e: Throwable) {
            Exceptions.throwIfFatal(e)
            get().cancel()
            onError(e)
        }
    }

    override fun onError(t: Throwable) {
        if (isDisposed) {
            RxJavaPlugins.onError(t)
            return
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