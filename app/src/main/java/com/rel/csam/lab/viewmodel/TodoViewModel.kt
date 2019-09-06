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

import com.rel.csam.lab.database.TagDao
import com.rel.csam.lab.database.Todo
import com.rel.csam.lab.database.TodoDao
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * View Model for the [UserActivity]
 */
class TodoViewModel : BaseViewModel() {

    lateinit var todoDao: TodoDao
    lateinit var tagDao: TagDao

    override fun init() {

    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun onStop() {

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
