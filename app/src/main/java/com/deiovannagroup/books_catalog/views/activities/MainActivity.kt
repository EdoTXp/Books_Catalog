/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.views.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.databinding.ActivityMainBinding
import com.deiovannagroup.books_catalog.services.app_services.AlertDialogService
import com.deiovannagroup.books_catalog.utils.showToastAndVibrate
import com.deiovannagroup.books_catalog.viewmodel.MainUiEvent
import com.deiovannagroup.books_catalog.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupObservers()
        initMenuBar()
        initListeners()

        if (savedInstanceState == null) {
            binding.rdgSearchBy.check(R.id.rbSearchByAll)
            mainViewModel.onSearchTypeSelected(binding.rdgSearchBy.checkedRadioButtonId)
        }
    }

    private fun setupEdgeToEdge() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.currentTheme.collect { themeMode ->
                        AppCompatDelegate.setDefaultNightMode(themeMode)
                    }
                }

                launch {
                    mainViewModel.searchFieldState.collect { state ->
                        binding.edtSearch.isEnabled = state.isEnabled
                        binding.edtSearch.inputType = state.inputType
                        binding.edtSearch.filters = state.filters
                        binding.edtSearch.hint =
                            if (state.hintResId != R.string.empty_field_error) getString(state.hintResId) else ""
                        binding.edtSearch.contentDescription =
                            getString(state.contentDescriptionResId)
                    }
                }

                launch {
                    mainViewModel.searchQuery.collect { query ->
                        if (binding.edtSearch.text.toString() != query) {
                            binding.edtSearch.setText(query)
                            binding.edtSearch.setSelection(query.length)
                        }
                    }
                }

                launch {
                    mainViewModel.uiEvents.collect { event ->
                        when (event) {
                            is MainUiEvent.ShowToast -> {
                                val message =
                                    if (event.messageResId != 0) getString(event.messageResId) else event.extraMessage
                                        ?: ""
                                showToastAndVibrate(message)
                            }

                            is MainUiEvent.NavigateToSearch -> {
                                val intent =
                                    Intent(this@MainActivity, SearchActivity::class.java).apply {
                                        putExtra("tipo", event.searchTypeForIntent)
                                        putExtra("chave", event.searchKey)
                                    }
                                startActivity(intent)
                            }

                            is MainUiEvent.ShowFeedbackDialog -> {
                                showFeedbackEmailDialog()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.edtSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                mainViewModel.onSearchRequested()
                return@setOnKeyListener true
            }
            false
        }

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainViewModel.searchQuery.value = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnInsert.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    InsertBookActivity::class.java,
                )
            )
        }

        binding.btnSearch.setOnClickListener { mainViewModel.onSearchRequested() }

        binding.rdgSearchBy.setOnCheckedChangeListener { _, checkedId ->
            mainViewModel.onSearchTypeSelected(checkedId)
        }
    }

    private fun initMenuBar() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menubar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_settings -> {
                        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        true
                    }

                    R.id.menu_feedback -> {
                        mainViewModel.onFeedbackMenuSelected()
                        true
                    }

                    R.id.menu_app_version -> {
                        mainViewModel.onAppVersionMenuSelected()
                        true
                    }

                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)
    }

    private fun showFeedbackEmailDialog() {
        val emailEditText = EditText(this).apply {
            inputType =
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            gravity = Gravity.START or Gravity.TOP
            isSingleLine = false
            minLines = 3
            isHorizontalScrollBarEnabled = false
            hint = getString(R.string.email_textHint)
            setHintTextColor(ResourcesCompat.getColor(resources, R.color.textColorHint, theme))
            setTextColor(ResourcesCompat.getColor(resources, R.color.textColor, theme))
        }

        AlertDialogService.showDialogWithCustomView(
            context = this,
            title = getString(R.string.email_title),
            customView = emailEditText,
            positiveButton = getString(R.string.email_btn_send),
            negativeButton = getString(R.string.email_btn_cancel),
            positiveAction = {
                mainViewModel.sendFeedbackEmail(
                    emailEditText.text.toString(),
                    this@MainActivity,
                )
            }
        )
    }
}
