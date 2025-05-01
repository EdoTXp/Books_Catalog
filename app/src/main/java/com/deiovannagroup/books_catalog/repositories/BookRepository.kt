/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.repositories

import android.content.ContentValues

interface BookRepository {
    fun searchByTitle(title: String): List<ContentValues>

    fun searchByAuthor(author: String): List<ContentValues>

    fun searchByYear(year: Int): List<ContentValues>

    fun searchAll(): List<ContentValues>

    fun tableExist(): Boolean

    fun insert(title: String, author: String, year: String): Long

    fun update(id: Int, updateValue: ContentValues?)

    fun delete(id: Int)
}