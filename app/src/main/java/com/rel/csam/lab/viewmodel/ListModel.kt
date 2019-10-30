package com.rel.csam.lab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rel.csam.lab.view.SectionItemDeco

abstract class ListModel<T> : BaseViewModel() {
    private val _items = MutableLiveData<ArrayList<T>>()
    val items: LiveData<ArrayList<T>> = _items

    private val _preItemAddCount = MutableLiveData<Int>()
    val preItemAddCount: LiveData<Int> = _preItemAddCount
    private val _scrollPosition = MutableLiveData<Int>()
    val scrollPosition: LiveData<Int> = _scrollPosition
    private val _notifyChange = MutableLiveData<Int>()
    val notifyChange: LiveData<Int> = _notifyChange
    private val _notifyItemChange = MutableLiveData<Int>()
    val notifyItemChange: LiveData<Int> = _notifyItemChange
    private val _notifyItemInserted = MutableLiveData<Int>()
    val notifyItemInserted: LiveData<Int> = _notifyItemInserted
    private val _notifyItemRangeInserted = MutableLiveData<Int>()
    val notifyItemRangeInserted: LiveData<Int> = _notifyItemRangeInserted


    var isLastPosition = true
//    var invokeOnResume: ObservableInt = ObservableInt()

    init {
        _items.value = ArrayList()
    }

    fun setItemList(itemList: ArrayList<T>) {
        this._items.value = itemList
    }

    fun getItemList(): ArrayList<T>? {
        return if (_items.value != null) {
            this._items.value
        } else {
            ArrayList()
        }
    }

    fun getItem(position: Int): T {
        return _items.value!![position]
    }

    fun getItemCount(): Int {
        return if (_items.value != null) {
            _items.value!!.size
        } else {
            0
        }
    }

    fun addItem(item: T): ArrayList<T> {
        _items.value?.add(item)
        return _items.value!!
    }

    fun addItem(itemList: ArrayList<T>?): ArrayList<T> {
        if (itemList != null) {
            _items.value?.addAll(itemList)
        }
        return _items.value!!
    }

    fun addItem(index: Int, item: T): ArrayList<T> {
        if (item != null) {
            _items.value?.add(index, item)
        }

        return _items.value!!
    }

    fun addItem(index: Int, itemList: ArrayList<T>?): ArrayList<T> {
        if (itemList != null) {
            _items.value?.addAll(index, itemList)
        }

        if (index == 0) {
            _preItemAddCount.value = itemList?.size
        }

        return _items.value!!
    }

    fun checkItemExist(): Boolean {
        return if (_items.value != null) {
            _items.value!!.size > 0
        } else {
            false
        }
    }

    fun setItem(index: Int, item: T) {
        _items.value!![index] = item
    }

    fun removeItem(item: T) {
        _items.value!!.remove(item)
    }

    fun removeItem(index: Int) {
        _items.value!!.removeAt(index)
    }

    fun getLastItem(): T {
        return if (_items.value!!.size > 0) {
            getItem(_items.value!!.size - 1)
        } else {
            getItem(0)
        }
    }

    fun scrollPosition(position: Int) {
        _scrollPosition.postValue(position)
    }

//    fun inVokeNotifyChange() {
//        _notifyChange.postValue(invokeOnResume.get())
//    }

    fun notifyChange() {
        _notifyChange.postValue(1)
    }

    fun notifyItemChange(position: Int) {
        _notifyItemChange.postValue(position)
    }

    fun notifyItemInserted(position: Int) {
        _notifyItemInserted.postValue(position)
    }

    fun notifyItemRangeInserted(position: Int) {
        _notifyItemRangeInserted.postValue(position)
    }

    open fun getSectionCallBack(): SectionItemDeco.SectionCallback? {
        return null
    }

}