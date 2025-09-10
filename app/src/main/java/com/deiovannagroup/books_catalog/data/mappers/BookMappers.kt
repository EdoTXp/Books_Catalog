package com.deiovannagroup.books_catalog.data.mappers

import com.deiovannagroup.books_catalog.data.models.BookEntity
import com.deiovannagroup.books_catalog.domain.models.Book

fun BookEntity.toBook(): Book {
    return Book(
        id = this.id,
        title = this.title,
        author = this.author,
        year = this.year
    )
}

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = this.id,
        title = this.title,
        author = this.author,
        year = this.year
    )
}