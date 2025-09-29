/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.ui.viewmodels

import android.util.Log
import app.cash.turbine.test
import com.deiovannagroup.books_catalog.data.repositories.book_repository.BookRepository
import com.deiovannagroup.books_catalog.domain.models.Book
import com.deiovannagroup.books_catalog.utils.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class InsertBookViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var bookRepository: BookRepository
    private lateinit var viewModel: InsertBookViewModel

    @Before
    fun setUp() {
        bookRepository = mockk(relaxed = true)
        viewModel = InsertBookViewModel(bookRepository)

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `initial state is Initial`() = runTest {
        assertThat(viewModel.insertBookState.value).isEqualTo(InsertBookState.Initial)
    }

    @Test
    fun `insertBook - when successful - emits Loading then Success`() = runTest {
        val title = "The Hitchhiker's Guide"
        val author = "Douglas Adams"
        val year = "1979"
        val expectedBook = Book(id = 0, title = title, author = author, year = year.toInt())

        viewModel.insertBookState.test {
            assertThat(awaitItem()).isEqualTo(InsertBookState.Initial)

            viewModel.insertBook(title, author, year)

            assertThat(awaitItem()).isEqualTo(InsertBookState.Loading)
            assertThat(awaitItem()).isEqualTo(InsertBookState.Success)

            coVerify(exactly = 1) { bookRepository.insertBook(expectedBook) }
        }
    }

    @Test
    fun `insertBook - when repository throws exception - emits Loading then Error`() = runTest {
        val errorMessage = "Database error"
        coEvery { bookRepository.insertBook(any()) } throws RuntimeException(errorMessage)

        viewModel.insertBookState.test {
            assertThat(awaitItem()).isEqualTo(InsertBookState.Initial)

            viewModel.insertBook("Title", "Author", "2023")

            assertThat(awaitItem()).isEqualTo(InsertBookState.Loading)
            assertThat(awaitItem()).isEqualTo(InsertBookState.Error)
        }
    }

    @Test
    fun `insertBook - with invalid year format - emits Loading then Error`() = runTest {
        val invalidYear = "not-a-year"

        viewModel.insertBookState.test {
            assertThat(awaitItem()).isEqualTo(InsertBookState.Initial)

            viewModel.insertBook("Title", "Author", invalidYear)

            assertThat(awaitItem()).isEqualTo(InsertBookState.Loading)
            assertThat(awaitItem()).isEqualTo(InsertBookState.Error)

            coVerify(exactly = 0) { bookRepository.insertBook(any()) }
        }
    }

    @Test
    fun `resetState - sets state to Initial`() = runTest {
        viewModel.insertBookState.test {
            assertThat(awaitItem()).isEqualTo(InsertBookState.Initial)

            viewModel.insertBook("Title", "Author", "2023")
            awaitItem()
            awaitItem()


            viewModel.resetState()

            assertThat(awaitItem()).isEqualTo(InsertBookState.Initial)
        }
    }
}
