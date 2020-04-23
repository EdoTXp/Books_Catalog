/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.customview.BookComponentAdapter;
import com.BiblioLivro.criarlivros.model.BookItem;
import com.BiblioLivro.criarlivros.storage.DatabaseHelper;
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TelaPesquisar extends AppCompatActivity implements View.OnClickListener {

    //TODO Aggiungere un menu per ordinare gli elementi della lista

    //ATRIBUTOS
    private FloatingActionButton upButton;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Método para setar o tema da activity ao iniciar
        setTheme();

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
         * o Tipo: recebe o id do RadioGroup passado na TelaPrincipal e na Notificação o id: R.id.rbPesquisarPorTodos
         * a chave: O valor do texto recebido na TelaPrincipal e no caso da notificação, um texto vazio
         * */
        Intent it = getIntent();

        if (it != null) {

            // preenchimento dos parâmetros para passar à busca no database.
            int tipo = it.getIntExtra("tipo", 0);
            String chave = it.getStringExtra("chave");

            List<ContentValues> lista = new ArrayList<>();

            //realização da busca por Título
            if (tipo == R.id.rbPesquisarPorTitulo) {
                lista = new DatabaseHelper(this).pesquisarPorTitulo(chave);
            }

            /* realização da busca por ano
             * se o ano não encontrar nenhum valor inteiro será feita uma busca por todos os objetos da lista
             */
            else if (tipo == R.id.rbPesquisarPorAno) {
                try {
                    lista = new DatabaseHelper(this).pesquisarPorAno(Integer.parseInt(Objects.requireNonNull(chave)));
                } catch (Exception e) {
                    lista = new DatabaseHelper(this).pesquisarPorTodos();
                }
            }
            //realização da busca por Autor
            else if (tipo == R.id.rbPesquisarPorAutor) {
                lista = new DatabaseHelper(this).pesquisarPorAutor(chave);
            }

            //realização da busca por todos os objetos
            else if (tipo == R.id.rbPesquisarPorTodos) {
                lista = new DatabaseHelper(this).pesquisarPorTodos();
            }

            /* Se a lista não for vazia,
             * será feita a criação do RecyclerView
             * onde vai preencher BookComponentAdapter
             * e em seguida, adicioná-los ao RecyclerView
             * */
            if (lista != null) {
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
                        BookItem bookItem = new BookItem(
                                cv.getAsInteger("id"),
                                cv.getAsString("titulo"),
                                cv.getAsString("autor"),
                                cv.getAsInteger("ano"));

                        bookItems.add(bookItem);
                    }

                    //adiocnado os arrays de livros ao bookadapter
                    BookComponentAdapter bookAdapter = new BookComponentAdapter(this, bookItems);

                    //adicionado o bookAdapter ao reclycerView e adicionado o layout
                    recyclerView.setAdapter(bookAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));

                    /* Adicionando o evento de scroll onde se a posição do primeiro item for maior que zero,
                     * aparecerá visível o botão upButton senão aparecerá invisível
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
                }

                /* Se a lista estiver vazia, será imprimido na tela que não foi encontrado
                 * ou registrado nenhum campo.
                 */
                else {
                    Toast.makeText(this, getString(R.string.FieldNotFound), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    /* Ao iniciar a Activity, será buscado no SharedPreferences o tema salvado.
     * Caso o estado retorna "true", será impostado o tema escuro.
     * Caso contrario, será impostado o tema padrão (claro)
     */
    private void setTheme() {
        SharedPreferencesTheme preferencesTheme = new SharedPreferencesTheme(this);
        if (preferencesTheme.getNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
    }

    /* Evento chamado pelo upButton onde ao clicá-lo será scrolado o recycleView para a posição 0
     * e o botão se tornará invisível.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floatingActionButtonUp) {
            recyclerView.scrollToPosition(0);
            upButton.setVisibility(View.INVISIBLE);
        }
    }
}

