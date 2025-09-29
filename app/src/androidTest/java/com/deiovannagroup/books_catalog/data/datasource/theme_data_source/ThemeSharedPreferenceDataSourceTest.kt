/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.datasource.theme_data_source

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.deiovannagroup.books_catalog.data.datasources.theme_data_source.ThemeSharedPreferenceDataSourceImpl
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeSharedPreferenceDataSourceTest {

    private lateinit var context: Context
    private lateinit var themeDataSource: ThemeSharedPreferenceDataSourceImpl

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        clearSharedPreferences()

        themeDataSource = ThemeSharedPreferenceDataSourceImpl(context)
    }

    @After
    fun tearDown() {
        clearSharedPreferences()
    }

    @Test
    fun getTheme_when_no_value_is_set_should_return_undefined() {
        val expectedTheme = -1

        val actualTheme = themeDataSource.getTheme()

        assertThat(actualTheme).isEqualTo(expectedTheme)
    }

    @Test
    fun setTheme_should_correctly_save_the_value() {
        val themeToSet = AppCompatDelegate.MODE_NIGHT_YES
        themeDataSource.setTheme(themeToSet)
        val savedTheme = themeDataSource.getTheme()
        assertThat(savedTheme).isEqualTo(themeToSet)
    }

    @Test
    fun setTheme_should_overwrite_an_existing_value() {
        themeDataSource.setTheme(AppCompatDelegate.MODE_NIGHT_NO)

        val newTheme = AppCompatDelegate.MODE_NIGHT_YES
        themeDataSource.setTheme(newTheme)

        val updatedTheme = themeDataSource.getTheme()
        assertThat(updatedTheme).isEqualTo(newTheme)
    }

    private fun clearSharedPreferences() {
        val prefs = context.getSharedPreferences(
            "settings_prefs",
            Context.MODE_PRIVATE,
        )

        prefs.edit().clear().commit()
    }

}