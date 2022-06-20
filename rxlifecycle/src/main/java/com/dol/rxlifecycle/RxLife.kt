package com.dol.rxlifecycle

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.dol.rxlifecycle.converter.RxConverter
import com.dol.rxlifecycle.scopes.LifecycleScope
import com.dol.rxlifecycle.scopes.ViewScope
import com.dol.rxlifecycle.scopes.Scope
import com.dol.rxlifecycle.source.*
import io.reactivex.*
import io.reactivex.parallel.ParallelFlowable

/**
 * Created by dlj on 2019/9/20.
 */
object RxLife {

    fun <T> `as`(scope: Scope, onMain: Boolean): RxConverter<T> {
        return object : RxConverter<T> {
            override fun apply(upstream: Observable<T>): ObservableLife<T> {
                return ObservableLife(upstream, scope, onMain)
            }

            override fun apply(upstream: Flowable<T>): FlowableLife<T> {
                return FlowableLife(upstream, scope, onMain)
            }

            override fun apply(upstream: ParallelFlowable<T>): ParallelFlowableLife<T> {
                return ParallelFlowableLife(upstream, scope, onMain)
            }

            override fun apply(upstream: Maybe<T>): MaybeLife<T> {
                return MaybeLife(upstream, scope, onMain)
            }

            override fun apply(upstream: Single<T>): SingleLife<T> {
                return SingleLife(upstream, scope, onMain)
            }

            override fun apply(upstream: Completable): CompletableLife {
                return CompletableLife(upstream, scope, onMain)
            }

        }
    }

    fun <T> `as`(
        owner: LifecycleOwner,
        event: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
        onMain: Boolean = false
    ): RxConverter<T> {
        return `as`(LifecycleScope.from(owner, event), onMain)
    }

    fun <T> asOnMain(owner: LifecycleOwner): RxConverter<T> {
        return `as`(owner, Lifecycle.Event.ON_DESTROY, true)
    }

    fun <T> asOnMain(owner: LifecycleOwner, event: Lifecycle.Event): RxConverter<T> {
        return `as`(owner, event, true)
    }

    fun <T> `as`(view: View): RxConverter<T> {
        return `as`(ViewScope.from(view, false), false)
    }

    /**
     * @param view         目标View
     * @param ignoreAttach 忽略View是否添加到Window，默认为false，即不忽略
     * @return RxConverter
     */
    fun <T> `as`(view: View, ignoreAttach: Boolean): RxConverter<T> {
        return `as`(ViewScope.from(view, ignoreAttach), false)
    }

    fun <T> asOnMain(view: View): RxConverter<T> {
        return `as`(ViewScope.from(view, false), true)
    }

    /**
     * @param view         目标View
     * @param ignoreAttach 忽略View是否添加到Window，默认为false，即不忽略
     * @return RxConverter
     */
    fun <T> asOnMain(view: View, ignoreAttach: Boolean): RxConverter<T> {
        return `as`(ViewScope.from(view, ignoreAttach), true)
    }

    fun <T> `as`(scope: Scope): RxConverter<T> {
        return `as`(scope, false)
    }

    fun <T> asOnMain(scope: Scope): RxConverter<T> {
        return `as`(scope, true)
    }
}