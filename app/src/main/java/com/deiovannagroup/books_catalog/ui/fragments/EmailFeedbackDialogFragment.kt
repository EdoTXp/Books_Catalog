/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.databinding.FragmentEmailFeedbackDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EmailFeedbackDialogFragment : DialogFragment() {

    private val binding by lazy {
        FragmentEmailFeedbackDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val requestKey = requireArguments().getString(ARG_REQUEST_KEY)!!

        return MaterialAlertDialogBuilder(
            requireActivity(), R.style.AlertDialogTheme
        )
            .setTitle(R.string.email_title)
            .setView(binding.root)
            .setIcon(R.drawable.transparent_icon_app)
            .setPositiveButton(R.string.email_btn_send) { _, _ ->
                val feedbackText = binding.editTextFeedback.text.toString().trim()

                if (feedbackText.isNotBlank()) {
                    setFragmentResult(
                        requestKey,
                        bundleOf(RESULT_FEEDBACK_TEXT to feedbackText),
                    )
                }
            }.setNegativeButton(
                context?.resources?.getString(R.string.email_btn_cancel),
                null,
            ).create()
    }

    companion object {
        const val RESULT_FEEDBACK_TEXT = "result_feedback_text"
        private const val ARG_REQUEST_KEY = "arg_request_key"

        fun newInstance(requestKey: String): EmailFeedbackDialogFragment {
            return EmailFeedbackDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_REQUEST_KEY, requestKey)
                }
            }
        }
    }

}