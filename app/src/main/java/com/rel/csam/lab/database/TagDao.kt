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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable

import io.reactivex.Flowable

/**
 * Data Access Object for the users table.
 */
@Dao
interface TagDao {

    /**
     * Get a user by id.

     * @return the user from the table with a specific id.
     */
    @Query("SELECT * FROM tag WHERE tag_name = :tag")
    fun getTag(tag: String): Flowable<Tag?>

    @Query("SELECT * FROM tag WHERE type = 'group'")
    fun getTagList(): Flowable<List<Tag>>

    /**
     * Insert a todo in the database. If the todo already exists, replace it.

     * @param todo the todo to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTag(tag: Tag): Completable

    /**
     * Delete all users.
     */
    @Query("DELETE FROM tag")
    fun deleteAllTag()
}
