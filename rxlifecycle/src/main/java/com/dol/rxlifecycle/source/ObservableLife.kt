package com.dol.rxlifecycle.source

import com.dol.rxlifecycle.lifecycle.LifeObserver
import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.internal.observers.LambdaObserver
import io.reactivex.plugins.RxJavaPlugins
import java.lang.NullPointerException

/**
 * Created by dlj on 2019/9/20.
 */
class ObservableLife<T>(val upStream: Observable<T>, scope: Scope, onMain: Boolean) :
    RxSource<Observer<in T>>(scope, onMain) {

    override fun subscribe(): Disposable {
        return subscribe(Functions.emptyConsumer<T>())
    }

    fun subscribe(
        onNext: Consumer<in T>,
        onError: Consumer<in Throwable> = Functions.ON_ERROR_MISSING,
        onComplete: Action = Functions.EMPTY_ACTION,
        onSubscribe: Consumer<in Disposable> = Functions.emptyConsumer()
    ): Disposable {
        ObjectHelper.requireNonNull(onNext, "onNext is null")
        ObjectHelper.requireNonNull(onError, "onError is null")
        ObjectHelper.requireNonNull(onComplete, "onComplete is null")
        ObjectHelper.requireNonNull(onSubscribe, "onSubscribe is null")
        val ls = LambdaObserver<T>(onNext, onError, onComplete, onSubscribe)
        subscribe(ls)
        return ls
    }

    override fun subscribe(observer: Observer<in T>) {
        ObjectHelper.requireNonNull(observer, "observer is null")
        try {
            val observer1 = RxJavaPlugins.onSubscribe(upStream, observer)
            ObjectHelper.requireNonNull(
                observer1,
                "The RxJavaPlugins.onSubscribe hook returned a null Observer. Please change the handler provided to RxJavaPlugins.setOnObservableSubscribe for invalid null returns. Further reading: https://github.com/ReactiveX/RxJava/wiki/Plugins"
            )
            subscribeActual(observer1)
        } catch (e: NullPointerException) {
            throw e
        } catch (e: Throwable) {
            Exceptions.throwIfFatal(e)
            RxJavaPlugins.onError(e)
            val npe = NullPointerException("Actually not ,but can't throw other exceptions due to RS")
            npe.initCause(e)
            throw npe
        }
    }

    private fun subscribeActual(observer: Observer<in T>) {
        var upStream = this.upStream
        if (onMain) {
            upStream = upStream.observeOn(AndroidSchedulers.mainThread())
        }
        upStream.onTerminateDetach().subscribe(LifeObserver<T>(observer, scope))
    }

}