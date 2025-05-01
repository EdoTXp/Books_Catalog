/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.services.email_service

import android.content.Context
import androidx.core.app.ShareCompat.IntentBuilder
import com.deiovannagroup.books_catalog.R
import org.jetbrains.annotations.NotNull

class EmailService {

    companion object {
        fun sendEmail(
            context: Context,
            emailTitle: String = context.resources.getString(R.string.email_chooseapp),
            emailBody: @NotNull String,
            emailSubject: String = context.resources.getString(R.string.email_subject),
            emailTo: String = context.resources.getString(R.string.developer_email),
        ) {
            IntentBuilder(context).apply {
                setType("message/rfc822") // Email Type
                setChooserTitle(emailTitle)
                addEmailTo(emailTo)
                setSubject(emailSubject)
                setText(emailBody)
                startChooser()
            }
        }
    }
}