/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.viewmodel

import androidx.lifecycle.ViewModel
import com.deiovannagroup.books_catalog.repositories.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    fun getSavedTheme(): Int = themeRepository.getTheme()
}