/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deiovannagroup.books_catalog.services.app_services.VibratorService

fun AppCompatActivity.showToastAndVibrate(message: String, vibrationDuration: Long = 100L) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_LONG,
    ).show()

    VibratorService.vibrate(
        this,
        vibrationDuration,
    )
}

fun AppCompatActivity.setSupportActionBar() {
    supportActionBar?.apply {
        setDisplayHomeAsUpEnabled(true)
    }
}