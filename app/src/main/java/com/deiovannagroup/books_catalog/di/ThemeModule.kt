/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.di

import android.content.Context
import com.deiovannagroup.books_catalog.data.datasources.theme_data_source.ThemeDataSource
import com.deiovannagroup.books_catalog.data.repositories.theme_repository.ThemeRepository
import com.deiovannagroup.books_catalog.data.datasources.theme_data_source.ThemeSharedPreferenceDataSourceImpl
import com.deiovannagroup.books_catalog.data.repositories.theme_repository.ThemeRepositoryImpl
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
    ): ThemeDataSource {
        return ThemeSharedPreferenceDataSourceImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(
        themeDataSource: ThemeSharedPreferenceDataSourceImpl,
    ): ThemeRepository {
        return ThemeRepositoryImpl(themeDataSource)
    }
}
