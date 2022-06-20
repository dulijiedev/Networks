package com.dol.rxlifecycle.source

import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by dlj on 2019/9/16.
 */
abstract class RxSource<E>(val scope: Scope, val onMain: Boolean) {

    abstract fun subscribe(): Disposable

    /**
     * Subscribes the given Observer to this ObservableSource instance.
     *
     * @param observer the Observer, not null
     * @throws NullPointerException if {@code observer} is null
     */
    abstract fun subscribe(observer: E)

    fun <O : E> subscribeWith(observer: O): O {
        subscribe(observer)
        return observer
    }
}