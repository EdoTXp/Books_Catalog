package com.deiovannagroup.books_catalog.ui.views.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.databinding.ActivitySearchBinding
import com.deiovannagroup.books_catalog.shared.utils.setEdgeToEdgeLayout
import com.deiovannagroup.books_catalog.shared.utils.showToastAndVibrate
import com.deiovannagroup.books_catalog.ui.adapters.BookComponentAdapter
import com.deiovannagroup.books_catalog.ui.fragments.EmptyBookFragment
import com.deiovannagroup.books_catalog.ui.viewmodel.SearchUiState
import com.deiovannagroup.books_catalog.ui.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var bookAdapter: BookComponentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeLayout(binding.root, binding.main)
        setupFragmentContainer(savedInstanceState)
        setupUI()
        initListeners()
        setupObservers()
    }

    private fun setupFragmentContainer(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                binding.searchFragmentContainer.id,
                EmptyBookFragment(),
            ).commit()
        }
    }


    private fun setupUI() {
        bookAdapter = BookComponentAdapter(
            onEditBook = { book ->
                searchViewModel.updateBook(
                    book.id,
                    book.title,
                    book.author,
                    book.year.toString()
                )
                showToastAndVibrate(getString(R.string.success_msg))
            }, onLongClickBook = { book ->

            }, onDeleteBook = { book ->
                searchViewModel.deleteBook(book)
                showToastAndVibrate(getString(R.string.success_msg))
            }
        )

        binding.rvBooks.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = bookAdapter
        }
    }

    private fun initListeners() {
        binding.rdgSearchBy.setOnCheckedChangeListener { _, checkedId ->
            binding.edtSearch.text?.clear()
            binding.edtSearch.isEnabled = true

            when (checkedId) {
                R.id.rbSearchByTitle -> binding.edtSearch.hint =
                    getString(R.string.hint_titulo)

                R.id.rbSearchByAuthor -> binding.edtSearch.hint =
                    getString(R.string.hint_autor)

                R.id.rbSearchByYear -> binding.edtSearch.hint =
                    getString(R.string.hint_ano)

                R.id.rbSearchByAll -> {
                    binding.edtSearch.isEnabled = false
                    binding.edtSearch.hint = getString(R.string.txt_pesquisar_todos)
                }
            }
        }

        binding.btnSearch.setOnClickListener {
            val query = binding.edtSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                when (binding.rdgSearchBy.checkedRadioButtonId) {
                    R.id.rbSearchByTitle -> {
                        searchViewModel.searchBookByTitle(query)
                    }

                    R.id.rbSearchByAuthor -> {
                        searchViewModel.searchBookByAuthor(query)
                    }

                    R.id.rbSearchByYear -> {
                        val year = query.toIntOrNull()
                        if (year != null) {
                            searchViewModel.searchBookByYear(year)
                        } else {
                            showToastAndVibrate(getString(R.string.error_msg))
                        }
                    }
                }
            } else if (binding.rdgSearchBy.checkedRadioButtonId == R.id.rbSearchByAll) {
                searchViewModel.searchAllBooks()
            } else {
                showToastAndVibrate(getString(R.string.empty_field_error))
            }
        }

        binding.fabUp.setOnClickListener {
            binding.rvBooks.smoothScrollToPosition(0)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    searchViewModel.searchState.collect { state ->
                        resetViews()

                        when (state) {
                            is SearchUiState.Initial -> {}

                            is SearchUiState.Loading -> binding.progressBar.visibility =
                                View.VISIBLE

                            is SearchUiState.Empty -> binding.searchFragmentContainer.visibility =
                                View.VISIBLE

                            is SearchUiState.Success -> {
                                binding.apply {
                                    rvBooks.visibility = View.VISIBLE
                                    fabUp.visibility = View.VISIBLE
                                    txtSearchFounded.visibility = View.VISIBLE

                                    bookAdapter.submitList(state.books)

                                    val booksFounded =
                                        getString(R.string.txt_pesquisar) + "${state.books.size}"

                                    txtSearchFounded.text = booksFounded
                                }
                            }

                            is SearchUiState.Error -> showToastAndVibrate(
                                getString(R.string.error_msg) + ": " + state.message,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun resetViews() {
        binding.apply {
            progressBar.visibility = View.GONE
            rvBooks.visibility = View.GONE
            searchFragmentContainer.visibility = View.GONE
            fabUp.visibility = View.GONE
            txtSearchFounded.visibility = View.GONE
        }
    }
}