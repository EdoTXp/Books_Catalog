/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.shared.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class ThemeSharedPreference @Inject constructor(context: Context) {

    companion object {
        private const val KEY_NIGHT_MODE = "NightModeChoice"
        private const val THEME_UNDEFINED = -1
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(
            "settings_prefs",
            Context.MODE_PRIVATE,
        )

    fun setTheme(themeValue: Int) {
        sharedPrefs.edit { putInt(KEY_NIGHT_MODE, themeValue) }
    }

    fun getTheme(): Int {
        return sharedPrefs.getInt(KEY_NIGHT_MODE, THEME_UNDEFINED)
    }
}