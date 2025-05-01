/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class SharedPreferencesHelper @Inject constructor(context: Context) {

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(
            "settings_prefs",
            Context.MODE_PRIVATE,
        )

    fun putInt(key: String, value: Int) {
        sharedPrefs.edit { putInt(key, value) }
    }

    fun getInt(key: String, default: Int = -1): Int {
        return sharedPrefs.getInt(key, default)
    }
}