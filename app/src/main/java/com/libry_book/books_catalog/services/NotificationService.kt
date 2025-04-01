/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.libry_book.books_catalog.services

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.libry_book.books_catalog.R

class NotificationService {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun execute(context: Context, textTitle: String, message: String) {

        var builder = NotificationCompat.Builder(context, "CHANNEL_ID")
            .setSmallIcon(R.drawable.transparent_icon_app)
            .setContentTitle(textTitle)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(
            1,
            builder.build(),
        )

    }

}