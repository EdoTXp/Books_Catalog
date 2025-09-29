/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.repositories.theme_repository

import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {

    val theme: StateFlow<Int>

    fun setTheme(value: Int)
}