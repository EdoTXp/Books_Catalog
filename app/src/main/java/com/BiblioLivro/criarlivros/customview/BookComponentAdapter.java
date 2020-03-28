/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.customview;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.activities.WindowPopUp;
import com.BiblioLivro.criarlivros.gestores.GestorVibrator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class BookComponentAdapter extends RecyclerView.Adapter<BookComponentAdapter.BookViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<Integer> Id;
    private ArrayList<String> Titulo, Autor;
    private ArrayList<Integer> Ano;
    private Context mcontext;
    private int itemCount;
    private int Position;


    public BookComponentAdapter(Context context, ArrayList<Integer> id, ArrayList<String> titolo, ArrayList<String> autor, ArrayList<Integer> ano, int count) {
        mcontext = context;
        Id = id;
        Titulo = titolo;
        Autor = autor;
        Ano = ano;
        itemCount = count;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate(R.layout.book_component, parent, false);
        view.setOnLongClickListener(this);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Position = holder.getLayoutPosition();

        adaptTextViewOnScreen(holder.txtTitulo);
        adaptTextViewOnScreen(holder.txtAutor);

        holder.txtId.setText(String.valueOf(Id.get(position)));
        holder.txtTitulo.setText(Titulo.get(position));
        holder.txtAutor.setText(Autor.get(position));
        holder.txtAno.setText(String.valueOf(Ano.get(position)));

        holder.imgEdit.setOnClickListener(this);
        holder.imgExcluir.setOnClickListener(this);
    }

    private void adaptTextViewOnScreen(@NotNull TextView textView) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mcontext).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        ViewGroup.LayoutParams params = textView.getLayoutParams();
        if (displayMetrics.widthPixels > 480) {
            params.width = mcontext.getResources().getDimensionPixelSize(R.dimen.TextViewResolution1920);
            textView.setLayoutParams(params);
        } else {
            params.width = mcontext.getResources().getDimensionPixelSize(R.dimen.TextViewResolution800);
            textView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }


    static class BookViewHolder extends RecyclerView.ViewHolder {

        TextView txtId, txtTitulo, txtAutor, txtAno;
        ImageView imgEdit, imgExcluir;

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_bookedit:
                Toast.makeText(v.getContext(), "Click edit", Toast.LENGTH_LONG).show();
                break;
            case R.id.img_bookdelete:
                Toast.makeText(v.getContext(), "Click remove", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        GestorVibrator.Vibrate(100L, (AppCompatActivity) v.getContext());

        String Share = v.getResources().getString(R.string.txt_id).concat(": ").concat(String.valueOf(Id.get(Position))).concat("\n")
                .concat(v.getResources().getString(R.string.txt_titulo)).concat(": ").concat(Titulo.get(Position)).concat("\n")
                .concat(v.getResources().getString(R.string.txt_autor)).concat(": ").concat(Autor.get(Position)).concat("\n")
                .concat(v.getResources().getString(R.string.txt_ano)).concat(": ").concat(String.valueOf(Ano.get(Position)));

        String URL = v.getResources().getString(R.string.Google_Search).concat(Titulo.get(Position)).concat(", ").concat(Autor.get(Position));

        WindowPopUp windowPopUp = new WindowPopUp();
        windowPopUp.showPopUpWindow(v, URL, Share, (AppCompatActivity) v.getContext());
        return false;
    }
}
