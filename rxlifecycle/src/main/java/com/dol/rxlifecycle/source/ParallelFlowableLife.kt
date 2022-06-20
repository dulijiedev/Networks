package com.dol.rxlifecycle.source

import com.dol.rxlifecycle.lifecycle.LifeConditionalSubscriber
import com.dol.rxlifecycle.lifecycle.LifeSubscriber
import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.internal.fuseable.ConditionalSubscriber
import io.reactivex.internal.subscriptions.EmptySubscription
import io.reactivex.parallel.ParallelFlowable
import org.reactivestreams.Subscriber

/**
 * Created by dlj on 2019/9/20.
 */
class ParallelFlowableLife<T>(val upStream: ParallelFlowable<T>, val scope: Scope, val onMain: Boolean) {

    fun subscribe(@NonNull subscribers: Array<Subscriber<in T>>) {
        if (!validate(subscribers)) {
            return
        }
        val n = subscribers.size
        val parents = arrayOfNulls<Subscriber<in T>>(n)
        for (i in 0 until n) {
            val a = subscribers[i]
            if (a is ConditionalSubscriber<*>) {
                parents[i] = LifeConditionalSubscriber(a as ConditionalSubscriber<in T>, scope)
            } else {
                parents[i] = LifeSubscriber(a, scope)
            }
        }
        var upStream = this.upStream
        if (onMain) upStream = upStream.runOn(AndroidSchedulers.mainThread())
        upStream.subscribe(parents)
    }

    private fun validate(@NonNull subscribers: Array<Subscriber<in T>>): Boolean {
        val p = parallelism()
        if (subscribers.size != p) {
            val iae = IllegalArgumentException("parallelism = " + p + ", subscribers = " + subscribers.size)
            for (s in subscribers) {
                EmptySubscription.error(iae, s)
            }
            return false
        }
        return true
    }

    private fun parallelism(): Int {
        return upStream.parallelism()
    }
}