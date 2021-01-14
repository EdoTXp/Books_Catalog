/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.activities;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.gestores.GestorVibrator;
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

public class TelaImpostacoes extends AppCompatActivity implements View.OnClickListener {

    // ATRIBUTOS
    private RadioGroup rg_language, rg_Theme;
    private SharedPreferencesTheme preferencesTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Ao criar a Activity "TelaImpostacoes",
         *  será colocado o tema em base as preferências salvas
         *  no objeto "preferencesTheme".
         * */
        preferencesTheme = new SharedPreferencesTheme(this);
        preferencesTheme.setAppTheme();

        // criação da Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_impostacoes);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.menu_settings));

        // Preenchendo os objetos
        rg_language = findViewById(R.id.rg_language);
        rg_Theme = findViewById(R.id.rg_theme);
        Button btn_clearData = findViewById(R.id.btn_clear_data);

        checkedRadioButtonByTheme();
        getDefaultLanguage();


        // Adicionado os eventos de click
        btn_clearData.setOnClickListener(this);
        rg_Theme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    /*
                     * Aplicar o tema com base na escolha do RadioButton
                     *  */
                    case R.id.rb_lightTheme:
                        preferencesTheme.setCheckedButton(preferencesTheme.THEME_LIGHT);
                        break;

                    case R.id.rb_darkTheme:
                        preferencesTheme.setCheckedButton(preferencesTheme.THEME_DARK);
                        break;

                    case R.id.rb_batteryTheme:
                        preferencesTheme.setCheckedButton(preferencesTheme.THEME_BATTERY);
                        break;

                    case R.id.rb_systemTheme:
                        preferencesTheme.setCheckedButton(preferencesTheme.THEME_SYSTEM);
                        break;

                    default:
                        return;
                }
                // depois de selecionar o tema, será envocado o método setTheme()
                preferencesTheme.setAppTheme();
            }
        });

    }

    private void checkedRadioButtonByTheme() {
        /* Ao iniciar a activity,
         * os radio buttons serão preenchidos de acordo com o que foi salvo no preferencesTheme
         */
        switch (preferencesTheme.getCheckedButton()) {
            case 1:
                rg_Theme.check(R.id.rb_darkTheme);
                break;

            case 2:
                rg_Theme.check(R.id.rb_systemTheme);
                break;

            case 3:
                rg_Theme.check(R.id.rb_batteryTheme);
                break;

            default:
            case 0:
                rg_Theme.check(R.id.rb_lightTheme);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        /*
         * Creando um diálogo para escolher se o usuário quer realmente apagar todos os dados
         *
         * */

        if (v.getId() == R.id.btn_clear_data) {
            AlertDialog.Builder clearDataBuilder = new AlertDialog.Builder(v.getContext());

            clearDataBuilder.setTitle(R.string.btn_clear_data);
            clearDataBuilder.setMessage(R.string.alert_dialog_message);
            clearDataBuilder.setIcon(R.drawable.transparent_icon_app);
            clearDataBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clearApplicationData();

                    Toast.makeText(getBaseContext(), getString(R.string.success_msg), Toast.LENGTH_LONG).show();

                    GestorVibrator.Vibrate(100L, getBaseContext());
                }
            });
            clearDataBuilder.setNegativeButton(R.string.no, null);
            clearDataBuilder.show();
        }
    }

    private void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(Objects.requireNonNull(cacheDirectory.getParent()));
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            assert fileNames != null;
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    private static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();

                assert children != null;
                for (String child : children) {
                    deletedAll = deleteFile(new File(file, child)) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    private void getDefaultLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            locale = Resources.getSystem().getConfiguration().locale;
        }

        switch (locale.getLanguage()) {
            case "en":
            default:
                rg_language.check(R.id.rb_english);
                break;
            case "it":
                rg_language.check(R.id.rb_italy);
                break;
            case "pt":
                rg_language.check(R.id.rb_portuguese);
                break;
        }
    }

}
