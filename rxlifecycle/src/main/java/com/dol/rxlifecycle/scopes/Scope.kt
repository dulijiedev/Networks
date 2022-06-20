package com.dol.rxlifecycle.scopes

import io.reactivex.disposables.Disposable

/**
 * Created by dlj on 2019/9/10.
 */
interface Scope {
    /**
     * 订阅事件时回调此方法，在 onSubscribe(Disposable d) 方法执行时回调本方法
     * @param d
     */
    fun onScopeStart(d: Disposable)

    /**
     * onError /onComplete 时调用此方法，即时间正常结束时回调
     */
    fun onScopeEnd()


}