/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.views.activities

import android.content.ContentValues
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.models.BookItem
import com.deiovannagroup.books_catalog.models.Order
import com.deiovannagroup.books_catalog.helpers.DatabaseHelperImpl
import com.deiovannagroup.books_catalog.views.adapters.BookComponentAdapter
import com.deiovannagroup.books_catalog.databinding.ActivitySearchBinding
import com.deiovannagroup.books_catalog.services.app_services.AlertDialogService
import com.deiovannagroup.books_catalog.utils.setSupportActionBar
import com.deiovannagroup.books_catalog.utils.showToastAndVibrate
import com.deiovannagroup.books_catalog.views.fragments.EmptyBookFragment

class SearchActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private val emptyBookFragmentManager = supportFragmentManager.beginTransaction()
        .replace(
            R.id.main,
            EmptyBookFragment(),
        )

    private lateinit var bookAdapter: BookComponentAdapter
    private var checked: Int = 0 // valor selecionado para executar o sort do bookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeLayout()
        setSupportActionBar()
        initFilterMenu()
        initListeners()

        /*
         * Intent recebe dois parâmentros:
         * o Tipo: recebe o id do RadioGroup passado na TelaPrincipal e
         * na Notificação o id: R.id.rbPesquisarPorTodos
         * a chave: O valor do texto recebido na TelaPrincipal e no caso da notificação, um texto vazio
         * */
        val it = intent
        if (it != null) {

            // preenchimento dos parâmetros para passar à busca no database.
            val tipo = it.getIntExtra("tipo", 0)
            val chave = it.getStringExtra("chave")

            //preenchimento da lista com os dados do database
            val lista = getContentValuesList(tipo, chave!!)

            /* Será feita a criação do RecyclerView
             * onde vai preencher BookComponentAdapter
             * e em seguida, adicioná-los ao RecyclerView
             * */if (lista.isNotEmpty()) {
                //preenchimeto do recyclerView com o file xml


                /* Será criado um Array de Livros e preencidos com os valores vindos da "lista".
                 * Ao terminar o preenchimento de todos os livro,
                 * será adicionado ao bookAdapter e em seguida,
                 * adicionado ao RecycleView
                 */

                //criação do Array de Livros
                val bookItems = ArrayList<BookItem>()
                for (cv in lista) {
                    val bookItem = BookItem(
                        cv.getAsInteger("id"),
                        cv.getAsString("titulo"),
                        cv.getAsString("autor"),
                        cv.getAsInteger("ano")
                    )
                    bookItems.add(bookItem)
                }


                //adicionando os arrays de livros ao bookadapter
                bookAdapter = BookComponentAdapter(bookItems)

                //adicionando um contador de livros encontrados
                val bookResult = findViewById<TextView>(R.id.txt_search_founded)
                val quantityOfBookResult = "${getString(R.string.txt_pesquisar)} ${bookItems.size}"

                bookResult.text = quantityOfBookResult

                //adicionando o bookAdapter, ordenadamente, ao reclycerView e adicionado o layout
                binding.rvBooks.adapter = bookAdapter
                bookAdapter.setSortOfAdapterView(1, Order.ASCENDANT)
                binding.rvBooks.layoutManager = LinearLayoutManager(this)

                /* Adicionando o evento de scroll onde se a posição do primeiro item for maior que zero,
                 * aparecerá visível o botão upButton senão ficará invisível
                 */
                binding.rvBooks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        binding.fabUp.visibility =
                            if (recyclerView.computeVerticalScrollOffset() != 0) {
                                View.VISIBLE
                            } else View.INVISIBLE
                        super.onScrolled(recyclerView, dx, dy)
                    }
                })

                // Quando o bookAdapter ficará vazio, será desabilitdo o botão do menu "btnFilter"
                bookAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                        if (bookAdapter.itemsIsEmpty()) {
                            showEmptyBookFragment()
                        }
                    }
                })
            } else
                showEmptyBookFragment()
        }
    }

    private fun showEmptyBookFragment() {
        binding.txtSearchFounded.visibility = View.INVISIBLE
        emptyBookFragmentManager.commit()
    }

    private fun initListeners() {
        binding.fabUp.setOnClickListener {
            binding.rvBooks.scrollToPosition(0)
            binding.fabUp.visibility = View.INVISIBLE
        }
    }

    private fun setEdgeToEdgeLayout() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom,
            )
            insets
        }
    }

    private fun getContentValuesList(tipo: Int, chave: String): List<ContentValues> {
        var booksList: List<ContentValues> = ArrayList()

        //realização da busca por Título
        when (tipo) {
            R.id.rbSearchByTitle -> {
                booksList = DatabaseHelperImpl(this).searchByTitle(chave)
            }

            R.id.rbSearchByYear -> {
                booksList = try {
                    DatabaseHelperImpl(this).searchByYear(chave.toInt())
                } catch (_: Exception) {
                    DatabaseHelperImpl(this).searchAll()
                }
            }

            R.id.rbSearchByAuthor -> {
                booksList = DatabaseHelperImpl(this).searchByAuthor(chave)
            }

            R.id.rbSearchByAll -> {
                booksList = DatabaseHelperImpl(this).searchAll()
            }
        }
        return booksList
    }

    private fun initFilterMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(
                menu: Menu,
                menuInflater: MenuInflater
            ) {
                if (DatabaseHelperImpl(this@SearchActivity).tableExist()) {
                    menuInflater.inflate(R.menu.filter_item_bar, menu)
                    true
                }
                false
            }


            override fun onMenuItemSelected(item: MenuItem): Boolean {
                /*
        * Opções para escolher como ordenar os livros
        * */
                if (item.itemId == R.id.btn_filter && bookAdapter.itemCount > 0) {
                    val orderOptions = arrayOf(
                        getString(R.string.txt_titulo) + " ↑",
                        getString(R.string.txt_titulo) + " ↓",
                        getString(R.string.txt_autor) + " ↑",
                        getString(R.string.txt_autor) + " ↓",
                        getString(R.string.txt_ano) + " ↑",
                        getString(R.string.txt_ano) + " ↓"
                    )

                    AlertDialogService.showDialogWithSingleChoiceItems(
                        this@SearchActivity,
                        getString(R.string.order_txt),
                        items = orderOptions,
                        checkedItem = checked,
                        negativeButton = getString(R.string.email_btn_cancel),
                        checkedAction = { dialog, which ->
                            when (which) {
                                0 -> {
                                    bookAdapter.setSortOfAdapterView(1, Order.ASCENDANT)
                                    dialog.dismiss()
                                    checked = 0
                                }

                                1 -> {
                                    bookAdapter.setSortOfAdapterView(1, Order.DESCENDANT)
                                    dialog.dismiss()
                                    checked = which
                                }

                                2 -> {
                                    bookAdapter.setSortOfAdapterView(2, Order.ASCENDANT)
                                    dialog.dismiss()
                                    checked = which
                                }

                                3 -> {
                                    bookAdapter.setSortOfAdapterView(2, Order.DESCENDANT)
                                    dialog.dismiss()
                                    checked = which
                                }

                                4 -> {
                                    bookAdapter.setSortOfAdapterView(3, Order.ASCENDANT)
                                    dialog.dismiss()
                                    checked = which
                                }

                                5 -> {
                                    bookAdapter.setSortOfAdapterView(3, Order.DESCENDANT)
                                    dialog.dismiss()
                                    checked = which
                                }

                                else -> {
                                    bookAdapter.setSortOfAdapterView(1, Order.ASCENDANT)
                                    dialog.dismiss()
                                    checked = 0

                                    showToastAndVibrate(
                                        getString(R.string.error_msg),
                                    )
                                }
                            }
                        },
                    )
                }
                return true
            }

        })
    }
}