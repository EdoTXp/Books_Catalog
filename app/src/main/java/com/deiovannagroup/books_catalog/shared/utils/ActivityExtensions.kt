/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.shared.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.deiovannagroup.books_catalog.domain.services.app_services.VibratorService

fun AppCompatActivity.setEdgeToEdgeLayout(bindingRoot: View, mainView: View) {
    enableEdgeToEdge()
    setContentView(bindingRoot)
    ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(
            systemBars.left,
            systemBars.top,
            systemBars.right,
            systemBars.bottom,
        )
        insets
    }
}

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

fun AppCompatActivity.handleNotificationPermissionRequest(
    permissionLauncher: ActivityResultLauncher<String>
): Boolean {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        true
    } else {
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        false
    }
}