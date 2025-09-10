/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.di

import android.content.Context
import com.deiovannagroup.books_catalog.data.repositories.data_repository.DataRepository
import com.deiovannagroup.books_catalog.data.repositories.data_repository.DataRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDataRepository(@ApplicationContext appContext: Context): DataRepository {
        return DataRepositoryImpl(appContext)
    }
}