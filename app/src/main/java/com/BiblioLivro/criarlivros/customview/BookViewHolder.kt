/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.customview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.BiblioLivro.criarlivros.R;

public class BookViewHolder extends RecyclerView.ViewHolder {

    final TextView txtId;
    final TextView txtTitulo;
    final TextView txtAutor;
    final TextView txtAno;
    final ImageView imgEdit;
    final ImageView imgExcluir;

    BookViewHolder(@NonNull View itemView) {
        super(itemView);

        txtId = itemView.findViewById(R.id.book_component_ID);
        txtTitulo = itemView.findViewById(R.id.txt_titlebookcomponent);
        txtAutor = itemView.findViewById(R.id.txtauthorbookcomponent);
        txtAno = itemView.findViewById(R.id.txt_anobookcomponent);

        imgEdit = itemView.findViewById(R.id.img_bookedit);
        imgExcluir = itemView.findViewById(R.id.img_bookdelete);
    }
}
