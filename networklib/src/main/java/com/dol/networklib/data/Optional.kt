package com.dol.networklib.data

import androidx.annotation.Nullable
import java.util.*

class Optional<M>(
    @param:Nullable // 获取可以为null的返回结果
    val includeNull: M? // 接收到的返回结果
) {
    // 判断返回结果是否为null
    val isEmpty: Boolean
        get() = this.includeNull == null

    // 获取不能为null的返回结果，如果为null，直接抛异常，经过二次封装之后，这个异常最终可以在走向RxJava的onError()
    fun get(): M {
        if (includeNull == null) {
            throw NoSuchElementException("No value present")
        }
        return includeNull
    }
}

