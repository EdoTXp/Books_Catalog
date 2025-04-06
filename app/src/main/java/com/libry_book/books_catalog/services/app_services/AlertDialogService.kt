/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.libry_book.books_catalog.services.app_services

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.libry_book.books_catalog.R

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
            negativeAction: (() -> Unit?)?,
        ) {
            AlertDialog.Builder(context).apply {
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

        fun showDialogWithSingleChoiceItems(
            context: Context,
            title: String,
            message: String? = null,
            icon: Int = R.drawable.transparent_icon_app,
            items: Array<String>,
            checkedItem: Int,
            checkedAction: (DialogInterface, Int) -> Unit,
            negativeButton: String = context.resources.getString(R.string.no),
            negativeAction: ((DialogInterface, Int) -> Unit?)? = null,
        ) {
            AlertDialog.Builder(
                context
            ).apply {
                setTitle(title)
                setMessage(message)
                setIcon(icon)
                setSingleChoiceItems(items, checkedItem) { dialog, checkedItem ->
                    checkedAction(dialog, checkedItem)
                }
                setNegativeButton(negativeButton) { dialog, checkedItem ->
                    negativeAction?.invoke(dialog, checkedItem)
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

            AlertDialog.Builder(context).apply {
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