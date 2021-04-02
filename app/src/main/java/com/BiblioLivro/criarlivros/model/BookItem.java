/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.model;

public class BookItem {

    private final int Id;
    private String bookTitle, authorName;
    private int bookYear;

    public BookItem(int id, String title, String author, int year) {
        Id = id;
        bookTitle = title;
        authorName = author;
        bookYear = year;
    }

    public int getId() {
        return Id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getBookYear() {
        return bookYear;
    }

    public void setBookYear(int bookYear) {
        this.bookYear = bookYear;
    }

}
