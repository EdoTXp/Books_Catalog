/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.di

import android.content.Context
import com.deiovannagroup.books_catalog.repositories.BookRepository
import com.deiovannagroup.books_catalog.repositories.BookRepositoryImpl
import com.deiovannagroup.books_catalog.helpers.DatabaseHelper
import com.deiovannagroup.books_catalog.helpers.DatabaseHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBookDatabase(@ApplicationContext appContext: Context): DatabaseHelper {
        return DatabaseHelperImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideBookRepository(bookDatabase: DatabaseHelper): BookRepository {
        return BookRepositoryImpl(bookDatabase)
    }
}