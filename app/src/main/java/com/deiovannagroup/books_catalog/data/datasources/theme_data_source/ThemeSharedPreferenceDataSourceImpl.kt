/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.data.datasources.theme_data_source

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ThemeSharedPreferenceDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context
) : ThemeDataSource {
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(
            "settings_prefs",
            Context.MODE_PRIVATE,
        )

    override fun getTheme(): Int {
        return sharedPrefs.getInt(KEY_NIGHT_MODE, THEME_UNDEFINED)
    }

    override fun setTheme(themeValue: Int) {
        sharedPrefs.edit { putInt(KEY_NIGHT_MODE, themeValue) }
    }

    companion object {
        private const val KEY_NIGHT_MODE = "NightModeChoice"
        private const val THEME_UNDEFINED = -1
    }
}