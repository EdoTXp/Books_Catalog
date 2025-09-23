/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso personal
 */
package com.deiovannagroup.books_catalog.ui.views.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.databinding.ActivitySettingsBinding
import com.deiovannagroup.books_catalog.domain.services.app_services.AlertDialogService
import com.deiovannagroup.books_catalog.shared.utils.setEdgeToEdgeLayout
import com.deiovannagroup.books_catalog.shared.utils.setSupportActionBar
import com.deiovannagroup.books_catalog.shared.utils.showToastAndVibrate
import com.deiovannagroup.books_catalog.ui.viewmodel.SettingsUiEvent
import com.deiovannagroup.books_catalog.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeLayout(binding.root, binding.main)
        setSupportActionBar()
        initListeners()
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingsViewModel.themeState.collect { themeMode ->
                        setCheckedRadioButton(themeMode)
                    }
                }

                launch {
                    settingsViewModel.uiEvents.collect { event ->
                        when (event) {
                            is SettingsUiEvent.ShowToast -> showToastAndVibrate(
                                getString(event.messageId),
                            )

                            is SettingsUiEvent.ShowConfirmationDialog -> {
                                AlertDialogService.showDialog(
                                    context = this@SettingsActivity,
                                    message = getString(event.messageId),
                                    title = getString(event.titleId),
                                    positiveAction = { event.onConfirm() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.apply {
            btnClearData.setOnClickListener {
                settingsViewModel.onClearDataClicked()
            }
            radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
                settingsViewModel.onThemeSelected(checkedId)
            }
        }
    }

    private fun setCheckedRadioButton(themeMode: Int) {
        val buttonId = when (themeMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> R.id.rb_lightTheme
            AppCompatDelegate.MODE_NIGHT_YES -> R.id.rb_darkTheme
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> R.id.rb_batteryTheme
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> R.id.rb_systemTheme
            else -> return
        }
        binding.radioGroupTheme.check(buttonId)
    }
}