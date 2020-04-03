/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.customview.BookComponentAdapter;
import com.BiblioLivro.criarlivros.model.BookItem;
import com.BiblioLivro.criarlivros.storage.DatabaseHelper;
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TelaPesquisar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferencesTheme preferencesTheme = new SharedPreferencesTheme(this);
        if (preferencesTheme.getNightModeState()) {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pesquisar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_activity_tela_pesquisar);


        /*
         * Intent recebe dois paramentros:
         * o Tipo: recebe o id do RadioGroup passado na TelaPrincipal e na Notificação o id: R.id.rbPesquisarPorTodos
         * a chave: O valor do texto recebido na TelaPrincipal e no caso da notificação, um texto vazio
         * */
        Intent it = getIntent();

        if (it != null) {

            // preenchimento dos parametros para passar pra buscar no database
            int tipo = it.getIntExtra("tipo", 0);
            String chave = it.getStringExtra("chave");

            List<ContentValues> lista = new ArrayList<>();

            //realização da busca por Título
            if (tipo == R.id.rbPesquisarPorTitulo) {
                lista = new DatabaseHelper(this).pesquisarPorTitulo(chave);
            }

            /*
             * realização da busca por ano
             * se o ano não encontrar nenhum valor inteiro será feita uma busca por todos os objectos da lista
             * */
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

            //realização da busca por todos os objectos
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

                    RecyclerView recyclerView = findViewById(R.id.rv_pesquisar);


                    ArrayList<BookItem> bookItems = new ArrayList<>();
                    for (ContentValues cv : lista) {
                        BookItem bookItem = new BookItem(cv.getAsInteger("id"), cv.getAsString("titulo"), cv.getAsString("autor"), cv.getAsInteger("ano"));
                        bookItems.add(bookItem);
                    }

                    BookComponentAdapter bookadapter = new BookComponentAdapter(this, bookItems);
                    recyclerView.setAdapter(bookadapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));

                }
                // Se a lista for vazia será imprimido na tela que não foi encontrado ou registrado nenhum campo
                else {
                    Toast.makeText(this, getString(R.string.FieldNotFound), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}

