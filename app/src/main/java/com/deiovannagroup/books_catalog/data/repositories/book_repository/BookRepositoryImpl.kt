/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.repositories.book_repository

import com.deiovannagroup.books_catalog.data.datasources.BookDataSource.BookDataSource
import com.deiovannagroup.books_catalog.domain.models.Book
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class BookRepositoryImpl @Inject constructor(
    private val bookDataSource: BookDataSource
) : BookRepository {
    override suspend fun insertBook(book: Book) {
        bookDataSource.insertBook(book)
    }

    override suspend fun updateBook(book: Book) {
        bookDataSource.updateBook(book)
    }

    override suspend fun deleteBook(book: Book) {
        bookDataSource.deleteBook(book)
    }

    override suspend fun getBookById(id: Int): Book? {
        return bookDataSource.getBookById(id)
    }

    override fun getAllBooks(): Flow<List<Book>> {
        return bookDataSource.getAllBooks()
    }

    override fun getBooksByTitle(title: String): Flow<List<Book>> {
        return bookDataSource.getBooksByTitle(title)
    }

    override fun getBooksByAuthor(author: String): Flow<List<Book>> {
        return bookDataSource.getBooksByAuthor(author)
    }

    override fun getBooksByYear(year: Int): Flow<List<Book>> {
        return bookDataSource.getBooksByYear(year)
    }
}