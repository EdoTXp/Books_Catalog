/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.repositories

import android.content.ContentValues
import com.deiovannagroup.books_catalog.helpers.DatabaseHelper
import javax.inject.Inject


class BookRepositoryImpl @Inject constructor(
    private val databaseHelper: DatabaseHelper
) : BookRepository {
    private val cv = ContentValues()

    override fun searchByTitle(title: String): List<ContentValues> {
        return databaseHelper.searchByTitle(title)
    }

    override fun searchByAuthor(author: String): List<ContentValues> {
        return databaseHelper.searchByAuthor(author)
    }

    override fun searchByYear(year: Int): List<ContentValues> {
        return databaseHelper.searchByYear(year)
    }

    override fun searchAll(): List<ContentValues> {
        return databaseHelper.searchAll()
    }

    override fun tableExist(): Boolean {
        return databaseHelper.tableExist()
    }


    override fun insert(title: String, author: String, year: String): Long {
        cv.put("titulo", title)
        cv.put("autor", author)
        cv.put("ano", year)
        return databaseHelper.insert(cv)
    }

    override fun update(id: Int, updateValue: ContentValues?) {
        databaseHelper.update(id, updateValue)
    }

    override fun delete(id: Int) {
        databaseHelper.delete(id)
    }

}