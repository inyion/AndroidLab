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

package com.rel.csam.lab

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import com.rel.csam.lab.database.Todo
import com.rel.csam.lab.database.AppDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test the implementation of [UserDao]
 */
@RunWith(AndroidJUnit4::class)
class TodoDaoTest {

    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Before fun initDb() {
        // using an in-memory database because the information stored here disappears after test
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build()
    }

    @After fun closeDb() {
        database.close()
    }

    @Test fun getUsersWhenNoUserInserted() {
        database.todoDao().getTodoBySeq(1)
                .test()
                .assertNoValues()
    }

    @Test fun insertAndGetUser() {
        // When inserting a new user in the data source
        database.todoDao().insertTodo(TODO).blockingAwait()

        // When subscribing to the emissions of the user
        database.todoDao().getTodoBySeq(TODO.seq)
                .test()
                // assertValue asserts that there was only one emission of the user
                .assertValue { it.seq == TODO.seq && it.name == TODO.name }
    }

    @Test fun updateAndGetUser() {
        // Given that we have a user in the data source
        database.todoDao().insertTodo(TODO).blockingAwait()

        // When we are updating the name of the user
        val updatedUser = Todo(TODO.seq, "new username", "살것")
        database.todoDao().insertTodo(updatedUser).blockingAwait()

        // When subscribing to the emissions of the user
        database.todoDao().getTodoBySeq(TODO.seq)
                .test()
                // assertValue asserts that there was only one emission of the user
                .assertValue { it.seq == TODO.seq && it.name == "new username" }
    }

    @Test fun deleteAndGetUser() {
        // Given that we have a user in the data source
        database.todoDao().insertTodo(TODO).blockingAwait()

        //When we are deleting all users
        database.todoDao().deleteAllTodo()
        // When subscribing to the emissions of the user
        database.todoDao().getTodoBySeq(TODO.seq)
                .test()
                // check that there's no user emitted
                .assertNoValues()
    }

    companion object {
        private val TODO = Todo(1L, "야채", "살것")
    }
}
