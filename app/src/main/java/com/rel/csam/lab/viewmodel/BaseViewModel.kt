package com.rel.csam.lab.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel: ViewModel() {

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
