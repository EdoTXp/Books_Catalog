/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.libry_book.books_catalog.views.activities

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
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.MenuProvider
import com.libry_book.books_catalog.BuildConfig
import com.libry_book.books_catalog.R
import com.libry_book.books_catalog.services.app_services.AlertDialogService
import com.libry_book.books_catalog.services.app_services.VibratorService
import com.libry_book.books_catalog.services.email_service.EmailService
import com.libry_book.books_catalog.storage.DatabaseHelperImpl
import com.libry_book.books_catalog.storage.SharedPreferencesTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var rdgSearchBy: RadioGroup
    private lateinit var edtSearch: EditText
    private lateinit var btnInsert: Button
    private lateinit var btnSearch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        // Setting the theme
        val preferencesTheme = SharedPreferencesTheme(this)
        preferencesTheme.setAppTheme()

        super.onCreate(savedInstanceState)
        // Starting the splash screen
        installSplashScreen()

        setContentView(R.layout.activity_main)
        bindingViews()
        initMenuBar()


        edtSearch.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
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
        btnInsert.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    InsertBookActivity::class.java,
                ),
            )
        }

        btnSearch.setOnClickListener { sendSearch() }

        rdgSearchBy.setOnCheckedChangeListener { _, checkedId ->
            edtSearch.isEnabled = true
            edtSearch.setText("")
            when (checkedId) {
                R.id.rbPesquisarPorAno -> {
                    edtSearch.inputType = InputType.TYPE_CLASS_NUMBER
                    edtSearch.filters = arrayOf<InputFilter>(LengthFilter(4))
                    edtSearch.setHint(R.string.hint_ano)
                    edtSearch.contentDescription = getString(R.string.txt_AccessDescriptionYear)
                }

                R.id.rbPesquisarPorAutor -> {
                    edtSearch.filters = arrayOf<InputFilter>(LengthFilter(100))
                    edtSearch.setHint(R.string.hint_autor)
                    edtSearch.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    edtSearch.contentDescription = getString(R.string.txt_AccessDescriptionAuthor)
                }

                R.id.rbPesquisarPorTitulo -> {
                    edtSearch.filters = arrayOf<InputFilter>(LengthFilter(100))
                    edtSearch.setHint(R.string.hint_titulo)
                    edtSearch.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    edtSearch.contentDescription = getString(R.string.txt_AccessDescriptionTitle)
                }

                R.id.rbPesquisarPorTodos -> {
                    edtSearch.isEnabled = false
                    edtSearch.hint = ""
                    edtSearch.filters = arrayOf<InputFilter>(LengthFilter(0))
                    edtSearch.inputType = InputType.TYPE_NULL
                    edtSearch.contentDescription = getString(R.string.txt_AccessDescriptionAll)
                }
            }
        }
    }

    private fun bindingViews() {
        btnInsert = findViewById<Button>(R.id.btnInsert)
        btnSearch = findViewById<Button>(R.id.btnSearch)
        rdgSearchBy = findViewById(R.id.rdgPesquisarPor)
        edtSearch = findViewById(R.id.edtPesquisar)
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
                                        getString(R.string.email_subject) + "#" + random.toString()

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
                                    VibratorService.vibrate(
                                        this@MainActivity,
                                        100L,
                                    )
                                    Toast.makeText(
                                        this@MainActivity,
                                        getString(R.string.email_notextinsert),
                                        Toast.LENGTH_LONG,
                                    ).show()
                                }
                            },
                        )
                    }
                    // displaying the app version
                    R.id.menu_app_version -> {
                        VibratorService.vibrate(
                            this@MainActivity,
                            100L,
                        )
                        Toast.makeText(
                            this@MainActivity,
                            BuildConfig.VERSION_NAME,
                            Toast.LENGTH_LONG,
                        ).show()

                    }

                    else -> return onOptionsItemSelected(menuItem)
                }
                return true
            }
        })
    }

    private fun sendSearch() {
        if (
            edtSearch.text.toString().isEmpty()
            &&
            rdgSearchBy.checkedRadioButtonId != R.id.rbPesquisarPorTodos
        ) {
            VibratorService.vibrate(this, 100L)
            Toast.makeText(
                this,
                getString(R.string.FieldEmpty),
                Toast.LENGTH_LONG,
            ).show()
            return
        }

        //Criar a nova Intent para a nova Tela Pesquisar se existir algum dado
        val intent = Intent(
            this,
            SearchActivity::class.java,
        )
        if (DatabaseHelperImpl(this).tableExist()) {

            intent.putExtra(
                "tipo",
                rdgSearchBy.checkedRadioButtonId,
            )
            intent.putExtra(
                "chave",
                edtSearch.text.toString(),
            )
        } else {
            VibratorService.vibrate(
                this,
                100L,
            )

            Toast.makeText(
                this,
                getString(R.string.FieldNotFound),
                Toast.LENGTH_LONG,
            ).show()
            return
        }
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