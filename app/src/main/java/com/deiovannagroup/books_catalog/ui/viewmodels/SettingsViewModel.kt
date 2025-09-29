/*
 * Copyright (c) 2023. Esta classe está sendo consedida para uso personal
 */

package com.deiovannagroup.books_catalog.ui.viewmodels

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.data.repositories.data_repository.DataRepository
import com.deiovannagroup.books_catalog.data.repositories.theme_repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingsUiEvent {
    data class ShowToast(val messageId: Int) : SettingsUiEvent()
    data class ShowConfirmationDialog(
        val titleId: Int, val messageId: Int
    ) : SettingsUiEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val dataRepository: DataRepository,
) : ViewModel() {

    private val _themeState = MutableStateFlow(themeRepository.theme.value)
    val themeState: StateFlow<Int> = _themeState.asStateFlow()

    private val _uiEvents = Channel<SettingsUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            themeRepository.theme.collect { newTheme ->
                _themeState.value = newTheme
            }
        }
    }

    fun onThemeSelected(checkedId: Int) {
        val selectedMode = when (checkedId) {
            R.id.rb_lightTheme -> AppCompatDelegate.MODE_NIGHT_NO
            R.id.rb_darkTheme -> AppCompatDelegate.MODE_NIGHT_YES
            R.id.rb_batteryTheme -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            R.id.rb_systemTheme -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> return
        }

        themeRepository.setTheme(selectedMode)
    }

    fun onClearDataClicked() {
        viewModelScope.launch {
            _uiEvents.send(
                SettingsUiEvent.ShowConfirmationDialog(
                    titleId = R.string.btn_clear_data,
                    messageId = R.string.alert_dialog_message
                )
            )
        }
    }

    fun onConfirmClearData() {
        viewModelScope.launch {
            dataRepository.clearApplicationData().collect { success ->
                val messageId =
                    if (success) R.string.success_msg
                    else R.string.error_msg

                _uiEvents.send(SettingsUiEvent.ShowToast(messageId))
            }
        }
    }
}