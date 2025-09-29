/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.ui.viewmodels

import app.cash.turbine.test
import com.deiovannagroup.books_catalog.data.repositories.book_repository.BookRepository
import com.deiovannagroup.books_catalog.domain.models.Book
import com.deiovannagroup.books_catalog.utils.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class SearchViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var bookRepository: BookRepository
    private lateinit var viewModel: SearchViewModel
    private lateinit var testBook1: Book
    private lateinit var testBook2: Book
    private lateinit var sampleBookList: List<Book>

    @Before
    fun setUp() {
        testBook1 = Book(
            id = 1,
            title = "The Lord of the Rings",
            author = "J.R.R. Tolkien",
            year = 1954,
        )
        testBook2 = Book(
            id = 2,
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            year = 1937,
        )
        sampleBookList = listOf(testBook1, testBook2)

        bookRepository = mockk()
        viewModel = SearchViewModel(bookRepository)
    }

    @Test
    fun `initial state is Initial`() = runTest {
        assertThat(viewModel.searchState.value).isEqualTo(SearchUiState.Initial)
    }

    @Test
    fun `searchAllBooks when repository returns books emits Loading then Success`() = runTest {

        coEvery { bookRepository.getAllBooks() } returns flowOf(sampleBookList)

        viewModel.searchState.test {
            skipItems(1)

            viewModel.searchAllBooks()

            assertThat(awaitItem()).isEqualTo(SearchUiState.Loading)
            assertThat(awaitItem()).isEqualTo(SearchUiState.Success(sampleBookList))

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `searchAllBooks when repository returns empty list emits Loading then Empty`() = runTest {
        coEvery { bookRepository.getAllBooks() } returns flowOf(emptyList())

        viewModel.searchState.test {
            skipItems(1)
            viewModel.searchAllBooks()

            assertThat(awaitItem()).isEqualTo(SearchUiState.Loading)
            assertThat(awaitItem()).isEqualTo(SearchUiState.Empty)
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `searchBookByTitle when repository throws exception emits Loading then Error then Initial`() =
        runTest {

            val errorMessage = "Database error"
            coEvery { bookRepository.getBooksByTitle(any()) } returns flow {
                throw IOException(
                    errorMessage
                )
            }

            viewModel.searchState.test {
                skipItems(1)
                viewModel.searchBookByTitle("any title")

                assertThat(awaitItem()).isEqualTo(SearchUiState.Loading)

                val errorState = awaitItem() as SearchUiState.Error
                assertThat(errorState.message).contains(errorMessage)

                assertThat(awaitItem()).isEqualTo(SearchUiState.Initial)
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `updateBook calls repository and refreshes by calling searchAllBooks`() = runTest {
        coEvery { bookRepository.updateBook(any()) } returns Unit
        coEvery { bookRepository.getAllBooks() } returns flowOf(sampleBookList)

        viewModel.updateBook(
            id = testBook1.id,
            title = testBook1.title,
            author = testBook1.author,
            year = testBook1.year.toString()
        )

        advanceUntilIdle()

        coVerify(exactly = 1) { bookRepository.updateBook(testBook1) }
        coVerify(exactly = 1) { bookRepository.getAllBooks() }
    }

    @Test
    fun `confirmDeleteBook calls repository and refreshes list`() = runTest {
        coEvery { bookRepository.deleteBook(any()) } returns Unit
        coEvery { bookRepository.getAllBooks() } returns flowOf(listOf(testBook2))

        viewModel.setBookPendingDeletion(testBook1)
        viewModel.confirmDeleteBook()

        advanceUntilIdle()
        
        coVerify(exactly = 1) { bookRepository.deleteBook(testBook1) }
        coVerify(exactly = 1) { bookRepository.getAllBooks() }

        viewModel.searchState.test {

            val finalState = expectMostRecentItem()
            if (finalState is SearchUiState.Success) {
                assertThat(finalState.books).isEqualTo(listOf(testBook2))
            }
        }
    }

}