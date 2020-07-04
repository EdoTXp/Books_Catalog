/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesTheme
{
    private final SharedPreferences preferences;


    public SharedPreferencesTheme(Context context)
    {
        preferences = context.getSharedPreferences("theme", Context.MODE_PRIVATE);
    }

    // Este método salva o tema, colocando true se for no modo "dark" ou false se for no modo "day"
    public void setNightModeState(Boolean state)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putBoolean("NightMode", state);
        editor.apply();
    }

    //Este método carrega o tema
    public Boolean getNightModeState()
    {
        return preferences.getBoolean("NightMode", false);
    }


}
