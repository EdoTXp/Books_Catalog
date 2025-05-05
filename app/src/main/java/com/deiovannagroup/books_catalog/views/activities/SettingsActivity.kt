/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.views.activities


import android.os.Bundle
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.deiovannagroup.books_catalog.databinding.ActivitySettingsBinding
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.services.app_services.AlertDialogService
import com.deiovannagroup.books_catalog.utils.setSupportActionBar
import com.deiovannagroup.books_catalog.utils.showToastAndVibrate
import com.deiovannagroup.books_catalog.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    private val settingsViewModel: SettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeLayout()
        setSupportActionBar()
        setCheckedRadioButtonByTheme()
        initListeners()
    }

    private fun initListeners() {
        // Adicionado os eventos de click
        binding.btnClearData.setOnClickListener {
            AlertDialogService.showDialog(
                this,
                message = getString(R.string.alert_dialog_message),
                title = getString(R.string.btn_clear_data),
                positiveAction = {
                    showToastAndVibrate(
                        getString(
                            if (settingsViewModel.clearApplicationData(this))
                                R.string.success_msg
                            else R.string.error_msg
                        )
                    )
                },
            )
        }

        binding.radioGroupTheme.setOnCheckedChangeListener { radio: RadioGroup?, checkedId: Int ->
            if (radio == null)
                return@setOnCheckedChangeListener

            val selectedMode = when (checkedId) {
                R.id.rb_lightTheme -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.rb_darkTheme -> AppCompatDelegate.MODE_NIGHT_YES
                R.id.rb_batteryTheme -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                R.id.rb_systemTheme -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                else -> return@setOnCheckedChangeListener
            }

            if (settingsViewModel.getSavedTheme() != selectedMode) {
                settingsViewModel.setTheme(selectedMode)
                AppCompatDelegate.setDefaultNightMode(selectedMode)
            }
        }
    }

    private fun setCheckedRadioButtonByTheme() {
        when (settingsViewModel.getSavedTheme()) {
            AppCompatDelegate.MODE_NIGHT_YES -> {
                binding.radioGroupTheme.check(R.id.rb_darkTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                binding.radioGroupTheme.check(R.id.rb_systemTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }

            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> {
                binding.radioGroupTheme.check(R.id.rb_batteryTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }

            AppCompatDelegate.MODE_NIGHT_NO -> {
                binding.radioGroupTheme.check(R.id.rb_lightTheme)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setEdgeToEdgeLayout() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom,
            )
            insets
        }
    }

    /* private val defaultLanguage: Unit
         get() {
             val locale: Locale = Resources.getSystem().configuration.locales[0]

             when (locale.language) {
                 "en" -> radioGroupLanguage.check(R.id.rb_english)
                 "it" -> radioGroupLanguage.check(R.id.rb_italy)
                 "pt" -> radioGroupLanguage.check(R.id.rb_portuguese)
                 else -> radioGroupLanguage.check(R.id.rb_english)
             }
         }*/
}