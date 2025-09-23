/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.deiovannagroup.books_catalog.databinding.BookComponentBinding
import com.deiovannagroup.books_catalog.domain.models.Book


class BookComponentAdapter(
    private val onEditBook: (Book) -> Unit,
    private val onLongClickBook: (Book) -> Unit,
    private val onDeleteBook: (Book) -> Unit
) :
    ListAdapter<Book, BookComponentAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = BookComponentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.bind(book, position)
    }

    inner class BookViewHolder(private val binding: BookComponentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book, position: Int) {
            val positionToText = (position + 1).toString()
            binding.bookComponentID.text = positionToText
            binding.txtTitlebookcomponent.text = book.title
            binding.txtauthorbookcomponent.text = book.author
            binding.txtAnobookcomponent.text = book.year.toString()


            binding.imgBookdelete.setOnClickListener {
                onDeleteBook(book)
            }

            binding.imgBookedit.setOnClickListener {
                onEditBook(book)
            }

            binding.root.setOnLongClickListener {
                onLongClickBook(book)
                true
            }
        }
    }
}

class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem == newItem
    }
}