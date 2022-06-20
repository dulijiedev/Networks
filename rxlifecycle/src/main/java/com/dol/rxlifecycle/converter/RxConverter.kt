package com.dol.rxlifecycle.converter

import com.dol.rxlifecycle.source.*
import io.reactivex.*
import io.reactivex.parallel.ParallelFlowableConverter

/**
 * Created by dlj on 2019/9/20.
 */
public interface RxConverter<T> : ObservableConverter<T, ObservableLife<T>>,
    FlowableConverter<T, FlowableLife<T>>,
    ParallelFlowableConverter<T, ParallelFlowableLife<T>>,
    MaybeConverter<T, MaybeLife<T>>,
    SingleConverter<T, SingleLife<T>>,
    CompletableConverter<CompletableLife>