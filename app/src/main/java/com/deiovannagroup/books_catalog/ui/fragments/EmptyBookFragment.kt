/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.deiovannagroup.books_catalog.databinding.FragmentEmptyBookBinding
import com.deiovannagroup.books_catalog.ui.views.activities.InsertBookActivity

class EmptyBookFragment : Fragment() {
    private lateinit var binding: FragmentEmptyBookBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmptyBookBinding.inflate(
            inflater,
            container,
            false,
        )

        binding.btnNewBook.setOnClickListener {
            val intent = Intent(
                requireContext(),
                InsertBookActivity::class.java,
            )

            startActivity(intent)
        }

        return binding.root
    }
}