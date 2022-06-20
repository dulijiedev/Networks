package com.dol.rxlifecycle.source

import com.dol.rxlifecycle.lifecycle.LifeMaybeObserver
import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.internal.operators.maybe.MaybeCallbackObserver
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by dlj on 2019/9/20.
 */
class MaybeLife<T>(val upStream: Maybe<T>, scope: Scope, onMain: Boolean) :
    RxSource<MaybeObserver<in T>>(scope, onMain) {


    override fun subscribe(): Disposable {
        return subscribe(Functions.emptyConsumer())
    }

    fun subscribe(
        onSuccess: Consumer<in T> = Functions.emptyConsumer(),
        onError: Consumer<in Throwable> = Functions.ON_ERROR_MISSING,
        onComplete: Action = Functions.EMPTY_ACTION
    ): Disposable {
        ObjectHelper.requireNonNull(onSuccess, "onSuccess is null")
        ObjectHelper.requireNonNull(onError, "onError is null")
        ObjectHelper.requireNonNull(onComplete, "onComplete is null")
        return subscribeWith(MaybeCallbackObserver<T>(onSuccess, onError, onComplete))
    }

    override fun subscribe(observer: MaybeObserver<in T>) {
        ObjectHelper.requireNonNull(observer, "observer is null")
        val observer = RxJavaPlugins.onSubscribe(upStream, observer)
        ObjectHelper.requireNonNull(
            observer,
            "The RxJavaPlugins.onSubscribe hook returned a null MaybeObserver. Please check the handler provided to RxJavaPlugins.setOnMaybeSubscribe for invalid null returns. Further reading: https://github.com/ReactiveX/RxJava/wiki/Plugins"
        )
        try {
            subscribeActual(observer)
        } catch (ex: NullPointerException) {
            throw ex
        } catch (ex: Throwable) {
            Exceptions.throwIfFatal(ex)
            val npe = NullPointerException("subscribeActual failed")
            npe.initCause(ex)
            throw  npe
        }
    }

    private fun subscribeActual(observer: MaybeObserver<in T>) {
        var upStream = this.upStream
        if (onMain) {
            upStream = upStream.observeOn(AndroidSchedulers.mainThread())
        }
        upStream.onTerminateDetach().subscribe(LifeMaybeObserver<T>(observer, scope))
    }


}