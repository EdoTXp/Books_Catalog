/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.models

data class BookItem(
    val id: Int,
    var bookTitle: String,
    var authorName: String,
    var bookYear: Int,
)
