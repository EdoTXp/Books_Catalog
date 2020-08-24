/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import com.BiblioLivro.criarlivros.BuildConfig;
import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.gestores.GestorVibrator;
import com.BiblioLivro.criarlivros.storage.DatabaseHelper;
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TelaPrincipal extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    //ATRIBUTOS
    private RadioGroup rdgPesquisarPor;
    private EditText edtPesquisar;
    private SharedPreferencesTheme preferencesTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Configurando o tema
        preferencesTheme = new SharedPreferencesTheme(this);
        preferencesTheme.setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        //ATRIBUTOS LOCAIS
        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        final Button btnPesquisar = findViewById(R.id.btnPesquisar);
        rdgPesquisarPor = findViewById(R.id.rdgPesquisarPor);
        edtPesquisar = findViewById(R.id.edtPesquisar);

        //EVENTOS
        edtPesquisar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    onClick(btnPesquisar);
                    return true;
                }
                return false;
            }
        });
        btnCadastrar.setOnClickListener(this);
        btnPesquisar.setOnClickListener(this);
        rdgPesquisarPor.setOnCheckedChangeListener(this);
    }


    // reinicar a Activity toda vez que for executado o evento onBackPressed() em outra activity
    @Override
    protected void onRestart() {
        preferencesTheme.setTheme();
        recreate();
        super.onRestart();
    }

    @Override
    public void onClick(View v) {
        // criação do Intent para iniciar uma nova Activity
        Intent it;

        switch (v.getId()) {

            // Criar a Intent para a nova Tela Cadastrar
            case R.id.btnCadastrar:
                it = new Intent(this, TelaCadastrar.class);
                break;

            /*
             * Abrir a nova Tela Pesquisar se o campo "edtPesquisar" estiver preenchido.
             * Caso contrário será exibido na tela um Toast pedindo pra preencher o campo vazio.
             * Caso for selecionado o radiobutton "rbPesquisarPorTodos" não será necessário preencher algum campo.
             * */
            case R.id.btnPesquisar:

                // verificando se algum campo está vazio e radiobutton não for "rbPesquisarPorTodos"
                if (edtPesquisar.getText().toString().equals("") && !(rdgPesquisarPor.getCheckedRadioButtonId() == R.id.rbPesquisarPorTodos)) {
                    GestorVibrator.Vibrate(100L, v.getContext());
                    Toast.makeText(this, getString(R.string.FieldEmpty), Toast.LENGTH_LONG).show();
                    return;
                }
                //Criar a nova Intent para a nova Tela Pesquisar se existir algum dado
                if (new DatabaseHelper(this).tableIsExist()) {
                    it = new Intent(this, TelaPesquisar.class);
                    it.putExtra("tipo", rdgPesquisarPor.getCheckedRadioButtonId());
                    it.putExtra("chave", edtPesquisar.getText().toString());
                }
                /* Se a lista estiver vazia, será imprimido na tela que não foi encontrado
                 * ou registrado nenhum campo.
                 */
                else {
                    GestorVibrator.Vibrate(100L, this);
                    Toast.makeText(this, getString(R.string.FieldNotFound), Toast.LENGTH_LONG).show();
                    return;
                }

                break;

            default:
                return;
        }
        //Iniciando a nova Intent
        startActivity(it);

    }

    //Preenchimento do menuBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menubar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                //Iniciando a nova Tela Impostações
                Intent it = new Intent(this, TelaImpostacoes.class);
                startActivity(it);
                return true;

            case R.id.menu_feedback:

                // Criação do AlertDialog para cadastrar o e-mail
                AlertDialog.Builder emailDialog = new AlertDialog.Builder(this);

                /*
                 * se o preferencesTheme retornar o valor "true",
                 * o emailDialog receberá o tema escuro.
                 * Caso contrário, receberá o tema claro
                 * */
              /*  if (preferencesTheme.getButton() == preferencesTheme.THEME_LIGHT || preferencesTheme.getButton() == preferencesTheme.THEME_BATTERY)
                    emailDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                else
                    emailDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);*/

                // Adição do icone e o título do email dialog
                emailDialog.setIcon(R.drawable.transparent_icon_app);
                emailDialog.setTitle(getString(R.string.email_title));


                final EditText emailBodyText = new EditText(this);

                // Configurando o emailBodyText
                emailBodyText.setInputType(InputType.TYPE_CLASS_TEXT);
                emailBodyText.setSingleLine(false);
                emailBodyText.setHint(getString(R.string.email_textHint));
                emailBodyText.setHintTextColor(getResources().getColor(R.color.colortexthint));
                emailBodyText.setTextColor(getResources().getColor(R.color.colorPrimaryText));
                emailBodyText.setGravity(Gravity.START | Gravity.TOP);
                emailBodyText.setHorizontalScrollBarEnabled(false);

                //Adicionando o emailBodyText ao emailDialog
                emailDialog.setView(emailBodyText);

                //Configurando o botão positivo
                emailDialog.setPositiveButton(getString(R.string.email_btn_send), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /*Se o "emailBodyText" não for vazio,
                         * Será criado o cabeçario do e-mail com um título,
                         *  um número random para o código da messagem.
                         * O corpo da mensagem com o "emailBodyText" juntamente com a data "local" do dispositivo*/
                        if (!(emailBodyText.getText().toString().equals(""))) {

                            //geração do número random
                            long random = (long) (Math.random() * 1.0E14D + 1.0E9D);

                            //montando o cabeçario
                            String subject = getString(R.string.email_subject).concat("#").concat(Long.toString(random));

                            // receber a data local
                            String time = getLocaleTime();

                            // montando o e-mail e escolher qual app para enviar
                            ShareCompat.IntentBuilder.from(TelaPrincipal.this)
                                    .setType("message/rfc822")
                                    .addEmailTo("edoardofabriziodeiovanna@hotmail.com")
                                    .setSubject(subject)
                                    .setText
                                            (
                                                    emailBodyText.getText().toString()
                                                            .concat("\n\n")
                                                            .concat(getString(R.string.email_timegenerated))
                                                            .concat(time)
                                            )
                                    .setChooserTitle(getString(R.string.email_chooseapp))
                                    .startChooser();

                        } // caso o emailBodyText for vazio sera impressa uma mensagem mais uma vibração
                        else {
                            GestorVibrator.Vibrate(100L, getBaseContext());
                            Toast.makeText(getBaseContext(), getString(R.string.email_notextinsert), Toast.LENGTH_LONG).show();
                        }
                    }

                });
                emailDialog.setNegativeButton(getString(R.string.email_btn_cancel), null);
                emailDialog.show();
                return true;

            // exibindo a versão do app
            case R.id.menu_app_version:
                GestorVibrator.Vibrate(100L, this);
                Toast.makeText(this, BuildConfig.VERSION_NAME, Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // método utilizado para escolher o formato da data em base o local do dispositivo
    @NotNull
    private String getLocaleTime() {
        SimpleDateFormat dateFormat;

        if (Locale.getDefault().getDisplayLanguage().equals("English")) {
            dateFormat = new SimpleDateFormat("hh:mm a - MM/dd/yyyy", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
        }

        return dateFormat.format(Calendar.getInstance().getTime());
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        edtPesquisar.setEnabled(true);
        edtPesquisar.setText("");

        switch (checkedId) {
            case R.id.rbPesquisarPorAno:
                edtPesquisar.setInputType(InputType.TYPE_CLASS_NUMBER);
                edtPesquisar.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                edtPesquisar.setHint(R.string.hint_ano);
                edtPesquisar.setContentDescription(getString(R.string.txt_AccessDescriptionYear));
                break;

            case R.id.rbPesquisarPorAutor:
                edtPesquisar.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
                edtPesquisar.setHint(R.string.hint_autor);
                edtPesquisar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                edtPesquisar.setContentDescription(getString(R.string.txt_AccessDescriptionAuthor));
                break;

            case R.id.rbPesquisarPorTitulo:
                edtPesquisar.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
                edtPesquisar.setHint(R.string.hint_titulo);
                edtPesquisar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                edtPesquisar.setContentDescription(getString(R.string.txt_AccessDescriptionTitle));
                break;

            case R.id.rbPesquisarPorTodos:
                edtPesquisar.setEnabled(false);
                edtPesquisar.setHint("");
                edtPesquisar.setFilters(new InputFilter[]{new InputFilter.LengthFilter(0)});
                edtPesquisar.setInputType(InputType.TYPE_NULL);
                edtPesquisar.setContentDescription(getString(R.string.txt_AccessDescriptionAll));
                break;

            default:
                break;
        }
    }

}
