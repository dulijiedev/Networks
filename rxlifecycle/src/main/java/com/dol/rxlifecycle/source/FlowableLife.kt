package com.dol.rxlifecycle.source

import com.dol.rxlifecycle.lifecycle.LifeSubscriber
import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.Flowable
import io.reactivex.FlowableSubscriber
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.internal.operators.flowable.FlowableInternalHelper
import io.reactivex.internal.subscribers.LambdaSubscriber
import io.reactivex.plugins.RxJavaPlugins
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

/**
 * Created by dlj on 2019/9/16.
 */
class FlowableLife<T>(val upStream: Flowable<T>, scope: Scope, onMain: Boolean) :
    RxSource<FlowableSubscriber<in T>>(scope, onMain) {


    override fun subscribe(): Disposable {
        return subscribe(
            Functions.emptyConsumer(),
            Functions.ON_ERROR_MISSING,
            Functions.EMPTY_ACTION,
            FlowableInternalHelper.RequestMax.INSTANCE
        )
    }

    override fun subscribe(observer: FlowableSubscriber<in T>) {
        ObjectHelper.requireNonNull(observer, "observer is null")
        try {
            val z = RxJavaPlugins.onSubscribe(upStream, observer)
            ObjectHelper.requireNonNull(
                z,
                "The RxJavaPlugins.onSubscribe hook returned a null FlowableSubscriber. Please check the handler provided to RxJavaPlugins.setOnFlowableSubscribe for invalid null returns. Further reading: https://github.com/ReactiveX/RxJava/wiki/Plugins"
            )
            subscribeActual(z)
        }catch (e:NullPointerException){
            throw e
        }catch (e:Throwable){
            Exceptions.throwIfFatal(e)
            // can't call onError because no way to know if a Subscription has been set or not
            // can't call onSubscribe because the call might have set a Subscription already
            RxJavaPlugins.onError(e)
            val npe=NullPointerException("Actually not, but can't throw other exceptions due to RS")
            npe.initCause(e)
            throw npe
        }

    }

    fun subscribe(
        onNext: Consumer<in T>,
        onError: Consumer<in Throwable>,
        onComplete: Action,
        onSubscribe: Consumer<in Subscription>
    ): Disposable {
        ObjectHelper.requireNonNull(onNext, "onNext is null")
        ObjectHelper.requireNonNull(onError, "onError is null")
        ObjectHelper.requireNonNull(onComplete, "onComplete is null")
        ObjectHelper.requireNonNull(onSubscribe, "onSubscribe is null")

        val ls = LambdaSubscriber<T>(onNext, onError, onComplete, onSubscribe)
        subscribe(ls)
        return ls
    }

    fun subscribe(onNext: Consumer<in T>): Disposable {
        return subscribe(
            onNext,
            Functions.ON_ERROR_MISSING,
            Functions.EMPTY_ACTION,
            FlowableInternalHelper.RequestMax.INSTANCE
        )
    }

    fun subscribe(onNext: Consumer<in T>, onError: Consumer<in Throwable>): Disposable {
        return subscribe(onNext, onError, Functions.EMPTY_ACTION, FlowableInternalHelper.RequestMax.INSTANCE)
    }

    fun subscribe(onNext: Consumer<in T>, onError: Consumer<in Throwable>, onComplete: Action): Disposable {
        return subscribe(onNext, onError, onComplete, FlowableInternalHelper.RequestMax.INSTANCE)
    }

    private fun subscribeActual(subscribe: Subscriber<in T>) {
        var upStream = this.upStream
        if (onMain) {
            upStream = upStream.observeOn(AndroidSchedulers.mainThread())
        }
        upStream.onTerminateDetach().subscribe(LifeSubscriber<T>(subscribe, scope))
    }

}