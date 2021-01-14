/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.gestores.GestorNotification;
import com.BiblioLivro.criarlivros.gestores.GestorVibrator;
import com.BiblioLivro.criarlivros.storage.DatabaseHelper;
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class TelaCadastrar extends AppCompatActivity implements View.OnClickListener {

    //ATRIBUTOS
    private EditText edtTitleBook;
    private EditText edtAuthorBook;
    private EditText edtYearBook;
    private boolean clickEventIsClicked = false; //variável utilizada para saber se já foi clicado mais de uma vez

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Método para setar o tema da activity ao iniciar
        SharedPreferencesTheme sharedPreferencesTheme = new SharedPreferencesTheme(this);
        sharedPreferencesTheme.setAppTheme();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tela_cadastrar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_tela_cadastrar));

        //ATRIBUTO LOCAL
        final Button btnSave = findViewById(R.id.btnSalvar);

        edtTitleBook = findViewById(R.id.edtTitulo);
        edtAuthorBook = findViewById(R.id.edtAutor);
        edtYearBook = findViewById(R.id.edtAno);

        //EVENTOS
        edtYearBook.setOnKeyListener(new View.OnKeyListener() {
            /*
             * Método para fazer o cadastro usando o teclado do dispositivo
             * */
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*
                 * Ao envocar o evento, será feito uma filtragem capturando somente a ação ACTION_UP.
                 * Em seguida, será capturada a tecla enter para chamar a ação de onClick*/
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        onClick(btnSave);
                        return true;
                    }
                }
                return false;
            }
        });


        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnSalvar && !clickEventIsClicked) {
            //clickEventIsClicked vira true
            clickEventIsClicked = true;

            //Valores para saber se o campo respectivo estiver vazio
            boolean title = checkEditText(edtTitleBook),
                    author = checkEditText(edtAuthorBook),
                    year = checkEditText(edtYearBook);

            if (title && author && year) {
                //Criação do ContentValues e preenchendo com os valores definidos pelo usuário e limpando todos os espaços em branco.
                ContentValues cv = new ContentValues();
                cv.put("titulo", edtTitleBook.getText().toString());
                cv.put("autor", edtAuthorBook.getText().toString());
                cv.put("ano", edtYearBook.getText().toString());

                /* Criando a conexão com o database e passando o ContentValues para a inserção de dados.
                 * Se a operação ocorrer com sucesso, será imprimido a notificação e será limpado os campos.
                 * Caso contrário, será exibida uma mensagem de erro.
                 */

                DatabaseHelper dh = new DatabaseHelper(this);

                if (dh.insert(cv) > 0) {
                    //imprimir a notificação
                    printNotification();

                    // limpando os campos e passando o foco ao edtTitleBook
                    edtTitleBook.setText("");
                    edtAuthorBook.setText("");
                    edtYearBook.setText("");
                    edtTitleBook.requestFocus();

                } else {
                    String msg = getString(R.string.error_msg);
                    GestorVibrator.Vibrate(100L, v.getContext());
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }

            } else {
                GestorVibrator.Vibrate(100L, v.getContext());
            }

            // handler utilizado para dar um delay ao evento de clicar para previnir multiplos cliques
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    clickEventIsClicked = false;
                }
            }, 1000);
        }
    }

    private boolean checkEditText(@NotNull EditText edt) {
        /*Este método verifica se o EditText edt, passado como parametro, está vazio ou não.
         * Caso estiver vazio, o background do edt será alterado para o error_border e
         *  será mostrado uma animação e um text dizendo que o campo está vazio
         * No outro caso será resetado o edt com os valores padrão*/

        if (edt.getText().toString().isEmpty()) {
            //criação da animação
            ObjectAnimator errorAnimation = ObjectAnimator.ofFloat(edt, "translationX", 20f);
            errorAnimation.setDuration(150); // a duração total da animação será de 150 millisegundos
            errorAnimation.setRepeatMode(ValueAnimator.REVERSE); // volta no local padrão
            errorAnimation.setRepeatCount(3); // repetir a animação 3 vezes

            edt.setHint(getString(R.string.hint_error));
            edt.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_error));
            errorAnimation.start();
            GestorVibrator.Vibrate(200, edt.getContext());

            return false;
        } else {
            edt.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border));

            if (edt.getId() == R.id.edtTitulo)
                edt.setHint(getString(R.string.hint_titulo));
            else if (edt.getId() == R.id.edtAutor)
                edt.setHint(getString(R.string.hint_autor));
            else
                edt.setHint(getString(R.string.hint_ano));
            return true;
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
