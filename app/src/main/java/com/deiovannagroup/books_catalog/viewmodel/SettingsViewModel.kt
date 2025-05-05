/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.deiovannagroup.books_catalog.repositories.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    fun getSavedTheme(): Int = themeRepository.getTheme()
    fun setTheme(theme: Int) = themeRepository.setTheme(theme)


    fun clearApplicationData(context: Context): Boolean {
        var success = true

        context.databaseList().forEach { dbName ->
            val deleted = context.deleteDatabase(dbName)
            success = success && deleted
        }

        val sharedPrefsDir = File(context.filesDir.parent, "settings_prefs")
        if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory) {
            sharedPrefsDir.listFiles()?.forEach { file ->
                val deleted = file.delete()
                success = success && deleted
            }
        }

        return success
    }
}