/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.libry_book.books_catalog.views.activities

import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.libry_book.books_catalog.R
import com.libry_book.books_catalog.gestores.GestorVibrator
import com.libry_book.books_catalog.storage.SharedPreferencesTheme
import java.io.File
import java.util.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    // ATRIBUTOS
    private lateinit var radioGroupLanguage: RadioGroup
    private lateinit var radioGroupTheme: RadioGroup
    private lateinit var preferencesTheme: SharedPreferencesTheme
    override fun onCreate(savedInstanceState: Bundle?) {

        /* Ao criar a Activity "TelaImpostacoes",
         *  será colocado o tema em base as preferências salvas
         *  no objeto "preferencesTheme".
         * */
        preferencesTheme = SharedPreferencesTheme(this)
        preferencesTheme.setAppTheme()

        // criação da Activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_impostacoes)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Preenchendo os objetos
        radioGroupLanguage = findViewById(R.id.rg_language)
        radioGroupTheme = findViewById(R.id.rg_theme)
        val clearDataButton = findViewById<Button>(R.id.btn_clear_data)
        checkedRadioButtonByTheme()
        defaultLanguage


        // Adicionado os eventos de click
        clearDataButton.setOnClickListener(this)
        radioGroupTheme.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            /*
             * Aplicar o tema com base na escolha do RadioButton
             *  */when (checkedId) {
            R.id.rb_lightTheme -> {
                preferencesTheme.checkedButton = SharedPreferencesTheme.THEME_LIGHT
            }

            R.id.rb_darkTheme -> {
                preferencesTheme.checkedButton = SharedPreferencesTheme.THEME_DARK
            }

            R.id.rb_batteryTheme -> {
                preferencesTheme.checkedButton = SharedPreferencesTheme.THEME_BATTERY
            }

            R.id.rb_systemTheme -> {
                preferencesTheme.checkedButton = SharedPreferencesTheme.THEME_SYSTEM
            }

            else -> {
                return@setOnCheckedChangeListener
            }
        }
            // depois de selecionar o tema, será envocado o método setTheme()
            preferencesTheme.setAppTheme()
        }
    }

    private fun checkedRadioButtonByTheme() {
        /* Ao iniciar a activity,
         * os radio buttons serão preenchidos de acordo com o que foi salvo no preferencesTheme
         */
        when (preferencesTheme.checkedButton) {
            1 -> radioGroupTheme.check(R.id.rb_darkTheme)
            2 -> radioGroupTheme.check(R.id.rb_systemTheme)
            3 -> radioGroupTheme.check(R.id.rb_batteryTheme)
            0 -> radioGroupTheme.check(R.id.rb_lightTheme)
            else -> radioGroupTheme.check(R.id.rb_lightTheme)
        }
    }

    override fun onClick(v: View) {
        /*
         * Creando um diálogo para escolher se o usuário quer realmente apagar todos os dados
         *
         * */
        if (v.id == R.id.btn_clear_data) {
            AlertDialog.Builder(v.context)
                .setTitle(R.string.btn_clear_data)
                .setMessage(R.string.alert_dialog_message)
                .setIcon(R.drawable.transparent_icon_app)
                .setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int ->
                    clearApplicationData()
                    Toast.makeText(baseContext, getString(R.string.success_msg), Toast.LENGTH_LONG)
                        .show()
                    GestorVibrator.vibrate(100L, baseContext)
                }
                .setNegativeButton(R.string.no, null)
                .create()
                .show()
        }
    }

    private fun clearApplicationData() {
        val cacheDirectory = cacheDir
        val applicationDirectory = File(Objects.requireNonNull(cacheDirectory.parent))
        if (applicationDirectory.exists()) {
            val fileNames = applicationDirectory.list()!!
            for (fileName in fileNames) {
                if (fileName != "lib") {
                    deleteFile(File(applicationDirectory, fileName))
                }
            }
        }
    }

    private val defaultLanguage: Unit
        get() {
            val locale: Locale = Resources.getSystem().configuration.locales[0]

            when (locale.language) {
                "en" -> radioGroupLanguage.check(R.id.rb_english)
                "it" -> radioGroupLanguage.check(R.id.rb_italy)
                "pt" -> radioGroupLanguage.check(R.id.rb_portuguese)
                else -> radioGroupLanguage.check(R.id.rb_english)
            }
        }

    companion object {
        private fun deleteFile(file: File?): Boolean {
            var deletedAll = true
            if (file != null) {
                if (file.isDirectory) {
                    val children = file.list()!!
                    for (child in children) {
                        deletedAll = deleteFile(File(file, child)) && deletedAll
                    }
                } else {
                    deletedAll = file.delete()
                }
            }
            return deletedAll
        }
    }
}