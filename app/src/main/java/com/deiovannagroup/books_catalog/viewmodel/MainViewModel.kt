/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.viewmodel

import android.app.Application
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deiovannagroup.books_catalog.BuildConfig
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.repositories.ThemeRepository
import com.deiovannagroup.books_catalog.services.email_service.EmailService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

enum class SearchType {
    YEAR, AUTHOR, TITLE, ALL
}

data class SearchFieldUiState(
    val inputType: Int = InputType.TYPE_NULL,
    val filters: Array<InputFilter> = emptyArray(),
    val hintResId: Int = R.string.empty_field_error,
    val contentDescriptionResId: Int = R.string.empty_field_error,
    val isEnabled: Boolean = false,
    val selectedSearchType: SearchType = SearchType.ALL
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchFieldUiState

        if (inputType != other.inputType) return false
        if (hintResId != other.hintResId) return false
        if (contentDescriptionResId != other.contentDescriptionResId) return false
        if (isEnabled != other.isEnabled) return false
        if (!filters.contentEquals(other.filters)) return false
        if (selectedSearchType != other.selectedSearchType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inputType
        result = 31 * result + hintResId
        result = 31 * result + contentDescriptionResId
        result = 31 * result + isEnabled.hashCode()
        result = 31 * result + filters.contentHashCode()
        result = 31 * result + selectedSearchType.hashCode()
        return result
    }
}

sealed class MainUiEvent {
    data class ShowToast(val messageResId: Int, val extraMessage: String? = null) : MainUiEvent()
    data class NavigateToSearch(val searchTypeForIntent: Int, val searchKey: String) : MainUiEvent()
    object ShowFeedbackDialog : MainUiEvent()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    themeRepository: ThemeRepository,
    private val application: Application
) : ViewModel() {

    private val _currentTheme = MutableStateFlow(themeRepository.getTheme())
    val currentTheme: StateFlow<Int> = _currentTheme.asStateFlow()

    private val _searchFieldState = MutableStateFlow(
        SearchFieldUiState(
            contentDescriptionResId = R.string.txt_AccessDescriptionAll,
            selectedSearchType = SearchType.ALL,
            isEnabled = false,
            hintResId = R.string.empty_field_error
        )
    )
    val searchFieldState: StateFlow<SearchFieldUiState> = _searchFieldState.asStateFlow()

    val searchQuery = MutableStateFlow("")

    private val _uiEvents = Channel<MainUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun onSearchTypeSelected(checkedId: Int) {
        val newSearchType = when (checkedId) {
            R.id.rbSearchByYear -> SearchType.YEAR
            R.id.rbSearchByAuthor -> SearchType.AUTHOR
            R.id.rbSearchByTitle -> SearchType.TITLE
            R.id.rbSearchByAll -> SearchType.ALL
            else -> _searchFieldState.value.selectedSearchType
        }

        _searchFieldState.value = when (newSearchType) {
            SearchType.YEAR -> SearchFieldUiState(
                inputType = InputType.TYPE_CLASS_NUMBER,
                filters = arrayOf(LengthFilter(4)),
                hintResId = R.string.hint_ano,
                contentDescriptionResId = R.string.txt_AccessDescriptionYear,
                isEnabled = true,
                selectedSearchType = newSearchType
            )

            SearchType.AUTHOR -> SearchFieldUiState(
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                filters = arrayOf(LengthFilter(100)),
                hintResId = R.string.hint_autor,
                contentDescriptionResId = R.string.txt_AccessDescriptionAuthor,
                isEnabled = true,
                selectedSearchType = newSearchType
            )

            SearchType.TITLE -> SearchFieldUiState(
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                filters = arrayOf(LengthFilter(100)),
                hintResId = R.string.hint_titulo,
                contentDescriptionResId = R.string.txt_AccessDescriptionTitle,
                isEnabled = true,
                selectedSearchType = newSearchType
            )

            SearchType.ALL -> SearchFieldUiState(
                inputType = InputType.TYPE_NULL,
                filters = emptyArray(),
                hintResId = R.string.empty_field_error,
                contentDescriptionResId = R.string.txt_AccessDescriptionAll,
                isEnabled = false,
                selectedSearchType = newSearchType
            )
        }
        if (!_searchFieldState.value.isEnabled) {
            searchQuery.value = ""
        }
    }

    fun onSearchRequested() {
        val currentSearchState = _searchFieldState.value
        val query = searchQuery.value

        if (query.isEmpty() && currentSearchState.isEnabled) {
            viewModelScope.launch { _uiEvents.send(MainUiEvent.ShowToast(R.string.FieldEmpty)) }
            return
        }

        val searchTypeForIntent = when (currentSearchState.selectedSearchType) {
            SearchType.YEAR -> R.id.rbSearchByYear
            SearchType.AUTHOR -> R.id.rbSearchByAuthor
            SearchType.TITLE -> R.id.rbSearchByTitle
            SearchType.ALL -> R.id.rbSearchByAll
        }
        viewModelScope.launch {
            _uiEvents.send(MainUiEvent.NavigateToSearch(searchTypeForIntent, query))
        }
    }

    fun onFeedbackMenuSelected() {
        viewModelScope.launch { _uiEvents.send(MainUiEvent.ShowFeedbackDialog) }
    }

    fun sendFeedbackEmail(feedbackText: String, activityContext: android.content.Context) {
        if (feedbackText.isNotBlank()) {
            val random = (Math.random() * 1.0E14 + 1.0E9).toLong()
            val subjectTemplate = application.getString(R.string.email_subject)
            val subject = "$subjectTemplate#$random"
            val timeGeneratedTemplate = application.getString(R.string.email_timegenerated)
            val time = getLocaleTime()

            EmailService.sendEmail(
                context = activityContext,
                emailBody = "$feedbackText\n$timeGeneratedTemplate$time".trimIndent(),
                emailSubject = subject,
            )
            viewModelScope.launch { _uiEvents.send(MainUiEvent.ShowToast(R.string.email_btn_send)) }
        } else {
            viewModelScope.launch { _uiEvents.send(MainUiEvent.ShowToast(R.string.email_notextinsert)) }
        }
    }

    fun onAppVersionMenuSelected() {
        viewModelScope.launch {
            _uiEvents.send(
                MainUiEvent.ShowToast(
                    0,
                    BuildConfig.VERSION_NAME,
                )
            )
        }
    }


    private fun getLocaleTime(): String {
        val userLocale = Locale.getDefault() // O un Locale specifico se necessario
        val dateFormat: SimpleDateFormat = if (userLocale.language == Locale.ENGLISH.language) {
            SimpleDateFormat("hh:mm a - MM/dd/yyyy", userLocale)
        } else {
            SimpleDateFormat("HH:mm - dd/MM/yyyy", userLocale)
        }
        return dateFormat.format(Calendar.getInstance(userLocale).time)
    }
}
