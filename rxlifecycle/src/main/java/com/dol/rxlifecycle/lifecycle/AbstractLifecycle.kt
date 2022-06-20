package com.dol.rxlifecycle.lifecycle

import android.os.Looper
import androidx.annotation.MainThread
import com.dol.rxlifecycle.scopes.LifecycleScope
import com.dol.rxlifecycle.scopes.Scope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReference

/**
 * Created by dlj on 2019/9/10.
 */
abstract class AbstractLifecycle<T>(private val scope: Scope) : AtomicReference<T>(), Disposable {

    private val mObject = java.lang.Object()

    private var isAddObserver: Boolean = false

    private val isMainThread: Boolean
        get() = Thread.currentThread() === Looper.getMainLooper().thread

    /**
     * 事件订阅时调用此方法
     */
    @Throws(Exception::class)
    protected fun addObserver() {
        //Lifecycle添加监听器需要在主线程执行
        if (isMainThread || scope !is LifecycleScope) {
            addObserverOnMain()
        } else {
            val `object` = mObject
            AndroidSchedulers.mainThread().scheduleDirect {
                addObserverOnMain()
                synchronized(`object`) {
                    isAddObserver = true
                    `object`.notifyAll()
                }
            }
            synchronized(`object`) {
                while (!isAddObserver) {
                    try {
                        `object`.wait()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    @MainThread
    private fun addObserverOnMain() {
        scope.onScopeStart(this)
    }

    /**
     * onError/onComplete 时调用此方法
     */
    internal fun removeObserver() {
        //Lifecycle移除监听器需要在主线程执行
        if (isMainThread || scope !is LifecycleScope) {
            scope.onScopeEnd()
        } else {
            AndroidSchedulers.mainThread().scheduleDirect { this.removeObserver() }
        }
    }

}
