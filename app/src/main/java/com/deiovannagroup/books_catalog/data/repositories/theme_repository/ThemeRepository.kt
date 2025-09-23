/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.repositories.theme_repository

import com.deiovannagroup.books_catalog.shared.helpers.ThemeSharedPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ThemeRepository @Inject constructor(
    private val themeSharedPreference: ThemeSharedPreference
) {
    private val _theme = MutableStateFlow(getTheme())
    val theme: StateFlow<Int> = _theme

    fun getTheme(): Int = themeSharedPreference.getTheme()

    fun setTheme(value: Int) {
        themeSharedPreference.setTheme(value)
        _theme.value = value
    }
}