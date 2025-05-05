/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.views.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import com.deiovannagroup.books_catalog.BuildConfig
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.databinding.ActivityMainBinding
import com.deiovannagroup.books_catalog.services.app_services.AlertDialogService
import com.deiovannagroup.books_catalog.services.email_service.EmailService
import com.deiovannagroup.books_catalog.utils.showToastAndVibrate
import com.deiovannagroup.books_catalog.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultTheme()
        installSplashScreen()
        setEdgeToEdgeLayout()
        initMenuBar()
        initListeners()
    }

    private fun setDefaultTheme() {
        when (mainViewModel.getSavedTheme()) {
            AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )

            AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )

            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            )

            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            )
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

    private fun initListeners() {
        binding.edtSearch.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            /**
             * When the event is invoked, a filtering will be done, capturing only the ACTION_UP action.
             * Then, the enter key will be captured to call the @sendSearch action.
             */
            if (event.action == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    sendSearch()
                    return@setOnKeyListener true
                }
            }
            false
        }
        binding.btnInsert.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    InsertBookActivity::class.java,
                ),
            )
        }

        binding.btnSearch.setOnClickListener { sendSearch() }

        binding.rdgSearchBy.setOnCheckedChangeListener { _, checkedId ->
            binding.edtSearch.isEnabled = true
            binding.edtSearch.setText("")
            when (checkedId) {
                R.id.rbPesquisarPorAno -> {
                    binding.edtSearch.inputType = InputType.TYPE_CLASS_NUMBER
                    binding.edtSearch.filters = arrayOf<InputFilter>(LengthFilter(4))
                    binding.edtSearch.setHint(R.string.hint_ano)
                    binding.edtSearch.contentDescription =
                        getString(R.string.txt_AccessDescriptionYear)
                }

                R.id.rbPesquisarPorAutor -> {
                    binding.edtSearch.filters = arrayOf<InputFilter>(LengthFilter(100))
                    binding.edtSearch.setHint(R.string.hint_autor)
                    binding.edtSearch.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    binding.edtSearch.contentDescription =
                        getString(R.string.txt_AccessDescriptionAuthor)
                }

                R.id.rbPesquisarPorTitulo -> {
                    binding.edtSearch.filters = arrayOf<InputFilter>(LengthFilter(100))
                    binding.edtSearch.setHint(R.string.hint_titulo)
                    binding.edtSearch.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    binding.edtSearch.contentDescription =
                        getString(R.string.txt_AccessDescriptionTitle)
                }

                R.id.rbPesquisarPorTodos -> {
                    binding.edtSearch.isEnabled = false
                    binding.edtSearch.hint = ""
                    binding.edtSearch.filters = arrayOf<InputFilter>(LengthFilter(0))
                    binding.edtSearch.inputType = InputType.TYPE_NULL
                    binding.edtSearch.contentDescription =
                        getString(R.string.txt_AccessDescriptionAll)
                }
            }
        }
    }

    private fun initMenuBar() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(
                menu: Menu, menuInflater: MenuInflater
            ) {
                val inflater = menuInflater
                inflater.inflate(R.menu.menubar, menu)
                true
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_settings -> {
                        //Iniciando a nova Tela Impostações
                        startActivity(
                            Intent(
                                this@MainActivity,
                                SettingsActivity::class.java,
                            ),
                        )
                    }

                    R.id.menu_feedback -> {
                        // Creating the AlertDialog to send the email
                        val emailEditText = EditText(this@MainActivity).apply {
                            inputType = InputType.TYPE_CLASS_TEXT
                            gravity = Gravity.START or Gravity.TOP
                            isSingleLine = false
                            isHorizontalScrollBarEnabled = false
                            hint = getString(R.string.email_textHint)
                            setHintTextColor(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.colorTextHint,
                                    theme,
                                ),
                            )
                            setTextColor(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.colorPrimaryText,
                                    theme,
                                ),
                            )
                        }

                        AlertDialogService.showDialogWithCustomView(
                            this@MainActivity,
                            getString(R.string.email_title),
                            customView = emailEditText,
                            positiveButton = getString(R.string.email_btn_send),
                            negativeButton = getString(R.string.email_btn_cancel),
                            positiveAction = {
                                /**
                                 * If the "emailBodyText" is not empty,
                                 * the email header will be created with a title,
                                 * a random number for the message code.
                                 * The message body with the "emailEditText"
                                 * along with the "local" date of the device.
                                 * Else the emailEditText is empty,
                                 * a message will be printed plus a vibration
                                 */
                                if (emailEditText.text.toString() != "") {

                                    val random = (Math.random() * 1.0E14 + 1.0E9).toLong()
                                    val subject =
                                        getString(R.string.email_subject) +
                                                "#" +
                                                random.toString()

                                    val time = getLocaleTime()

                                    EmailService.sendEmail(
                                        this@MainActivity,
                                        emailBody = """
                                                ${emailEditText.text}
                                                ${getString(R.string.email_timegenerated)}$time
                                            """.trimIndent(),
                                        emailSubject = subject,
                                    )
                                } else {
                                    showToastAndVibrate(
                                        getString(R.string.email_notextinsert),
                                    )
                                }
                            },
                        )
                    }
                    // displaying the app version
                    R.id.menu_app_version -> {
                        showToastAndVibrate(
                            BuildConfig.VERSION_NAME,
                        )
                    }

                    else -> return onOptionsItemSelected(menuItem)
                }
                return true
            }
        })
    }

    private fun sendSearch() {
        if (binding.edtSearch.text.toString().isEmpty()
            &&
            binding.rdgSearchBy.checkedRadioButtonId != R.id.rbPesquisarPorTodos
        ) {
            showToastAndVibrate(
                getString(R.string.FieldEmpty),
            )
            return
        }

        val intent = Intent(
            this,
            SearchActivity::class.java,
        )

        intent.putExtra(
            "tipo",
            binding.rdgSearchBy.checkedRadioButtonId,
        )
        intent.putExtra(
            "chave",
            binding.edtSearch.text.toString(),
        )

        startActivity(intent)
    }


    private fun getLocaleTime(): String {
        val dateFormat: SimpleDateFormat = if (Locale.getDefault().displayLanguage == "English") {
            SimpleDateFormat("hh:mm a - MM/dd/yyyy", Locale.getDefault())
        } else {
            SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
        }
        return dateFormat.format(Calendar.getInstance().time)

    }
}