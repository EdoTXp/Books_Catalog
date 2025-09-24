/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.datasources.book_data_source

import com.deiovannagroup.books_catalog.data.dao.BookDao
import com.deiovannagroup.books_catalog.data.mappers.toBook
import com.deiovannagroup.books_catalog.data.mappers.toEntity
import com.deiovannagroup.books_catalog.domain.models.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalBookDataSourceImpl @Inject constructor(
    private val bookDao: BookDao
) : BookDataSource {
    override suspend fun insertBook(book: Book) {
        bookDao.insertBook(book.toEntity())
    }

    override suspend fun updateBook(book: Book) {
        bookDao.updateBook(book.toEntity())
    }

    override suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book.toEntity())
    }

    override suspend fun getBookById(bookId: Int): Book? {
        return bookDao.getBookById(bookId)?.toBook()
    }

    override fun getAllBooks(): Flow<List<Book>> {
        return bookDao.getAllBooks().map { books -> books.map { it.toBook() } }
    }

    override fun getBooksByTitle(title: String): Flow<List<Book>> {
        val jollyTitleSearch = "%$title%"
        return bookDao.getBooksByTitle(jollyTitleSearch).map { books ->
            books.map { it.toBook() }
        }
    }

    override fun getBooksByAuthor(author: String): Flow<List<Book>> {
        val jollyAuthorSearch = "%$author%"
        return bookDao.getBooksByAuthor(jollyAuthorSearch).map { books ->
            books.map { it.toBook() }
        }
    }

    override fun getBooksByYear(year: Int): Flow<List<Book>> {
        val jollyYearSearch = "%$year%"
        return bookDao.getBooksByYear(jollyYearSearch).map { books ->
            books.map { it.toBook() }
        }
    }
}