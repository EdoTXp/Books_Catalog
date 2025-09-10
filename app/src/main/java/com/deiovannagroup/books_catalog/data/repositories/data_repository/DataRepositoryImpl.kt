/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.repositories.data_repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val context: Context
) : DataRepository {
    override suspend fun clearApplicationData(): Flow<Boolean> = flow {
        var success = true

        context.databaseList().forEach { dbName ->
            val deleted = context.deleteDatabase(dbName)
            success = success && deleted
        }

        val sharedPrefsDir = File(context.filesDir.parent, "shared_prefs")
        if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory) {
            sharedPrefsDir.listFiles()?.forEach { file ->
                val deleted = file.delete()
                success = success && deleted
            }
        }

        emit(success)
    }
}