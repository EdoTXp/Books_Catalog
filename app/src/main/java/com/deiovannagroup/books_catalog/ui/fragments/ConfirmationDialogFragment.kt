/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.deiovannagroup.books_catalog.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ConfirmationDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = requireArguments().getString(ARG_TITLE)
        val message = requireArguments().getString(ARG_MESSAGE)
        val requestKey = requireArguments().getString(ARG_REQUEST_KEY)!!

        return MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.AlertDialogTheme
        )
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.transparent_icon_app)
            .setPositiveButton(context?.resources?.getString(R.string.yes)) { _, _ ->
                setFragmentResult(requestKey, Bundle())
            }
            .setNegativeButton(
                context?.resources?.getString(R.string.no),
                null,
            )
            .create()
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_MESSAGE = "arg_message"

        private const val ARG_REQUEST_KEY = "arg_request_key"

        fun newInstance(
            title: String,
            message: String,
            requestKey: String,
        ): ConfirmationDialogFragment {
            return ConfirmationDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                    putString(ARG_REQUEST_KEY, requestKey)
                }
            }
        }
    }
}