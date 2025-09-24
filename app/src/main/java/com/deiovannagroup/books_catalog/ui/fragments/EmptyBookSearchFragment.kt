/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.deiovannagroup.books_catalog.ui.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.deiovannagroup.books_catalog.databinding.FragmentEmptyBookSearchBinding
import com.deiovannagroup.books_catalog.ui.views.activities.InsertBookActivity

class EmptyBookSearchFragment : Fragment() {
    private lateinit var binding: FragmentEmptyBookSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmptyBookSearchBinding.inflate(
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        val orientation = newConfig.orientation

        binding.imageView.visibility =
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                View.VISIBLE
            else
                View.GONE

        super.onConfigurationChanged(newConfig)
    }
}