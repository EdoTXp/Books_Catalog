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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.gestores.GestorNotification;
import com.BiblioLivro.criarlivros.gestores.GestorVibrator;
import com.BiblioLivro.criarlivros.storage.DatabaseHelper;
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;

import java.util.Objects;


public class TelaCadastrar extends AppCompatActivity implements View.OnClickListener {

    //ATRIBUTOS
    private EditText edtTitleBook;
    private EditText edtAuthorBook;
    private EditText edtYearBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Método para setar o tema da activity ao iniciar
        SharedPreferencesTheme sharedPreferencesTheme = new SharedPreferencesTheme(this);
        sharedPreferencesTheme.setTheme();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tela_cadastrar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_tela_cadastrar));

        //ATRIBUTO LOCAL
        final Button btnSave = findViewById(R.id.btnSalvar);

        edtTitleBook = findViewById(R.id.edtTitulo);
        edtAuthorBook = findViewById(R.id.edtAutor);
        edtYearBook = findViewById(R.id.edtAno);

        edtYearBook.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    onClick(btnSave);
                    return true;
                }
                return false;
            }
        });
        btnSave.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {

        /*
         * Se todos os campos forem preenchidos,
         * será cadastrado no banco de dados o livro, em seguida,
         * será impressa uma notificação
         * indacando que o livro foi cadastrado com sucesso.
         */

        if (v.getId() == R.id.btnSalvar) {
            if (!(edtTitleBook.getText().toString().equals("") || edtAuthorBook.getText().toString().equals("") || edtYearBook.getText().toString().equals(""))) {
                //Criação do ContentValues e preenchendo com os valores definidos pelo usuário.
                ContentValues cv = new ContentValues();
                cv.put("titulo", edtTitleBook.getText().toString());
                cv.put("autor", edtAuthorBook.getText().toString());
                cv.put("ano", edtYearBook.getText().toString());

                /* Criando a conexão com o database e passando o ContentValues para a inserção de dados.
                 * Se a operação ocorrer com sucesso, será imprimido a notificação e será limpado os campos.
                 * Caso contrário, será exibida uma mensagem de erro.
                 */

                DatabaseHelper dh = new DatabaseHelper(this);

                if (dh.inserir(cv) > 0) {
                    //imprimir a notificação
                    printNotification();

                    // limpando os campos
                    edtTitleBook.setText("");
                    edtYearBook.setText("");
                    edtAuthorBook.setText("");
                    edtTitleBook.requestFocus();

                } else {
                    String msg = getString(R.string.error_msg);
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }

            } else {
                GestorVibrator.Vibrate(100L, v.getContext());
                Toast.makeText(this, getString(R.string.incomplete_msg), Toast.LENGTH_LONG).show();
            }

        }
    }

    private void printNotification() {

        String bodyTextNotification = getString(R.string.Notification_Text_1)
                .concat(" \"")
                .concat(edtTitleBook.getText().toString()).concat("\" ")
                .concat(getString(R.string.Notification_Text_2));

        GestorNotification notification = new GestorNotification
                (
                        this,
                        R.drawable.transparent_icon_app,
                        getString(R.string.Notification_Title),
                        bodyTextNotification,
                        2
                );

        notification.setColor(Color.WHITE);
        notification.setDurationVibrate(new long[]{0L, 200L, 150L, 200L});
        notification.setSound
                (
                        Uri.parse("android.resource://"
                                + getBaseContext().getPackageName()
                                + "/"
                                + R.raw.recycle)
                );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notification.createChannelId
                    (
                            getString(R.string.Notification_Channel),
                            getString(R.string.Notification_Description),
                            getSystemService(NotificationManager.class)
                    );
        }
        notification.printNotification();
    }
}
