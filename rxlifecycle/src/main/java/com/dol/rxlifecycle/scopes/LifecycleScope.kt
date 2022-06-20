package com.dol.rxlifecycle.scopes

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable

/**
 * Created by dlj on 2019/9/10.
 */
internal class LifecycleScope(private val lifecycle: Lifecycle, private val event: Lifecycle.Event) : Scope,
    LifecycleEventObserver {

    private var disposable: Disposable? = null

    companion object {
        fun from(lifecycleOwner: LifecycleOwner, event: Lifecycle.Event): LifecycleScope {
            return LifecycleScope(lifecycleOwner.lifecycle, event)
        }
    }

    override fun onScopeStart(d: Disposable) {
        this.disposable = d
        onScopeEnd()
        lifecycle.addObserver(this)
    }

    override fun onScopeEnd() {
        lifecycle.removeObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == this.event) {
            disposable?.dispose()
            source.lifecycle.removeObserver(this)
        }
    }

}