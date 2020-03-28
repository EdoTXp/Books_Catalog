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
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;

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

        preferencesTheme = new SharedPreferencesTheme(this);
        if (preferencesTheme.getNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        //ATRIBUTOS LOCAIS
        Button btnCadastrar = findViewById(R.id.btnCadastrar);
        Button btnPesquisar = findViewById(R.id.btnPesquisar);
        rdgPesquisarPor = findViewById(R.id.rdgPesquisarPor);
        edtPesquisar = findViewById(R.id.edtPesquisar);

        btnCadastrar.setOnClickListener(this);
        btnPesquisar.setOnClickListener(this);

        rdgPesquisarPor.setOnCheckedChangeListener(this);

    }


    @Override
    protected void onRestart() {
       finish();
       startActivity(getIntent());
       super.onRestart();
    }

    @Override
    public void onClick(View v) {
        Intent it = null;

        switch (v.getId()) {
            // Abrir a nova Tela Cadastrar


            case R.id.btnCadastrar:
                it = new Intent(this, TelaCadastrar.class);
                break;

            case R.id.btnPesquisar:

                if (edtPesquisar.getText().toString().equals("") && !(rdgPesquisarPor.getCheckedRadioButtonId() == R.id.rbPesquisarPorTodos)) {
                    GestorVibrator.Vibrate(100L, this);
                    Toast.makeText(this, getString(R.string.FieldEmpty), Toast.LENGTH_LONG).show();
                    return;
                }

                it = new Intent(this, TelaPesquisar.class);
                it.putExtra("tipo", rdgPesquisarPor.getCheckedRadioButtonId());
                it.putExtra("chave", edtPesquisar.getText().toString());
                break;
        }

        startActivity(it);

    }

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

                Intent it = new Intent(this, TelaImpostacoes.class);
                startActivity(it);
                return true;

            case R.id.menu_feedback:

                // Criação do AlertDialog para cadastrar o e-mail
                AlertDialog.Builder emaildialog;

                /*
                 * se o preferencesTheme retornar o valor "true",
                 * o emaildialog receberá o tema escuro.
                 * Caso contrário, receberá o tema claro*/
                if (preferencesTheme.getNightModeState())
                    emaildialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
                else
                    emaildialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                // Adição do icone e o título do email dialog
                emaildialog.setIcon(R.drawable.iconapp);
                emaildialog.setTitle(getString(R.string.email_title));


                final EditText emailtext = new EditText(this);
                final AppCompatActivity activity = this;

                //
                emailtext.setInputType(InputType.TYPE_CLASS_TEXT);
                emailtext.setSingleLine(false);
                emailtext.setHint(getString(R.string.email_textHint));

                //TODO trovare altri modi per settare il colore
                emailtext.setHintTextColor((preferencesTheme.getNightModeState()) ? getResources().getColor(R.color.nightcolortexthint) : getResources().getColor(R.color.colortexthint));
                emailtext.setTextColor((preferencesTheme.getNightModeState()) ? getResources().getColor(R.color.nightcolorPrimaryText) : getResources().getColor(R.color.colorPrimaryText));

                emailtext.setGravity(Gravity.START | Gravity.TOP);
                emailtext.setHorizontalScrollBarEnabled(false);
                emaildialog.setView(emailtext);

                emaildialog.setPositiveButton(getString(R.string.email_btn_send), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!(emailtext.getText().toString().equals(""))) {

                            long random = (long) (Math.random() * 1.0E14D + 1.0E9D);
                            String subject = getString(R.string.email_subject).concat("#").concat(Long.toString(random));

                            SimpleDateFormat dateFormat;

                            if (Locale.getDefault().getDisplayLanguage().equals("English")) {
                                dateFormat = new SimpleDateFormat("hh:mm a - MM/dd/yyyy", Locale.getDefault());
                            } else {
                                dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
                            }

                            String time = dateFormat.format(Calendar.getInstance().getTime());

                            ShareCompat.IntentBuilder.from(activity)
                                    .setType("message/rfc822")
                                    .addEmailTo("edoardofabriziodeiovanna@hotmail.com")
                                    .setSubject(subject)
                                    .setText(emailtext.getText().toString().concat("\n\n").concat(getString(R.string.email_timegenerated)).concat(time))
                                    .setChooserTitle(getString(R.string.email_chooseapp))
                                    .startChooser();

                        } else {
                            GestorVibrator.Vibrate(100L, activity);
                            Toast.makeText(activity, getString(R.string.email_notextinsert), Toast.LENGTH_LONG).show();
                        }
                    }

                });

                emaildialog.setNegativeButton(getString(R.string.email_btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                emaildialog.show();
                return true;

            case R.id.menu_app_version:
                GestorVibrator.Vibrate(100L, this);
                Toast.makeText(this, BuildConfig.VERSION_NAME, Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
                edtPesquisar.setInputType(InputType.TYPE_CLASS_TEXT);
                edtPesquisar.setContentDescription(getString(R.string.txt_AccessDescriptionAuthor));
                break;

            case R.id.rbPesquisarPorTitulo:
                edtPesquisar.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
                edtPesquisar.setHint(R.string.hint_titulo);
                edtPesquisar.setInputType(InputType.TYPE_CLASS_TEXT);
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
