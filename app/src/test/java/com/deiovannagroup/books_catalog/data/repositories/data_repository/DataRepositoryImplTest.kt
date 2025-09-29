/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.data.repositories.data_repository

import android.content.Context
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DataRepositoryImplTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var mockContext: Context
    private lateinit var repository: DataRepositoryImpl
    private lateinit var filesDir: File
    private lateinit var sharedPrefsDir: File


    @Before
    fun setUp() {
        mockContext = mockk()
        filesDir = tempFolder.newFolder("files")
        sharedPrefsDir = tempFolder.newFolder("shared_prefs")

        every { mockContext.filesDir } returns filesDir

        repository = DataRepositoryImpl(mockContext)
    }

    @Test
    fun `clearApplicationData should emit true when all data is deleted successfully`() = runTest {
        every { mockContext.databaseList() } returns arrayOf("db1.db", "db2.db")
        every { mockContext.deleteDatabase(any()) } returns true

        val prefFile1 = File(sharedPrefsDir, "prefs1.xml")
        prefFile1.createNewFile()

        val prefFile2 = File(sharedPrefsDir, "prefs2.xml")
        prefFile2.createNewFile()

        assertThat(sharedPrefsDir.listFiles()?.size).isEqualTo(2)

        repository.clearApplicationData().test {
            val result = awaitItem()
            assertThat(result).isTrue()

            awaitComplete()
        }

        assertThat(sharedPrefsDir.listFiles()?.size).isEqualTo(0)
    }

    @Test
    fun `clearApplicationData should emit false when a database deletion fails`() = runTest {
        every { mockContext.databaseList() } returns arrayOf("db1.db", "db2.db")
        every { mockContext.deleteDatabase("db1.db") } returns true
        every { mockContext.deleteDatabase("db2.db") } returns false

        repository.clearApplicationData().test {
            assertThat(awaitItem()).isFalse()
            awaitComplete()
        }
    }

    @Test
    fun `clearApplicationData should emit true when there is no data to clear`() = runTest {
        every { mockContext.databaseList() } returns emptyArray()
        assertThat(sharedPrefsDir.listFiles()).isEmpty()

        repository.clearApplicationData().test {
            assertThat(awaitItem()).isTrue()
            awaitComplete()
        }
    }

}