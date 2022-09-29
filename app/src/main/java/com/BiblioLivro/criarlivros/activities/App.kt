/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */
package com.BiblioLivro.criarlivros.activities

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())
    }
}