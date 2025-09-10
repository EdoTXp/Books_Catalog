/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.di

import android.content.Context
import androidx.room.Room
import com.deiovannagroup.books_catalog.data.dao.BookDao
import com.deiovannagroup.books_catalog.data.database.AppDatabase
import com.deiovannagroup.books_catalog.data.datasources.BookDataSource.BookDataSource
import com.deiovannagroup.books_catalog.data.datasources.BookDataSource.LocalBookDataSourceImpl
import com.deiovannagroup.books_catalog.data.repositories.book_repository.BookRepository
import com.deiovannagroup.books_catalog.data.repositories.book_repository.BookRepositoryImpl
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "books.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBookDao(appDatabase: AppDatabase): BookDao {
        return appDatabase.bookDao()
    }

    @Provides
    @Singleton
    fun provideBookDataSource(bookDao: BookDao): BookDataSource {
        return LocalBookDataSourceImpl(bookDao)
    }

    @Provides
    @Singleton
    fun provideBookRepository(bookDataSource: BookDataSource): BookRepository {
        return BookRepositoryImpl(bookDataSource)
    }
}