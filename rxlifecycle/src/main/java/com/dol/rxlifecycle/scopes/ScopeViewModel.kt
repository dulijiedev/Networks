package com.dol.rxlifecycle.scopes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by dlj on 2019/9/10.
 */
open class ScopeViewModel(application: Application) : AndroidViewModel(application), Scope {

    private var mDisposable: CompositeDisposable? = null

    override fun onScopeStart(d: Disposable) {
        //订阅事件时回调
        addDisposable(d)
    }

    override fun onScopeEnd() {
        //事件正常结束时回调
    }

    override fun onCleared() {
        super.onCleared()
        disposable()
    }

    private fun addDisposable(disposable: Disposable) {
        if (mDisposable == null) {
            mDisposable = CompositeDisposable()
        }
        mDisposable?.add(disposable)
    }

    private fun disposable() {
        if (mDisposable == null) {
            return
        }
        mDisposable?.dispose()
    }

    /**
     * cancel request
     */
    fun destroyScope() {
        onCleared()
    }
}