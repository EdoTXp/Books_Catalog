/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.deiovannagroup.books_catalog.data.database.AppDatabase
import com.deiovannagroup.books_catalog.data.models.BookEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class BookDaoTest {

    private lateinit var bookDao: BookDao
    private lateinit var db: AppDatabase

    private val book1 = BookEntity(
        id = 1,
        title = "The Hitchhiker's Guide to the Galaxy",
        author = "Douglas Adams",
        year = 1979
    )
    private val book2 = BookEntity(
        id = 2,
        title = "Dune",
        author = "Frank Herbert",
        year = 1965,
    )
    private val book3 = BookEntity(
        id = 3,
        title = "The Lord of the Rings",
        author = "J.R.R. Tolkien",
        year = 1954,
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        bookDao = db.bookDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertBook_and_getBookById_retrievesCorrectBook() = runTest {
        bookDao.insertBook(book1)
        val retrievedBook = bookDao.getBookById(book1.id)

        assertThat(retrievedBook).isNotNull()
        assertThat(retrievedBook?.id).isEqualTo(book1.id)
        assertThat(retrievedBook?.title).isEqualTo(book1.title)
    }

    @Test
    fun insertBook_withConflict_replacesExistingBook() = runTest {
        bookDao.insertBook(book1)
        val updatedBook = book1.copy(title = "The Ultimate Hitchhiker's Guide")

        bookDao.insertBook(updatedBook)
        val retrievedBook = bookDao.getBookById(book1.id)

        assertThat(retrievedBook?.title).isEqualTo("The Ultimate Hitchhiker's Guide")
    }

    @Test
    fun updateBook_persistsChanges() = runTest {
        bookDao.insertBook(book1)
        val updatedBook = book1.copy(author = "Franklin Herbert")

        bookDao.updateBook(updatedBook)
        val retrievedBook = bookDao.getBookById(book1.id)

        assertThat(retrievedBook?.author).isEqualTo("Franklin Herbert")
    }

    @Test
    fun deleteBook_removesItFromDatabase() = runTest {
        bookDao.insertBook(book1)
        assertThat(bookDao.getBookById(book1.id)).isNotNull()

        bookDao.deleteBook(book1)
        val retrievedBook = bookDao.getBookById(book1.id)

        assertThat(retrievedBook).isNull()
    }

    @Test
    fun getAllBooks_returnsAllInsertedBooks() = runTest {
        bookDao.insertBook(book1)
        bookDao.insertBook(book2)

        bookDao.getAllBooks().test {
            val bookList = awaitItem()
            assertThat(bookList).hasSize(2)
            assertThat(bookList).containsExactly(book1, book2)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getBooksByTitle_withPartialMatch_returnsCorrectBooks() = runTest {
        // Arrange
        bookDao.insertBook(book1)
        bookDao.insertBook(book3)

        // Act & Assert using Turbine
        // The '%' are wildcards for the LIKE query
        bookDao.getBooksByTitle("%the%").test {
            val bookList = awaitItem()
            assertThat(bookList).hasSize(2)
            assertThat(bookList).containsExactly(book1, book3)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getBooksByAuthor_returnsCorrectBooks() = runTest {
        // Arrange
        bookDao.insertBook(book2)
        bookDao.insertBook(book3)

        // Act & Assert
        bookDao.getBooksByAuthor("%Herbert%").test {
            val bookList = awaitItem()
            assertThat(bookList).hasSize(1)
            assertThat(bookList.first()).isEqualTo(book2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}