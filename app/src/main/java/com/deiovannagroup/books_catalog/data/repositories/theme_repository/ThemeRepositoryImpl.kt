/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.repositories.theme_repository

import com.deiovannagroup.books_catalog.data.datasources.theme_data_source.ThemeDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val themeDataSource: ThemeDataSource
) : ThemeRepository {
    private val _theme = MutableStateFlow(themeDataSource.getTheme())
    override val theme: StateFlow<Int> = _theme

    override fun setTheme(value: Int) {
        themeDataSource.setTheme(value)
        _theme.value = value
    }
}