/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.libry_book.books_catalog.views.activities

import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.libry_book.books_catalog.R
import com.libry_book.books_catalog.models.BookItem
import com.libry_book.books_catalog.models.Order
import com.libry_book.books_catalog.storage.DatabaseHelperImpl
import com.libry_book.books_catalog.storage.SharedPreferencesTheme
import com.libry_book.books_catalog.views.customview.BookComponentAdapter

class SearchActivity : AppCompatActivity(), View.OnClickListener {
    //ATRIBUTOS
    private lateinit var upButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookComponentAdapter
    private lateinit var btnNewBook: Button // esse botão aparecerà quando não tiver nenhum livro na busca ou quando a busca não tiver nenhum resultado

    private lateinit var menuFilter: Menu
    private var checked: Int = 0 // valor selecionado para executar o sort do bookAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        //Método para setar o tema da activity ao iniciar
        val sharedPreferences = SharedPreferencesTheme(this)
        sharedPreferences.setAppTheme()

        //Arrumando a Activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_pesquisar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //preenchendo o botão upButton e adicionando um evento de click
        upButton = findViewById(R.id.floatingActionButtonUp)
        upButton.setOnClickListener(this)

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
                recyclerView = findViewById(R.id.rv_pesquisar)

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
                bookAdapter = BookComponentAdapter(this, bookItems)

                //adicionando um contador de livros encontrados
                val bookResult = findViewById<TextView>(R.id.txt_search_founded)
                val quantityOfBookResult = "${getString(R.string.txt_pesquisar)} ${bookItems.size}"

                bookResult.text = quantityOfBookResult

                //adicionando o bookAdapter, ordenadamente, ao reclycerView e adicionado o layout
                recyclerView.adapter = bookAdapter
                bookAdapter.setSortOfAdapterView(1, Order.ASCENDANT)
                recyclerView.layoutManager = LinearLayoutManager(this)

                /* Adicionando o evento de scroll onde se a posição do primeiro item for maior que zero,
                 * aparecerá visível o botão upButton senão ficará invisível
                 */recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (recyclerView.computeVerticalScrollOffset() != 0) upButton.visibility =
                            View.VISIBLE else upButton.visibility = View.INVISIBLE
                        super.onScrolled(recyclerView, dx, dy)
                    }
                })

                // Quando o bookAdapter ficará vazio, será desabilitdo o botão do menu "btnFilter"
                bookAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                        if (bookAdapter.itemIsEmpty()) {
                            menuFilter.getItem(0).isEnabled = false
                            setContentView(R.layout.activity_tela_pesquisar_empty_book)
                            btnNewBook = findViewById(R.id.btnNewBook)
                            btnNewBook.setOnClickListener { addNewBook() }
                            val emptyBook = findViewById<TextView>(R.id.txt_emptyBook)
                            emptyBook.setText(R.string.AllBookDeleted)
                        }
                    }
                })
            } else {
                setContentView(R.layout.activity_tela_pesquisar_empty_book)
                btnNewBook = findViewById(R.id.btnNewBook)
                btnNewBook.setOnClickListener { addNewBook() }
                val emptyBook = findViewById<TextView>(R.id.txt_emptyBook)
                emptyBook.setText(R.string.FieldNotFound)
            }
        }
    }

    private fun getContentValuesList(tipo: Int, chave: String): List<ContentValues> {
        var booksList: List<ContentValues> = ArrayList()

        //realização da busca por Título
        when (tipo) {
            R.id.rbPesquisarPorTitulo -> {
                booksList = DatabaseHelperImpl(this).searchByTitle(chave)
            }

            R.id.rbPesquisarPorAno -> {
                booksList = try {
                    DatabaseHelperImpl(this).searchByYear(chave.toInt())
                } catch (e: Exception) {
                    DatabaseHelperImpl(this).searchAll()
                }
            }

            R.id.rbPesquisarPorAutor -> {
                booksList = DatabaseHelperImpl(this).searchByAuthor(chave)
            }

            R.id.rbPesquisarPorTodos -> {
                booksList = DatabaseHelperImpl(this).searchAll()
            }
        }
        return booksList
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.filter_item_bar, menu)
        menuFilter = menu
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        /* Método chamado quando algum resultado pesquisado não foi encontrado na lista,
         * será desativado o menu de ordenamento
         * */
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*
         * Opções para escolher como ordenar os livros
         * */
        if (item.itemId == R.id.btn_filter && bookAdapter.itemCount > 0) {
            val orderDialog = AlertDialog.Builder(this)
            orderDialog.setTitle(getString(R.string.order_txt))
            val orderOptions = arrayOf(
                getString(R.string.txt_titulo) + " ↑",
                getString(R.string.txt_titulo) + " ↓",
                getString(R.string.txt_autor) + " ↑",
                getString(R.string.txt_autor) + " ↓",
                getString(R.string.txt_ano) + " ↑",
                getString(R.string.txt_ano) + " ↓"
            )
            orderDialog.setIcon(R.drawable.filter_img)
            orderDialog.setSingleChoiceItems(
                orderOptions,
                checked
            ) { dialog: DialogInterface, which: Int ->
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
                        Toast.makeText(this, getString(R.string.error_msg), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            orderDialog.setNegativeButton(R.string.email_btn_cancel, null)
            orderDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {


        /* Evento chamado pelo upButton onde ao clicá-lo será scrolado o recycleView para a posição 0
         * e o botão se tornará invisível.
         */
        if (v.id == R.id.floatingActionButtonUp) {
            recyclerView.scrollToPosition(0)
            upButton.visibility = View.INVISIBLE
        }
    }

    fun addNewBook() {
        /*Evento chamado pelo botão do layout "Tela Pesquisar Empty Book / No Book Found",
         * onde ao clicá-lo será iniciada a tela Cadastrar para que o usuário possa adiocionar
         * um novo livro.
         * */
        startActivity(Intent(this, RegistryActivity::class.java))
        finish()
    }
}