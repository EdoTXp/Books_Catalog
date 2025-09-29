/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.datasources.book_data_source

import app.cash.turbine.test
import com.deiovannagroup.books_catalog.data.dao.BookDao
import com.deiovannagroup.books_catalog.data.mappers.toBook
import com.deiovannagroup.books_catalog.data.mappers.toEntity
import com.deiovannagroup.books_catalog.data.models.BookEntity
import com.deiovannagroup.books_catalog.domain.models.Book
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LocalBookDataSourceImplTest {

    private lateinit var bookDao: BookDao
    private lateinit var dataSource: BookDataSource

    private lateinit var book: Book
    private lateinit var bookEntity: BookEntity

    @Before
    fun setUp() {
        bookDao = mockk(relaxed = true)
        dataSource = LocalBookDataSourceImpl(bookDao)

        book = Book(
            id = 1,
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            year = 1925,
        )

        bookEntity = book.toEntity()
    }

    @Test
    fun `insertBook should call dao with mapped entity`() = runTest {
        dataSource.insertBook(book)
        coVerify(exactly = 1) {
            bookDao.insertBook(bookEntity)
        }
    }

    @Test
    fun `updateBook should call dao with mapped entity`() = runTest {
        dataSource.updateBook(book)
        coVerify(exactly = 1) {
            bookDao.updateBook(bookEntity)
        }
    }

    @Test
    fun `deleteBook should call dao with mapped entity`() = runTest {
        dataSource.deleteBook(book)
        coVerify(exactly = 1) {
            bookDao.deleteBook(bookEntity)
        }
    }

    @Test
    fun `getBookById should call dao with correct id`() = runTest {
        val bookId = 1
        val bookEntity =
            BookEntity(id = bookId, title = "1984", author = "George Orwell", year = 1949)
        coEvery { bookDao.getBookById(bookId) } returns bookEntity

        val result = dataSource.getBookById(bookId)

        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(bookEntity.toBook())
    }

    @Test
    fun `getBookById when book does not exist returns null`() = runTest {
        val bookId = 99
        coEvery { bookDao.getBookById(bookId) } returns null

        val result = dataSource.getBookById(bookId)

        assertThat(result).isNull()
    }

    @Test
    fun `getAllBooks return flow of mapped books`() = runTest {
        val bookEntities = listOf(
            BookEntity(1, "Book A", "Author A", 2000),
            BookEntity(2, "Book B", "Author B", 2001)
        )

        val expectedBooks = bookEntities.map { it.toBook() }
        every { bookDao.getAllBooks() } returns flowOf(bookEntities)

        dataSource.getAllBooks().test {
            val items = awaitItem()
            assertThat(items).isEqualTo(expectedBooks)
            awaitComplete()
        }
    }

    @Test
    fun `getBooksByTitle calls dao with wildcard and returns mapped flow`() = runTest {
        val titleQuery = "Kotlin"
        val expectedSearchTerm = "%Kotlin%"
        val entities = listOf(
            BookEntity(
                1,
                "Kotlin in Action",
                "Dmitry Jemerov",
                2017,
            )
        )
        val expectedBooks = entities.map { it.toBook() }

        every { bookDao.getBooksByTitle(expectedSearchTerm) } returns flowOf(entities)

        dataSource.getBooksByTitle(titleQuery).test {
            assertThat(awaitItem()).isEqualTo(expectedBooks)
            awaitComplete()
        }

        coVerify { bookDao.getBooksByTitle(expectedSearchTerm) }
    }

    @Test
    fun `getBooksByAuthor calls dao with wildcard and returns mapped flow`() = runTest {
        val authorQuery = "Martin"
        val expectedSearchTerm = "%Martin%"
        val entities = listOf(
            BookEntity(
                1,
                "Clean Code",
                "Robert C. Martin",
                2008,
            )
        )
        val expectedBooks = entities.map { it.toBook() }
        every { bookDao.getBooksByAuthor(expectedSearchTerm) } returns flowOf(entities)

        dataSource.getBooksByAuthor(authorQuery).test {
            assertThat(awaitItem()).isEqualTo(expectedBooks)
            awaitComplete()
        }
    }

    @Test
    fun `getBooksByYear calls dao with wildcard and returns mapped flow`() = runTest {
        val yearQuery = 2008
        val expectedSearchTerm = "%2008%"
        val entities = listOf(
            BookEntity(
                1,
                "Clean Code",
                "Robert C. Martin",
                2008,
            )
        )
        val expectedBooks = entities.map { it.toBook() }
        every { bookDao.getBooksByYear(expectedSearchTerm) } returns flowOf(entities)

        dataSource.getBooksByYear(yearQuery).test {
            assertThat(awaitItem()).isEqualTo(expectedBooks)
            awaitComplete()
        }
    }

}