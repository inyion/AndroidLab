/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rel.csam.lab.viewmodel

import android.content.Context
import com.rel.csam.lab.database.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * View Model for the [UserActivity]
 */
class TodoViewModel : BaseViewModel() {

    private lateinit var todoDao: TodoDao
    lateinit var tagModel: TagModel

    override fun init() {

    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun onStop() {

    }

    fun initDatabase(context: Context) {
        todoDao = AppDatabase.getInstance(context).todoDao()
        tagModel = TagModel()
        tagModel.initDatabase(context)
    }

    fun getTodoList(): Flowable<List<TodoAndTag>> {
        return todoDao.getTodoList()
    }

    fun insertTag(tag: Tag): Completable {
        return tagModel.tagDao.insertTag(tag)
    }

    fun insertTodo(todo: Todo): Completable {
        return todoDao.insertTodo(todo)
    }

    fun insertTodoList(todoList: List<Todo>): Completable {
        return todoDao.insertTodoList(todoList)
    }

    fun deleteTodo(todo: Todo): Completable {
        return todoDao.deleteTodo(todo)
    }

    fun deleteTag(tag: Tag): Completable {
        addDisposable(todoDao.deleteAllTodo(tag.tagName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                }
        )
        return tagModel.deleteTag(tag)
    }

    /**
     * Get the user name of the user.

     * @return a [Flowable] that will emit every time the user name has been updated.
     */
    // for every emission of the user, get the user name
    fun userName(): Flowable<String> {
        return todoDao.getTodoBySeq(TEST_TODO_ID)
                .map { todo -> todo.name }
    }

    /**
     * Update the user name.
     * @param userName the new user name
     * *
     * @return a [Completable] that completes when the user name is updated
     */
    fun updateUserName(name: String): Completable {
        val todo = Todo(TEST_TODO_ID, name, "살것")
        return todoDao.insertTodo(todo)
    }

    companion object {
        // using a hardcoded value for simplicity
        const val TEST_TODO_ID = 1L
    }
}
