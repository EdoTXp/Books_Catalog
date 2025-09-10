/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.domain.models

data class Book(
    val id: Int,
    var title: String,
    var author: String,
    var year: Int,
)
