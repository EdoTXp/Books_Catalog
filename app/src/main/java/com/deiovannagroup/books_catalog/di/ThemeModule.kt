/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.di

import android.content.Context
import com.deiovannagroup.books_catalog.shared.helpers.SharedPreferencesHelper
import com.deiovannagroup.books_catalog.data.repositories.theme_repository.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ThemeModule {
    @Provides
    @Singleton
    fun provideSharedPreferencesHelper(
        @ApplicationContext appContext: Context,
    ): SharedPreferencesHelper {
        return SharedPreferencesHelper(appContext)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(
        sharedPreferencesHelper: SharedPreferencesHelper,
    ): ThemeRepository {
        return ThemeRepository(sharedPreferencesHelper)
    }
}
