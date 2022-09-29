/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */
package com.BiblioLivro.criarlivros.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme
import android.view.WindowManager
import com.BiblioLivro.criarlivros.R
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets

import java.util.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Selecionando o tema
        val sharedPreferencesTheme = SharedPreferencesTheme(this)
        sharedPreferencesTheme.setAppTheme()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        }
        else {
            supportActionBar!!.hide()
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setContentView(R.layout.activity_splashscreen)

        // tempo percorrido, em ms, antes de lançar a tela principal
        val splashTimeOut = 4000L
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            val homeIntent = Intent(this@SplashScreen, TelaPrincipal::class.java)
            startActivity(homeIntent)
            finish()
        }, splashTimeOut)
    }
}
