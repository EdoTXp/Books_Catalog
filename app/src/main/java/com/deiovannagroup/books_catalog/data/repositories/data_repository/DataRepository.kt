/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.repositories.data_repository

import kotlinx.coroutines.flow.Flow

interface DataRepository {
    suspend fun clearApplicationData(): Flow<Boolean>
}