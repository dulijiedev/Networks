package com.dol.rxlifecycle.scopes

import android.os.Build
import android.view.View
import com.dol.rxlifecycle.exceptions.OutsideScopeException
import io.reactivex.disposables.Disposable

/**
 * Created by dlj on 2019/9/10.
 * View作用域
 */
class ViewScope(val view: View, val ignoreAttach: Boolean) : Scope, View.OnAttachStateChangeListener {
    private var disposable: Disposable? = null

    companion object {
        fun from(view: View, ignoreAttach: Boolean): ViewScope {
            return ViewScope(view, ignoreAttach)
        }
    }

    override fun onScopeStart(d: Disposable) {
        disposable = d
        val isAttached =
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && view.isAttachedToWindow) || view.windowToken != null
        if (!isAttached && !ignoreAttach) {
            throw OutsideScopeException("View is not attached!")
        }
        view.addOnAttachStateChangeListener(this)
    }

    override fun onScopeEnd() {
        view.removeOnAttachStateChangeListener(this)
    }

    override fun onViewDetachedFromWindow(v: View?) {
        disposable?.dispose()
        v?.removeOnAttachStateChangeListener(this)
    }

    override fun onViewAttachedToWindow(v: View?) {
    }

}