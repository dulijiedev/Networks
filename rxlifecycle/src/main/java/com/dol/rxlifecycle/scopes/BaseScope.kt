package com.dol.rxlifecycle.scopes

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by dlj on 2019/9/10.
 */
class BaseScope(owner: LifecycleOwner) : Scope, LifecycleEventObserver {

    private var mDisposables: CompositeDisposable? = null

    init {
        owner.lifecycle.addObserver(this)
    }

    override fun onScopeStart(d: Disposable) {
        addDisposable(d)
    }

    override fun onScopeEnd() {
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            source.lifecycle.removeObserver(this)
            dispose()
        }
    }

    private fun addDisposable(disposable: Disposable) {
        if (mDisposables == null) {
            mDisposables = CompositeDisposable()
        }
        mDisposables?.add(disposable)
    }

    private fun dispose() {
        if (mDisposables == null) {
            return
        }
        mDisposables?.dispose()
    }
}