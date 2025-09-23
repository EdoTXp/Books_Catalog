/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deiovannagroup.books_catalog.data.repositories.book_repository.BookRepository
import com.deiovannagroup.books_catalog.domain.models.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    object Initial : SearchUiState()
    object Loading : SearchUiState()
    object Empty : SearchUiState()
    data class Success(val books: List<Book>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val bookRepository: BookRepository,
) : ViewModel() {


    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val searchState: StateFlow<SearchUiState> = _searchState


    fun searchBookByTitle(title: String) {
        viewModelScope.launch {
            _searchState.value = SearchUiState.Loading
            try {
                val books = bookRepository.getBooksByTitle(title)

                books.collect { books ->
                    delay(1000L) // 1 second delay to wait a loading animation
                    if (books.isEmpty()) {
                        _searchState.value = SearchUiState.Empty
                    } else {
                        _searchState.value = SearchUiState.Success(books)
                    }
                }
            } catch (e: Exception) {
                _searchState.value =
                    SearchUiState.Error("Error searching books by title: ${e.message}")
                delay(2000L)
                resetState()
            }
        }
    }

    fun searchBookByAuthor(author: String) {
        viewModelScope.launch {
            _searchState.value = SearchUiState.Loading
            try {
                val books = bookRepository.getBooksByAuthor(author)

                books.collect { books ->
                    delay(1000L) // 1 second delay to wait a loading animation
                    if (books.isEmpty()) {
                        _searchState.value = SearchUiState.Empty
                    } else {
                        _searchState.value = SearchUiState.Success(books)
                    }
                }
            } catch (e: Exception) {
                _searchState.value =
                    SearchUiState.Error("Error searching books by author: ${e.message}")
                delay(2000L)
                resetState()
            }
        }
    }

    fun searchBookByYear(year: Int) {
        viewModelScope.launch {
            _searchState.value = SearchUiState.Loading
            try {
                val books = bookRepository.getBooksByYear(year)

                books.collect { books ->
                    delay(1000L) // 1 second delay to wait a loading animation
                    if (books.isEmpty()) {
                        _searchState.value = SearchUiState.Empty
                    } else {
                        _searchState.value = SearchUiState.Success(books)
                    }
                }
            } catch (e: Exception) {
                _searchState.value =
                    SearchUiState.Error("Error searching books by year: ${e.message}")
                delay(2000L)
                resetState()
            }
        }
    }

    fun searchAllBooks() {
        viewModelScope.launch {
            _searchState.value = SearchUiState.Loading
            try {
                val books = bookRepository.getAllBooks()

                books.collect { books ->
                    delay(1000L) // 1 second delay to wait a loading animation
                    if (books.isEmpty()) {
                        _searchState.value = SearchUiState.Empty
                    } else {
                        _searchState.value = SearchUiState.Success(books)
                    }
                }
            } catch (e: Exception) {
                _searchState.value =
                    SearchUiState.Error("Error searching all books: ${e.message}")
                delay(2000L)
                resetState()
            }
        }
    }

    fun updateBook(id: Int, title: String, author: String, year: String) {
        viewModelScope.launch {
            val book = Book(
                id = id,
                title = title,
                author = author,
                year = year.toInt(),
            )

            try {
                bookRepository.updateBook(book)
            } catch (e: Exception) {
                _searchState.value =
                    SearchUiState.Error("Error updating book: ${e.message}")
                delay(2000L)
                resetState()
            }
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            try {
                bookRepository.deleteBook(book)
            } catch (e: Exception) {
                _searchState.value =
                    SearchUiState.Error("Error deleting book: ${e.message}")
                delay(2000L)
                resetState()
            }
        }
    }


    private fun resetState() {
        _searchState.value = SearchUiState.Initial
    }
}



