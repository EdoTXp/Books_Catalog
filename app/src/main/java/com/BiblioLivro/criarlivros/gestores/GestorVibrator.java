/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.gestores;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GestorVibrator {

    /**
     * @param milliseconds: duração da vibração
     * @param context: receber o SystemService
     */
    public static void Vibrate(long milliseconds, @NotNull Context context)
    {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Objects.requireNonNull(vibrator).vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else
            Objects.requireNonNull(vibrator).vibrate(milliseconds);
    }
}
