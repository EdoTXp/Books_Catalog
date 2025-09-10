/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.repositories.theme_repository

import com.deiovannagroup.books_catalog.shared.helpers.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ThemeRepository @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) {
    companion object {
        private const val KEY_NIGHT_MODE = "NightModeChoice"
        const val THEME_UNDEFINED = -1
    }

    private val _theme = MutableStateFlow(getTheme())
    val theme: StateFlow<Int> = _theme.asStateFlow()

    fun getTheme() = sharedPreferencesHelper.getInt(
        KEY_NIGHT_MODE,
        THEME_UNDEFINED,
    )

    fun setTheme(value: Int) {
        sharedPreferencesHelper.putInt(KEY_NIGHT_MODE, value)
        _theme.value = value
    }

}