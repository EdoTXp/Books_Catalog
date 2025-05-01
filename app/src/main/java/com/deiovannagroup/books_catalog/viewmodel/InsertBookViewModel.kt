/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.viewmodel


import androidx.lifecycle.ViewModel
import com.deiovannagroup.books_catalog.repositories.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsertBookViewModel @Inject constructor(private val bookRepository: BookRepository) :
    ViewModel() {

    fun insertBook(titleBook: String, authorBook: String, yearBook: String): Boolean {
        return bookRepository.insert(
            titleBook,
            authorBook,
            yearBook,
        ) > 0
    }
}