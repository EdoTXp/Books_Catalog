/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deiovannagroup.books_catalog.data.repositories.book_repository.BookRepository
import com.deiovannagroup.books_catalog.domain.models.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class InsertBookState {
    object Idle : InsertBookState()
    object Loading : InsertBookState()
    object Success : InsertBookState()
    object Error : InsertBookState()
}

@HiltViewModel
class InsertBookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _insertBookState = MutableStateFlow<InsertBookState>(InsertBookState.Idle)
    val insertBookState: StateFlow<InsertBookState> = _insertBookState

    fun insertBook(title: String, author: String, year: String) {
        viewModelScope.launch {
            _insertBookState.value = InsertBookState.Loading
            try {
                val book = Book(
                    id = 0,
                    title = title,
                    author = author,
                    year = year.toInt() // The view should ensure this is a valid Int
                )
                bookRepository.insertBook(book)
                _insertBookState.value = InsertBookState.Success
            } catch (_: Exception) {
                // Log the exception for debugging
                _insertBookState.value = InsertBookState.Error
            }
        }
    }

    fun resetState() {
        _insertBookState.value = InsertBookState.Idle
    }
}