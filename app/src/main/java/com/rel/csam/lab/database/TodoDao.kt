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

package com.rel.csam.lab.database

import androidx.room.*
import io.reactivex.Completable

import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Data Access Object for the users table.
 */
@Dao
interface TodoDao {

    @Query("SELECT * FROM todo LEFT JOIN (SELECT * from tag GROUP BY tag_name) as tag on tag.tag_name = todo.tag AND tag.type = 'group' ORDER BY seq")
    fun getTodoList(): Flowable<List<TodoAndTag>>

    /**
     * Get a user by id.
     *
     * @return the user from the table with a specific id.
     */
    @Query("SELECT * FROM todo WHERE seq = :seq")
    fun getTodoBySeq(seq: Long?): Flowable<Todo>

    /**
     * Insert a todo in the database. If the todo already exists, replace it.

     * @param todo the todo to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodo(todo: Todo): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodoList(todo: List<Todo>): Completable

    @Update
    fun updateTodo(todo: Todo): Completable

    @Delete
    fun deleteTodo(todo: Todo): Completable

    /**
     * Delete all users.
     */
    @Query("DELETE FROM todo")
    fun deleteAllTodo()
}
