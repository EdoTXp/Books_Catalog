/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.activities;

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
    private RadioGroup rdgSearchBy;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Configurando o tema
        SharedPreferencesTheme preferencesTheme = new SharedPreferencesTheme(this);
        preferencesTheme.setAppTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        //ATRIBUTOS LOCAIS
        Button btnRegister = findViewById(R.id.btnCadastrar);
        final Button btnSearch = findViewById(R.id.btnPesquisar);
        rdgSearchBy = findViewById(R.id.rdgPesquisarPor);
        edtSearch = findViewById(R.id.edtPesquisar);

        //EVENTOS
        /*
         * Método para fazer a pesquisa usando o teclado do dispositivo
         * */
        edtSearch.setOnKeyListener((v, keyCode, event) -> {
            /*
             * Ao envocar o evento, será feito uma filtragem capturando somente a ação ACTION_UP.
             * Em seguida, será capturada a tecla enter para chamar a ação de onClick*/
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    onClick(btnSearch);
                    return true;
                }
            }
            return false;
        });
        btnRegister.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        rdgSearchBy.setOnCheckedChangeListener(this);


    }


    @Override
    public void onClick(View v) {
        // criação do Intent para iniciar uma nova Activity
        Intent it = null;

        int id = v.getId();// Criar a Intent para a nova Tela Cadastrar
        if (id == R.id.btnCadastrar) {
            it = new Intent(this, TelaCadastrar.class);

            /*
             * Abrir a nova Tela Pesquisar se o campo "edtPesquisar" estiver preenchido.
             * Caso contrário será exibido na tela um Toast pedindo pra preencher o campo vazio.
             * Caso for selecionado o radiobutton "rbPesquisarPorTodos" não será necessário preencher algum campo.
             * */
        } else if (id == R.id.btnPesquisar) {// verificando se algum campo está vazio e o radiobutton não for "rbPesquisarPorTodos"
            if (edtSearch.getText().toString().equals("") && !(rdgSearchBy.getCheckedRadioButtonId() == R.id.rbPesquisarPorTodos)) {
                GestorVibrator.Vibrate(100L, v.getContext());
                Toast.makeText(this, getString(R.string.FieldEmpty), Toast.LENGTH_LONG).show();
                return;
            }

            //Criar a nova Intent para a nova Tela Pesquisar se existir algum dado
            if (new DatabaseHelper(this).tableIsExist()) {
                it = new Intent(this, TelaPesquisar.class);
                it.putExtra("tipo", rdgSearchBy.getCheckedRadioButtonId());
                it.putExtra("chave", edtSearch.getText().toString());
            }
            /* Se a lista estiver vazia, será imprimido na tela que não foi encontrado
             * ou registrado nenhum campo.
             */
            else {
                GestorVibrator.Vibrate(100L, this);
                Toast.makeText(this, getString(R.string.FieldNotFound), Toast.LENGTH_LONG).show();
                return;
            }
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
        int itemId = item.getItemId();

        if (itemId == R.id.menu_settings) {//Iniciando a nova Tela Impostações
            Intent it = new Intent(this, TelaImpostacoes.class);
            startActivity(it);
            return true;
        } else if (itemId == R.id.menu_feedback) {// Criação do AlertDialog para cadastrar o e-mail
            AlertDialog.Builder emailDialog = new AlertDialog.Builder(this);

            // Adição do icone e o título do email dialog
            emailDialog.setIcon(R.drawable.transparent_icon_app);
            emailDialog.setTitle(getString(R.string.email_title));


            final EditText emailBodyText = new EditText(this);

            // Configurando o emailBodyText
            emailBodyText.setInputType(InputType.TYPE_CLASS_TEXT);
            emailBodyText.setSingleLine(false);
            emailBodyText.setHint(getString(R.string.email_textHint));
            emailBodyText.setHintTextColor(getResources().getColor(R.color.colorTextHint));
            emailBodyText.setTextColor(getResources().getColor(R.color.colorPrimaryText));
            emailBodyText.setGravity(Gravity.START | Gravity.TOP);
            emailBodyText.setHorizontalScrollBarEnabled(false);

            //Adicionando o emailBodyText ao emailDialog
            emailDialog.setView(emailBodyText);

            //Configurando o botão positivo
            emailDialog.setPositiveButton(getString(R.string.email_btn_send), (dialog, which) -> {

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
                    ShareCompat.IntentBuilder shareEmail = new ShareCompat.IntentBuilder(emailDialog.getContext())
                            .setType("message/rfc822")
                            .addEmailTo(getString(R.string.developer_email))
                            .setSubject(subject)
                            .setText(
                                    emailBodyText.getText().toString()
                                            .concat("\n\n")
                                            .concat(getString(R.string.email_timegenerated))
                                            .concat(time)
                            )
                            .setChooserTitle(getString(R.string.email_chooseapp));
                    shareEmail.startChooser();

                } // caso o emailBodyText for vazio sera impressa uma mensagem mais uma vibração
                else {
                    GestorVibrator.Vibrate(100L, getBaseContext());
                    Toast.makeText(getBaseContext(), getString(R.string.email_notextinsert), Toast.LENGTH_LONG).show();
                }
            }).setNegativeButton(getString(R.string.email_btn_cancel), null);
            emailDialog.show();
            return true;

            // exibindo a versão do app
        } else if (itemId == R.id.menu_app_version) {
            GestorVibrator.Vibrate(100L, this);
            Toast.makeText(this, BuildConfig.VERSION_NAME, Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        edtSearch.setEnabled(true);
        edtSearch.setText("");

        if (checkedId == R.id.rbPesquisarPorAno) {
            edtSearch.setInputType(InputType.TYPE_CLASS_NUMBER);
            edtSearch.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            edtSearch.setHint(R.string.hint_ano);
            edtSearch.setContentDescription(getString(R.string.txt_AccessDescriptionYear));
        } else if (checkedId == R.id.rbPesquisarPorAutor) {
            edtSearch.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            edtSearch.setHint(R.string.hint_autor);
            edtSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            edtSearch.setContentDescription(getString(R.string.txt_AccessDescriptionAuthor));
        } else if (checkedId == R.id.rbPesquisarPorTitulo) {
            edtSearch.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            edtSearch.setHint(R.string.hint_titulo);
            edtSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            edtSearch.setContentDescription(getString(R.string.txt_AccessDescriptionTitle));
        } else if (checkedId == R.id.rbPesquisarPorTodos) {
            edtSearch.setEnabled(false);
            edtSearch.setHint("");
            edtSearch.setFilters(new InputFilter[]{new InputFilter.LengthFilter(0)});
            edtSearch.setInputType(InputType.TYPE_NULL);
            edtSearch.setContentDescription(getString(R.string.txt_AccessDescriptionAll));
        }
    }

}