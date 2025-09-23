/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.ui.viewmodel


import android.util.Log
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

sealed class InsertBookState {
    object Initial : InsertBookState()
    object Loading : InsertBookState()
    object Success : InsertBookState()
    object Error : InsertBookState()
}

@HiltViewModel
class InsertBookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _insertBookState = MutableStateFlow<InsertBookState>(InsertBookState.Initial)
    val insertBookState: StateFlow<InsertBookState> = _insertBookState

    fun insertBook(title: String, author: String, year: String) {
        viewModelScope.launch {
            _insertBookState.value = InsertBookState.Loading
            try {
                val book = Book(
                    id = 0,
                    title = title,
                    author = author,
                    year = year.toInt(),
                )
                bookRepository.insertBook(book)

                delay(1000L) // 1 second delay to wait a loading animation
                _insertBookState.value = InsertBookState.Success
            } catch (e: Exception) {
                Log.e("InsertBookViewModel", "Error on insert book: ${e.message}")
                _insertBookState.value = InsertBookState.Error
            }
        }
    }

    fun resetState() {
        _insertBookState.value = InsertBookState.Initial
    }
}