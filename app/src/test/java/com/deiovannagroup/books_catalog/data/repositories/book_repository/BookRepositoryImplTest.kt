/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.repositories.book_repository

import app.cash.turbine.test
import com.deiovannagroup.books_catalog.data.datasources.book_data_source.BookDataSource
import com.deiovannagroup.books_catalog.domain.models.Book
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var mockBookDataSource: BookDataSource

    private lateinit var bookRepository: BookRepositoryImpl

    private lateinit var book: Book

    @Before
    fun setUp() {
        bookRepository = BookRepositoryImpl(mockBookDataSource)
        book = Book(
            id = 1,
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            year = 1925,
        )
    }

    @Test
    fun `insertBook should call data source's insertBook on data source`() = runTest {
        bookRepository.insertBook(book)

        coVerify(exactly = 1) { mockBookDataSource.insertBook(book) }
    }

    @Test
    fun `updateBook should call data source's updateBook on data source`() = runTest {
        bookRepository.updateBook(book)

        coVerify(exactly = 1) { mockBookDataSource.updateBook(book) }
    }

    @Test
    fun `deleteBook should call data source's deleteBook on data source`() = runTest {
        bookRepository.deleteBook(book)

        coVerify(exactly = 1) { mockBookDataSource.deleteBook(book) }
    }

    @Test
    fun `getBookById should call getBookById on data source and return the book`() = runTest {
        coEvery { mockBookDataSource.getBookById(1) } returns book

        val result = bookRepository.getBookById(1)

        assertThat(result).isEqualTo(book)
        coVerify(exactly = 1) { mockBookDataSource.getBookById(1) }
    }

    @Test
    fun `getAllBooks should call getAllBooks on data source and return flow of books`() = runTest {
        val bookList = listOf(book)
        val bookFlow = flowOf(bookList)

        every { mockBookDataSource.getAllBooks() } returns bookFlow

        val resultFlow = bookRepository.getAllBooks()

        resultFlow.test {
            assertThat(awaitItem()).isEqualTo(bookList)
            awaitComplete()
        }
    }

    @Test
    fun `getBooksByTitle should call getBooksByTitle on data source`() = runTest {
        val title = "The Great Gatsby"
        val bookList = listOf(book)
        val bookFlow = flowOf(bookList)
        every { mockBookDataSource.getBooksByTitle(title) } returns bookFlow

        val resultFlow = bookRepository.getBooksByTitle(title)

        resultFlow.test {
            assertThat(awaitItem()).isEqualTo(bookList)
            awaitComplete()
        }

        coVerify(exactly = 1) { mockBookDataSource.getBooksByTitle(title) }
    }

    @Test
    fun `getBooksByAuthor should call getBooksByAuthor on data source`() = runTest {
        val author = "F. Scott Fitzgerald"
        val bookList = listOf(book)
        val bookFlow = flowOf(bookList)
        every { mockBookDataSource.getBooksByAuthor(author) } returns bookFlow

        val resultFlow = bookRepository.getBooksByAuthor(author)

        resultFlow.test {
            assertThat(awaitItem()).isEqualTo(bookList)
            awaitComplete()
        }
        coVerify(exactly = 1) { mockBookDataSource.getBooksByAuthor(author) }
    }

    @Test
    fun `getBooksByYear should call getBooksByYear on data source`() = runTest {
        val year = 1925
        val bookList = listOf(book)
        val bookFlow = flowOf(bookList)
        every { mockBookDataSource.getBooksByYear(year) } returns bookFlow

        val resultFlow = bookRepository.getBooksByYear(year)

        resultFlow.test {
            assertThat(awaitItem()).isEqualTo(bookList)
            awaitComplete()
        }
        coVerify(exactly = 1) { mockBookDataSource.getBooksByYear(year) }
    }
}