/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.datasources.BookDataSource

import com.deiovannagroup.books_catalog.domain.models.Book
import kotlinx.coroutines.flow.Flow

interface BookDataSource {
    suspend fun insertBook(book: Book)
    suspend fun updateBook(book: Book)
    suspend fun deleteBook(book: Book)
    suspend fun getBookById(bookId: Int): Book?
    fun getAllBooks(): Flow<List<Book>>
    fun getBooksByTitle(title: String): Flow<List<Book>>
    fun getBooksByAuthor(author: String): Flow<List<Book>>
    fun getBooksByYear(year: Int): Flow<List<Book>>
}