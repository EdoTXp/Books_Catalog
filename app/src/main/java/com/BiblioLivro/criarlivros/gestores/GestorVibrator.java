/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.gestores;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GestorVibrator {

    /**
     * @param milliseconds: duração da vibração
     * @param activity: receber o SystemService
     */
    public static void Vibrate(long milliseconds, @NotNull AppCompatActivity activity)
    {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Objects.requireNonNull(vibrator).vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else
            Objects.requireNonNull(vibrator).vibrate(milliseconds);
    }
}
