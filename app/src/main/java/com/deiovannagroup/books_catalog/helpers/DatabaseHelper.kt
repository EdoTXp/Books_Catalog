/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.helpers

import android.content.ContentValues

interface DatabaseHelper {
    fun insert(cv: ContentValues?): Long

    fun update(id: Int, updateValue: ContentValues?)

    fun delete(id: Int)

    fun searchByTitle(title: String): List<ContentValues>

    fun searchByAuthor(author: String): List<ContentValues>

    fun searchByYear(year: Int): List<ContentValues>

    fun tableExist(): Boolean

    fun searchAll(): List<ContentValues>
}
