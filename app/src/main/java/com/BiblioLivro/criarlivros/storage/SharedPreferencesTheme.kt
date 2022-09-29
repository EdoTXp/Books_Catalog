/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

public class SharedPreferencesTheme {
    public final int THEME_UNDEFINED = -1;
    public final int THEME_LIGHT = 0;
    public final int THEME_DARK = 1;
    public final int THEME_SYSTEM = 2;
    public final int THEME_BATTERY = 3;

    private final SharedPreferences preferences;
    private final Context context;

    public SharedPreferencesTheme(Context context) {
        preferences = context.getSharedPreferences("theme", Context.MODE_PRIVATE);
        this.context = context;
    }

    public int getCheckedButton() {
        return preferences.getInt("NightModeChoice", THEME_UNDEFINED);
    }

    public void setCheckedButton(int button) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("NightModeChoice", button);
        editor.apply();
    }

    public void setAppTheme() {
        switch (getCheckedButton()) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case THEME_BATTERY:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;

            case THEME_UNDEFINED:
            default:
                switch (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                    case Configuration.UI_MODE_NIGHT_NO:
                    case Configuration.UI_MODE_NIGHT_UNDEFINED:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;

                    case Configuration.UI_MODE_NIGHT_YES:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                }
                break;
        }

    }
}
