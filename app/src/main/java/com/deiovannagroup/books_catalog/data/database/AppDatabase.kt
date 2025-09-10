/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.deiovannagroup.books_catalog.data.dao.BookDao
import com.deiovannagroup.books_catalog.data.models.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}