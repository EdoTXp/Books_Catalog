/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */
package com.BiblioLivro.criarlivros.customview

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.BiblioLivro.criarlivros.R

class BookViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @JvmField
    val txtId: TextView
    @JvmField
    val txtTitulo: TextView
    @JvmField
    val txtAutor: TextView
    @JvmField
    val txtAno: TextView
    @JvmField
    val imgEdit: ImageView
    @JvmField
    val imgExcluir: ImageView

    init {
        txtId = itemView.findViewById(R.id.book_component_ID)
        txtTitulo = itemView.findViewById(R.id.txt_titlebookcomponent)
        txtAutor = itemView.findViewById(R.id.txtauthorbookcomponent)
        txtAno = itemView.findViewById(R.id.txt_anobookcomponent)
        imgEdit = itemView.findViewById(R.id.img_bookedit)
        imgExcluir = itemView.findViewById(R.id.img_bookdelete)
    }
}