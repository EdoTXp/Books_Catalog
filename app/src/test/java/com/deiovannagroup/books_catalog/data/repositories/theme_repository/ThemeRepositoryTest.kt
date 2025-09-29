/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.repositories.theme_repository

import app.cash.turbine.test
import com.deiovannagroup.books_catalog.data.datasources.theme_data_source.ThemeDataSource
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ThemeRepositoryTest {
    private lateinit var themeDataSource: ThemeDataSource

    private lateinit var themeRepositoryImpl: ThemeRepositoryImpl

    private val initialThemeValue = 1

    @Before
    fun setup() {
        themeDataSource = mockk()

        every { themeDataSource.getTheme() } returns initialThemeValue
        every { themeDataSource.setTheme(any()) } returns Unit

        themeRepositoryImpl = ThemeRepositoryImpl(themeDataSource)
    }

    @Test
    fun `repository initializes with theme from shared preferences`() = runTest {
        assert(themeRepositoryImpl.theme.value == initialThemeValue)
    }

    @Test
    fun `getTheme delegates call to shared preferences`() = runTest {
        val currentTheme = themeRepositoryImpl.theme.value
        assertThat(currentTheme).isEqualTo(initialThemeValue)
        verify(exactly = 1) { themeDataSource.getTheme() }
    }


    @Test
    fun `setTheme updates shared preferences and emits new value to flow`() = runTest {
        val newThemeValue = 2

        themeRepositoryImpl.theme.test {
            assertThat(awaitItem()).isEqualTo(initialThemeValue)

            themeRepositoryImpl.setTheme(newThemeValue)

            assertThat(awaitItem()).isEqualTo(newThemeValue)
            verify(exactly = 1) { themeDataSource.setTheme(newThemeValue) }

            cancelAndIgnoreRemainingEvents()
        }
    }
}