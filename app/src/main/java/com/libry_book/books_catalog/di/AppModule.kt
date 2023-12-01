/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.libry_book.books_catalog.di

import android.app.Application
import com.libry_book.books_catalog.repositories.BookRepository
import com.libry_book.books_catalog.repositories.BookRepositoryImpl
import com.libry_book.books_catalog.storage.DatabaseHelper
import com.libry_book.books_catalog.storage.DatabaseHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBookDatabase(appContext: Application): DatabaseHelper {
        return DatabaseHelperImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideBookRepository(bookDatabase: DatabaseHelper): BookRepository {
        return BookRepositoryImpl(bookDatabase)
    }
}