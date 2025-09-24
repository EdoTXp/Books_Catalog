package com.deiovannagroup.books_catalog.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.deiovannagroup.books_catalog.databinding.FragmentBookOptionsDialogBinding
import androidx.core.net.toUri
import com.deiovannagroup.books_catalog.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BookOptionsDialogFragment : DialogFragment() {
    private val binding by lazy {
        FragmentBookOptionsDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bookTitle = requireArguments().getString(ARG_BOOK_TITLE) ?: ""
        val bookAuthor = requireArguments().getString(ARG_BOOK_AUTHOR) ?: ""

        setupListeners(bookTitle, bookAuthor)

        return MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.AlertDialogTheme,
        )
            .setView(binding.root)
            .create()
    }

    private fun setupListeners(title: String, author: String) {
        val shareText = getString(R.string.share_text, title, author)


        val searchUrl = "${getString(R.string.Google_Search)}${Uri.encode(title)}"

        binding.apply {
            btnShare.setOnClickListener {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
                dismiss()
            }

            btnSearch.setOnClickListener {
                val searchIntent = Intent(
                    Intent.ACTION_VIEW,
                    searchUrl.toUri(),
                )
                startActivity(searchIntent)
                dismiss()
            }
        }
    }

    companion object {
        private const val ARG_BOOK_TITLE = "book_title"
        private const val ARG_BOOK_AUTHOR = "book_author"

        fun newInstance(title: String, author: String): BookOptionsDialogFragment {
            val args = Bundle().apply {
                putString(ARG_BOOK_TITLE, title)
                putString(ARG_BOOK_AUTHOR, author)
            }
            return BookOptionsDialogFragment().apply {
                arguments = args
            }
        }
    }
}