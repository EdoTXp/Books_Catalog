/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.datasources.theme_data_source

interface ThemeDataSource {
    fun getTheme(): Int
    fun setTheme(themeValue: Int)
}