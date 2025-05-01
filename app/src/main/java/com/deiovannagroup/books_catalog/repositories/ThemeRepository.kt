/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.repositories

import com.deiovannagroup.books_catalog.helpers.SharedPreferencesHelper
import javax.inject.Inject

class ThemeRepository @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) {
    companion object {
        private const val KEY_NIGHT_MODE = "NightModeChoice"
        const val THEME_UNDEFINED = -1
    }

    fun getTheme() = sharedPreferencesHelper.getInt(
        KEY_NIGHT_MODE,
        THEME_UNDEFINED,
    )

    fun setTheme(value: Int) = sharedPreferencesHelper.putInt(KEY_NIGHT_MODE, value)

}