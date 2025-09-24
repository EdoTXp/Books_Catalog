/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.domain.services.app_services

import android.content.Context
import android.view.View
import com.deiovannagroup.books_catalog.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertDialogService {

    companion object {
        fun showDialog(
            context: Context,
            title: String,
            message: String,
            icon: Int = R.drawable.transparent_icon_app,
            positiveButton: String = context.resources.getString(R.string.yes),
            negativeButton: String = context.resources.getString(R.string.no),
            positiveAction: () -> Unit,
            negativeAction: (() -> Unit?)? = null,
        ) {
            MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme).apply {
                setTitle(title)
                setMessage(message)
                setIcon(icon)
                setPositiveButton(positiveButton) { _, _ ->
                    positiveAction()
                }
                setNegativeButton(negativeButton) { _, _ ->
                    negativeAction?.invoke()
                }
                create()
                show()
            }
        }

        fun showDialogWithCustomView(
            context: Context,
            title: String,
            message: String? = null,
            customView: View,
            icon: Int = R.drawable.transparent_icon_app,
            positiveButton: String = context.resources.getString(R.string.yes),
            negativeButton: String = context.resources.getString(R.string.no),
            positiveAction: () -> Unit,
            negativeAction: (() -> Unit?)? = null
        ) {

            MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme).apply {
                setTitle(title)
                setMessage(message)
                setIcon(icon)
                setView(customView)
                setPositiveButton(positiveButton) { _, _ ->
                    positiveAction()
                }
                setNegativeButton(negativeButton) { _, _ ->
                    negativeAction?.invoke()
                }
                create()
                show()
            }
        }
    }
}