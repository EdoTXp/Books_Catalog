/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.deiovannagroup.books_catalog.data.repositories.theme_repository.ThemeRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class BookCatalogApp : Application() {

    @Inject
    lateinit var themeRepository: ThemeRepository

    override fun onCreate() {
        super.onCreate()
        initializeTheme()
    }

    private fun initializeTheme() {
        CoroutineScope(Dispatchers.Main).launch {
            themeRepository.theme.collect { themeMode ->
                AppCompatDelegate.setDefaultNightMode(themeMode)
                Log.i("BookCatalogAppTAG", "Theme set to $themeMode")
            }
        }
    }
}