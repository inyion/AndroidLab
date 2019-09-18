package com.rel.csam.lab.viewmodel

import android.content.Context
import com.rel.csam.lab.database.AppDatabase
import com.rel.csam.lab.database.Tag
import com.rel.csam.lab.database.TagDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TagModel: ListModel<Tag>() {

    lateinit var tagDao: TagDao
    val selectTagList: MutableList<Tag> = ArrayList()

    override fun init() {

        addDisposable(tagDao.getTagList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    setItemList(list as ArrayList<Tag>)
                })
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun onStop() {

    }

    fun getTagName(position: Int): String {
        return getItem(position).tagName
    }

    fun selectTag(position: Int, isSelected: Boolean) {
        val tag = getItem(position)
        if (isSelected) {
            selectTagList.add(tag)
        } else {
            selectTagList.remove(tag)
        }
    }

    fun initDatabase(context: Context) {
        tagDao = AppDatabase.getInstance(context).tagDao()
    }

}