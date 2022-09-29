/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Selecionando o tema
        SharedPreferencesTheme sharedPreferencesTheme = new SharedPreferencesTheme(this);
        sharedPreferencesTheme.setAppTheme();

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags
                (
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN
                );
        setContentView(R.layout.activity_splashscreen);

        // tempo percorrido, em ms, antes de lançar a tela principal
        int SPLASH_TIME_OUT = 4000;

        new Handler().postDelayed(() -> {
            Intent homeIntent = new Intent(SplashScreen.this, TelaPrincipal.class);
            startActivity(homeIntent);
            finish();
        }, SPLASH_TIME_OUT);

    }


}
