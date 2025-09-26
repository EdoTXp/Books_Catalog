/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.deiovannagroup.books_catalog.databinding.FragmentEditBookDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.deiovannagroup.books_catalog.R

class EditBookDialogFragment : DialogFragment() {

    private val binding by lazy {
        FragmentEditBookDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val requestKey = requireArguments().getString(ARG_REQUEST_KEY)!!
        val bookId = requireArguments().getInt(ARG_BOOK_ID)
        val bookTitle = requireArguments().getString(ARG_BOOK_TITLE)
        val bookAuthor = requireArguments().getString(ARG_BOOK_AUTHOR)
        val bookYear = requireArguments().getInt(ARG_BOOK_YEAR)

        binding.apply {
            tietEditTitle.setText(bookTitle)
            tietEditAuthor.setText(bookAuthor)
            tietEditYear.setText(bookYear.toString())
        }

        return MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.AlertDialogTheme
        )
            .setTitle(R.string.edit_item_Title)
            .setIcon(R.drawable.transparent_icon_app)
            .setView(binding.root)
            .setPositiveButton(R.string.btn_Salvar) { _, _ ->
                val newTitle = binding.tietEditTitle.text.toString().trim()
                val newAuthor = binding.tietEditAuthor.text.toString().trim()
                val newYear = binding.tietEditYear.text.toString().trim()

                if (newTitle.isNotEmpty() && newAuthor.isNotEmpty() && newYear.isNotEmpty()) {
                    val resultBundle = bundleOf(
                        RESULT_BOOK_ID to bookId,
                        RESULT_BOOK_TITLE to newTitle,
                        RESULT_BOOK_AUTHOR to newAuthor,
                        RESULT_BOOK_YEAR to newYear
                    )
                    setFragmentResult(requestKey, resultBundle)
                }
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .create()
    }

    companion object {
        private const val ARG_REQUEST_KEY = "arg_request_key"
        private const val ARG_BOOK_ID = "arg_book_id"
        private const val ARG_BOOK_TITLE = "arg_book_title"
        private const val ARG_BOOK_AUTHOR = "arg_book_author"
        private const val ARG_BOOK_YEAR = "arg_book_year"

        const val RESULT_BOOK_ID = "result_book_id"
        const val RESULT_BOOK_TITLE = "result_book_title"
        const val RESULT_BOOK_AUTHOR = "result_book_author"
        const val RESULT_BOOK_YEAR = "result_book_year"

        fun newInstance(
            requestKey: String,
            bookId: Int,
            bookTitle: String,
            bookAuthor: String,
            bookYear: Int
        ): EditBookDialogFragment {
            return EditBookDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_REQUEST_KEY, requestKey)
                    putInt(ARG_BOOK_ID, bookId)
                    putString(ARG_BOOK_TITLE, bookTitle)
                    putString(ARG_BOOK_AUTHOR, bookAuthor)
                    putInt(ARG_BOOK_YEAR, bookYear)
                }
            }
        }
    }
}