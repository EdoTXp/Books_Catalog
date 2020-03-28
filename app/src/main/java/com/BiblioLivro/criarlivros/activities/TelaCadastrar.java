/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.activities;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.graphics.Color;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.BiblioLivro.criarlivros.storage.DatabaseHelper;
import com.BiblioLivro.criarlivros.gestores.GestorNotification;
import com.BiblioLivro.criarlivros.gestores.GestorVibrator;
import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;

import java.util.Objects;


public class TelaCadastrar extends AppCompatActivity implements View.OnClickListener {

    //ATRIBUTOS
    private EditText edtTitulo;
    private EditText edtAutor;
    private EditText edtAno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferencesTheme preferencesTheme = new SharedPreferencesTheme(this);
        if (preferencesTheme.getNightModeState()) {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tela_cadastrar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_tela_cadastrar));

        //ATRIBUTOS
        Button btnSalvar = findViewById(R.id.btnSalvar);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtAutor = findViewById(R.id.edtAutor);
        edtAno = findViewById(R.id.edtAno);

        btnSalvar.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnSalvar) {
            if (!(edtTitulo.getText().toString().equals("") || edtAutor.getText().toString().equals("") || edtAno.getText().toString().equals(""))) {

                ContentValues cv = new ContentValues();
                cv.put("titulo", edtTitulo.getText().toString());
                cv.put("autor", edtAutor.getText().toString());
                cv.put("ano", edtAno.getText().toString());

                //imprimir a notificação
                printNotification();


                DatabaseHelper dh = new DatabaseHelper(this);
                String msg;
                if (dh.inserir(cv) > 0) {
                    msg = getString(R.string.success_msg);
                    edtTitulo.setText("");
                    edtAno.setText("");
                    edtAutor.setText("");
                    edtTitulo.requestFocus();
                } else {
                    msg = getString(R.string.error_msg);
                }

                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            } else {
                GestorVibrator.Vibrate(100L, this);
                Toast.makeText(this, getString(R.string.incomplete_msg), Toast.LENGTH_LONG).show();
            }

        }
    }


    private void printNotification() {
        // begin Print Notification
        String text = getString(R.string.Notification_Text_1).concat(" " + edtTitulo.getText().toString()).concat(" " + getString(R.string.Notification_Text_2));

        GestorNotification notification = new GestorNotification(this, R.drawable.iconapp,
                getString(R.string.Notification_Title),
                text
                , 1);


        notification.setColor(Color.WHITE);
        notification.setDurationVibrate(new long[]{0L, 200L, 150L, 200L});
        notification.setSound(Uri.parse("android.resource://"
                + getBaseContext().getPackageName() + "/" + R.raw.recycle));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notification.createChannelId(getString(R.string.Notification_Channel), getString(R.string.Notification_Description), getSystemService(NotificationManager.class));
        }

        notification.printNotification();
        // end Print Notification
    }

}
