package com.deiovannagroup.books_catalog.ui.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import app.cash.turbine.test
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.data.repositories.data_repository.DataRepository
import com.deiovannagroup.books_catalog.data.repositories.theme_repository.ThemeRepository
import com.deiovannagroup.books_catalog.utils.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var themeRepository: ThemeRepository
    private lateinit var dataRepository: DataRepository

    private lateinit var viewModel: SettingsViewModel

    private val themeFlow = MutableStateFlow(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    @Before
    fun setUp() {
        themeRepository = mockk {
            every { theme } returns themeFlow
            every { setTheme(any()) } just runs
        }
        dataRepository = mockk()

        viewModel = SettingsViewModel(themeRepository, dataRepository)
    }

    @Test
    fun `init - themeState should reflect repository's initial theme`() = runTest {
        // Assert
        assertThat(viewModel.themeState.value).isEqualTo(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    @Test
    fun `init - themeState should update when repository's theme flow emits`() = runTest {
        viewModel.themeState.test {
            assertThat(awaitItem()).isEqualTo(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

            themeFlow.value = AppCompatDelegate.MODE_NIGHT_YES

            assertThat(awaitItem()).isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    @Test
    fun `onThemeSelected - with light theme id - calls repository with MODE_NIGHT_NO`() {
        viewModel.onThemeSelected(R.id.rb_lightTheme)

        verify { themeRepository.setTheme(AppCompatDelegate.MODE_NIGHT_NO) }
    }

    @Test
    fun `onThemeSelected - with dark theme id - calls repository with MODE_NIGHT_YES`() {
        viewModel.onThemeSelected(R.id.rb_darkTheme)

        verify { themeRepository.setTheme(AppCompatDelegate.MODE_NIGHT_YES) }
    }

    @Test
    fun `onThemeSelected - with invalid id - does not call repository`() {
        viewModel.onThemeSelected(-2)

        verify(exactly = 0) { themeRepository.setTheme(any()) }
    }

    @Test
    fun `onClearDataClicked - sends ShowConfirmationDialog event`() = runTest {
        viewModel.uiEvents.test {
            viewModel.onClearDataClicked()

            val expectedEvent = SettingsUiEvent.ShowConfirmationDialog(
                titleId = R.string.btn_clear_data,
                messageId = R.string.alert_dialog_message
            )
            assertThat(awaitItem()).isEqualTo(expectedEvent)
        }
    }

    @Test
    fun `onConfirmClearData - when successful - sends success toast event`() = runTest {
        coEvery { dataRepository.clearApplicationData() } returns flowOf(true)

        viewModel.uiEvents.test {
            viewModel.onConfirmClearData()

            val expectedEvent = SettingsUiEvent.ShowToast(R.string.success_msg)
            assertThat(awaitItem()).isEqualTo(expectedEvent)
        }
    }

    @Test
    fun `onConfirmClearData - when failure - sends error toast event`() = runTest {
        coEvery { dataRepository.clearApplicationData() } returns flowOf(false)

        viewModel.uiEvents.test {
            // Act
            viewModel.onConfirmClearData()

            // Assert
            val expectedEvent = SettingsUiEvent.ShowToast(R.string.error_msg)
            assertThat(awaitItem()).isEqualTo(expectedEvent)
        }
    }
}