/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.activities;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.customview.BookComponentAdapter;
import com.BiblioLivro.criarlivros.model.BookItem;
import com.BiblioLivro.criarlivros.model.Order;
import com.BiblioLivro.criarlivros.storage.DatabaseHelper;
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TelaPesquisar extends AppCompatActivity implements View.OnClickListener {

    //ATRIBUTOS
    private FloatingActionButton upButton;
    private RecyclerView recyclerView;
    private BookComponentAdapter bookAdapter;
    private Menu menuFilter;
    private int checked = 0;  // valor selecionado para executar o sort do bookAdapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Método para setar o tema da activity ao iniciar
        SharedPreferencesTheme sharedPreferences = new SharedPreferencesTheme(this);
        sharedPreferences.setAppTheme();

        //Arrumando a Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pesquisar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_activity_tela_pesquisar);

        //preenchendo o botão upButton e adicionando um evento de click
        upButton = findViewById(R.id.floatingActionButtonUp);
        upButton.setOnClickListener(this);

        /*
         * Intent recebe dois parâmentros:
         * o Tipo: recebe o id do RadioGroup passado na TelaPrincipal e
         * na Notificação o id: R.id.rbPesquisarPorTodos
         * a chave: O valor do texto recebido na TelaPrincipal e no caso da notificação, um texto vazio
         * */
        Intent it = getIntent();

        if (it != null) {

            // preenchimento dos parâmetros para passar à busca no database.
            int tipo = it.getIntExtra("tipo", 0);
            String chave = it.getStringExtra("chave");

            //preenchimento da lista com os dados do database
            List<ContentValues> lista = getContentValuesList(tipo, chave);

            /* Será feita a criação do RecyclerView
             * onde vai preencher BookComponentAdapter
             * e em seguida, adicioná-los ao RecyclerView
             * */
            if (lista.size() > 0) {
                //preenchimeto do recyclerView com o file xml
                recyclerView = findViewById(R.id.rv_pesquisar);

                /* Será criado um Array de Livros e preencidos com os valores vindos da "lista".
                 * Ao terminar o preenchimento de todos os livro,
                 * será adicionado ao bookAdapter e em seguida,
                 * adicionado ao RecycleView
                 */

                //criação do Array de Livros
                ArrayList<BookItem> bookItems = new ArrayList<>();
                for (ContentValues cv : lista) {
                    BookItem bookItem = new BookItem
                            (
                                    cv.getAsInteger("id"),
                                    cv.getAsString("titulo"),
                                    cv.getAsString("autor"),
                                    cv.getAsInteger("ano")
                            );

                    bookItems.add(bookItem);
                }

                //adicionando os arrays de livros ao bookadapter
                bookAdapter = new BookComponentAdapter(this, bookItems);

                //adicionando o bookAdapter, ordenadamente, ao reclycerView e adicionado o layout
                recyclerView.setAdapter(bookAdapter);
                bookAdapter.setSortOfAdapterView(1, Order.ASCENDANT);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                /* Adicionando o evento de scroll onde se a posição do primeiro item for maior que zero,
                 * aparecerá visível o botão upButton senão ficará invisível
                 */
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        if (recyclerView.computeVerticalScrollOffset() != 0)
                            upButton.setVisibility(View.VISIBLE);
                        else upButton.setVisibility(View.INVISIBLE);

                        super.onScrolled(recyclerView, dx, dy);
                    }
                });

                // Quando o bookAdapter ficará vazio, será desabilitdo o botão do menu "btnFilter"
                bookAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {
                        if (bookAdapter.itemIsEmpty()) {
                            menuFilter.getItem(0).setEnabled(false);

                            setContentView(R.layout.activity_tela_pesquisar_empty_book);
                            TextView empty_Book = findViewById(R.id.txt_emptyBook);
                            empty_Book.setText(R.string.AllBookDeleted);
                        }
                    }
                });

            }

            /*Se a lista for vazia será exibido o layout Empty Book e
             *será adiocionado o texto comforme o contexto
             */
            else {
                setContentView(R.layout.activity_tela_pesquisar_empty_book);
                TextView empty_Book = findViewById(R.id.txt_emptyBook);
                empty_Book.setText(R.string.FieldNotFound);
            }
        }
    }

    private List<ContentValues> getContentValuesList(int tipo, String chave) {
        List<ContentValues> booksList = new ArrayList<>();

        //realização da busca por Título
        if (tipo == R.id.rbPesquisarPorTitulo) {
            booksList = new DatabaseHelper(this).searchByTitle(chave);
        }

        /* realização da busca por ano
         * se o ano não encontrar nenhum valor inteiro será feita uma busca por todos os objetos da lista
         */
        else if (tipo == R.id.rbPesquisarPorAno) {
            try {
                booksList = new DatabaseHelper(this).searchByYear(Integer.parseInt(Objects.requireNonNull(chave)));
            } catch (Exception e) {
                booksList = new DatabaseHelper(this).searchAll();
            }
        }
        //realização da busca por Autor
        else if (tipo == R.id.rbPesquisarPorAutor) {
            booksList = new DatabaseHelper(this).searchByAuthor(chave);
        }

        //realização da busca por todos os objetos
        else if (tipo == R.id.rbPesquisarPorTodos) {
            booksList = new DatabaseHelper(this).searchAll();
        }
        return booksList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_item_bar, menu);
        menuFilter = menu;
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        /*Método chamado quando algum resultado pesquisado não foi encontrado na lista,
         * será desativado o menu de ordenamento
         * */

        if (bookAdapter == null) {
            menu.findItem(R.id.btn_filter).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /*
         * Opções para escolher como ordenar os livros
         * */

        if (item.getItemId() == R.id.btn_filter && bookAdapter.getItemCount() > 0) {
            AlertDialog.Builder orderDialog = new AlertDialog.Builder(this);
            orderDialog.setTitle(getString(R.string.order_txt));

            String[] orderOption =
                    {
                            getString(R.string.txt_titulo).concat(" ↑"),
                            getString(R.string.txt_titulo).concat(" ↓"),
                            getString(R.string.txt_autor).concat(" ↑"),
                            getString(R.string.txt_autor).concat(" ↓"),
                            getString(R.string.txt_ano).concat(" ↑"),
                            getString(R.string.txt_ano).concat(" ↓")
                    };

            orderDialog.setIcon(R.drawable.filter_img);
            orderDialog.setSingleChoiceItems(orderOption, checked, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            bookAdapter.setSortOfAdapterView(1, Order.ASCENDANT);
                            dialog.dismiss();
                            checked = which;
                            break;

                        case 1:
                            bookAdapter.setSortOfAdapterView(1, Order.DESCENDANT);
                            dialog.dismiss();
                            checked = which;
                            break;

                        case 2:
                            bookAdapter.setSortOfAdapterView(2, Order.ASCENDANT);
                            dialog.dismiss();
                            checked = which;
                            break;

                        case 3:
                            bookAdapter.setSortOfAdapterView(2, Order.DESCENDANT);
                            dialog.dismiss();
                            checked = which;
                            break;

                        case 4:
                            bookAdapter.setSortOfAdapterView(3, Order.ASCENDANT);
                            dialog.dismiss();
                            checked = which;
                            break;

                        case 5:
                            bookAdapter.setSortOfAdapterView(3, Order.DESCENDANT);
                            dialog.dismiss();
                            checked = which;
                            break;

                        default:
                            break;
                    }
                }
            });
            orderDialog.setNegativeButton(R.string.email_btn_cancel, null);
            orderDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {


        /* Evento chamado pelo upButton onde ao clicá-lo será scrolado o recycleView para a posição 0
         * e o botão se tornará invisível.
         */

        if (v.getId() == R.id.floatingActionButtonUp) {
            recyclerView.scrollToPosition(0);
            upButton.setVisibility(View.INVISIBLE);
        }
    }

    public void addNewBook(View view) {
        /*Evento chamado pelo botão do layout "Tela Pesquisar Empty Book / No Book Found",
         * onde ao clicá-lo será iniciada a tela Cadastrar para que o usuário possa adiocionar
         * um novo livro.
         * */
        startActivity(new Intent(this, TelaCadastrar.class));
        finish();
    }
}

