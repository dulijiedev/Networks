package com.dol.rxlifecycle.source

import com.dol.rxlifecycle.lifecycle.LifeCompletableObserver
import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.internal.observers.CallbackCompletableObserver
import io.reactivex.internal.observers.EmptyCompletableObserver
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by dlj on 2019/9/16.
 */
class CompletableLife(val upStream: Completable, scope: Scope, onMain: Boolean) :
    RxSource<CompletableObserver>(scope, onMain) {

    override fun subscribe(): Disposable {
        val observer = EmptyCompletableObserver()
        subscribe(observer)
        return observer
    }

    fun subscribe(onComplete: Action): Disposable {
        ObjectHelper.requireNonNull(onComplete, "onComplete is null")
        val observer = CallbackCompletableObserver(onComplete)
        subscribe(observer)
        return observer
    }

    fun subscribe(onComplete: Action, onError: Consumer<in Throwable>): Disposable {
        ObjectHelper.requireNonNull(onError, "onError is null")
        ObjectHelper.requireNonNull(onComplete, "onComplete is null")

        val observer = CallbackCompletableObserver(onError, onComplete)
        subscribe(observer)
        return observer
    }

    override fun subscribe(observer: CompletableObserver) {
        var observer = observer
        ObjectHelper.requireNonNull(observer, "observer is null")
        try {

            observer = RxJavaPlugins.onSubscribe(upStream, observer)

            ObjectHelper.requireNonNull(
                observer,
                "The RxJavaPlugins.onSubscribe hook returned a null CompletableObserver. Please check the handler provided to RxJavaPlugins.setOnCompletableSubscribe for invalid null returns. Further reading: https://github.com/ReactiveX/RxJava/wiki/Plugins"
            )

            subscribeActual(observer)
        } catch (ex: NullPointerException) { // NOPMD
            throw ex
        } catch (ex: Throwable) {
            Exceptions.throwIfFatal(ex)
            RxJavaPlugins.onError(ex)
            val npe = NullPointerException("Actually not, but can't pass out an exception otherwise...")
            npe.initCause(ex)
            throw npe
        }

    }


    private fun subscribeActual(observer: CompletableObserver) {
        var upStream = this.upStream
        if (onMain) {
            upStream = upStream.observeOn(AndroidSchedulers.mainThread())
        }
        upStream.onTerminateDetach().subscribe(LifeCompletableObserver(observer, scope))
    }
}