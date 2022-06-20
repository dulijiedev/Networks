package com.dol.rxlifecycle.source

import com.dol.rxlifecycle.lifecycle.LifeSingleObserver
import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.internal.observers.BiConsumerSingleObserver
import io.reactivex.internal.observers.ConsumerSingleObserver
import io.reactivex.plugins.RxJavaPlugins
import java.lang.NullPointerException

/**
 * Created by dlj on 2019/9/20.
 */
class SingleLife<T>(val upStream: Single<T>, scope: Scope, onMain: Boolean) :
    RxSource<SingleObserver<in T>>(scope, onMain) {


    override fun subscribe(): Disposable {
        return subscribe(Functions.emptyConsumer(), Functions.ON_ERROR_MISSING)
    }

    internal fun subscribe(onCallback: BiConsumer<in T, in Throwable>): Disposable {
        ObjectHelper.requireNonNull(onCallback, "onCallback is null")
        val observer = BiConsumerSingleObserver<T>(onCallback)
        subscribe(observer)
        return observer
    }

    internal fun subscribe(
        onSuccess: Consumer<in T> = Functions.emptyConsumer(),
        onError: Consumer<in Throwable> = Functions.ON_ERROR_MISSING
    ): Disposable {
        ObjectHelper.requireNonNull(onSuccess, "onSuccess is null")
        ObjectHelper.requireNonNull(onError, "onError is null")
        val observer = ConsumerSingleObserver<T>(onSuccess, onError)
        subscribe(observer)
        return observer
    }

    override fun subscribe(observer: SingleObserver<in T>) {
        ObjectHelper.requireNonNull(observer, "observer is null")
        val observer = RxJavaPlugins.onSubscribe(upStream, observer)
        try {
            subscribeActual(observer)
        } catch (ex: NullPointerException) {
            throw ex
        } catch (ex: Throwable) {
            Exceptions.throwIfFatal(ex)
            val npe = NullPointerException("subscribeActual failed")
            npe.initCause(ex)
            throw npe
        }
    }

    private fun subscribeActual(observer: SingleObserver<in T>) {
        var upStream = this.upStream
        if (onMain) {
            upStream = upStream.observeOn(AndroidSchedulers.mainThread())
        }
        upStream.onTerminateDetach().subscribe(LifeSingleObserver<T>(observer, scope))
    }
}