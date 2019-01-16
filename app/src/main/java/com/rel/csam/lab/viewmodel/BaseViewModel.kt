package com.rel.csam.lab.viewmodel

import android.databinding.BaseObservable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel: BaseObservable() {

    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun onDispose() {
        compositeDisposable.dispose()
    }

    abstract fun init()

    abstract fun onBackPressed(): Boolean

    abstract fun onStop()
}
