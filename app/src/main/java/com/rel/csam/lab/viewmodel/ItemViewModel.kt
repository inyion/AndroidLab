package com.rel.csam.lab.viewmodel

import android.databinding.BaseObservable


abstract class ItemViewModel<ITEM> : BaseObservable() {
    abstract fun setItem(item: ITEM)
}