/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.model;

public class BookItem {

    private int Id;
    private String nomelivro, nomeautor;
    private int anolivro;

    public BookItem(int id, String titulo, String autor, int ano) {
        Id = id;
        nomelivro = titulo;
        nomeautor = autor;
        anolivro = ano;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNomelivro() {
        return nomelivro;
    }

    public void setNomelivro(String nomelivro) {
        this.nomelivro = nomelivro;
    }

    public String getNomeautor() {
        return nomeautor;
    }

    public void setNomeautor(String nomeautor) {
        this.nomeautor = nomeautor;
    }

    public int getAnolivro() {
        return anolivro;
    }

    public void setAnolivro(int anolivro) {
        this.anolivro = anolivro;
    }

}
