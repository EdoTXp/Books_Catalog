/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.customview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.BiblioLivro.criarlivros.R;
import com.BiblioLivro.criarlivros.activities.WindowPopUp;
import com.BiblioLivro.criarlivros.gestores.GestorVibrator;
import com.BiblioLivro.criarlivros.model.BookItem;
import com.BiblioLivro.criarlivros.model.Order;
import com.BiblioLivro.criarlivros.storage.DatabaseHelper;
import com.BiblioLivro.criarlivros.storage.SharedPreferencesTheme;

import org.jetbrains.annotations.NotNull;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class BookComponentAdapter extends RecyclerView.Adapter<BookComponentAdapter.BookViewHolder> {

    private Context mcontext;
    private View view;
    private ArrayList<BookItem> bookItems;
    private SharedPreferencesTheme preferencesTheme;


    /**
     * @param context   onde vai ser inserido o BookComponentAdapter
     * @param bookitems ArrayList de bookitem (int: Id, String: Título, String: Autor, int: Ano)
     */
    public BookComponentAdapter(Context context, ArrayList<BookItem> bookitems) {
        mcontext = context;
        bookItems = bookitems;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        view = inflater.inflate(R.layout.book_component, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookViewHolder holder, int position) {
        adaptTextViewOnScreen(holder.txtTitulo);
        adaptTextViewOnScreen(holder.txtAutor);

        holder.txtId.setText(String.valueOf(position + 1));
        holder.txtTitulo.setText(bookItems.get(position).getNomelivro());
        holder.txtAutor.setText(bookItems.get(position).getNomeautor());
        holder.txtAno.setText(String.valueOf(bookItems.get(position).getAnolivro()));

        final int Position = holder.getAdapterPosition();

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                GestorVibrator.Vibrate(100L, v.getContext());

                String Share = v.getResources().getString(R.string.txt_id).concat(": ").concat(String.valueOf(bookItems.get(Position).getId()).concat("\n")
                        .concat(v.getResources().getString(R.string.txt_titulo)).concat(": ").concat(bookItems.get(Position).getNomelivro()).concat("\n")
                        .concat(v.getResources().getString(R.string.txt_autor)).concat(": ").concat(bookItems.get(Position).getNomeautor()).concat("\n")
                        .concat(v.getResources().getString(R.string.txt_ano)).concat(": ").concat(String.valueOf(bookItems.get(Position).getAnolivro())));

                String URL = v.getResources().getString(R.string.Google_Search).concat(bookItems.get(Position).getNomelivro()).concat(", ").concat(bookItems.get(Position).getNomeautor());

                WindowPopUp windowPopUp = new WindowPopUp();
                windowPopUp.showPopUpWindow(v, URL, Share, (AppCompatActivity) v.getContext());

                return true;
            }
        });

        //TODO Aggiungere il codice per gli eventi di edit
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Click edit", Toast.LENGTH_LONG).show();
            }
        });

        holder.imgExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                preferencesTheme = new SharedPreferencesTheme(holder.itemView.getContext());

                if (preferencesTheme.getNightModeState())
                    builder = new AlertDialog.Builder(holder.itemView.getContext(), android.R.style.Theme_DeviceDefault_Dialog_Alert);
                else
                    builder = new AlertDialog.Builder(holder.itemView.getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                builder.setTitle(holder.itemView.getContext().getResources().getString(R.string.delete_item_Title).concat(bookItems.get(Position).getNomelivro()));
                builder.setMessage(R.string.delete_item_msg);
                builder.setIcon(R.drawable.iconapp);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAt(Position);
                    }
                });
                builder.setNegativeButton(R.string.no, null);
                builder.show();
            }
        });

        Animation animation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);
    }

    // este método adapta o texto a diferentes formatos de telas
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

    /* Este método serve para remover cada item através sua posição. Removendo da lista e do database
    e atualizando o recycleView
     */
    private void removeAt(int position) {
        try {
            Animation anim = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.slide_out_right);
            anim.setDuration(300);
            view.startAnimation(anim);
            DatabaseHelper db = new DatabaseHelper(view.getContext());

            db.deletar(bookItems.get(position).getId());
            bookItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, bookItems.size());

            GestorVibrator.Vibrate(100L, view.getContext());
            Toast.makeText(view.getContext(), R.string.success_msg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            GestorVibrator.Vibrate(100L, view.getContext());
            Toast.makeText(view.getContext(), R.string.error_msg +
                    "\n" +
                    e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void changeOrderOfRecycleView(final int id_order, final Order order, ArrayList<BookItem> bookItems) {
        Collections.sort(bookItems, new Comparator<BookItem>() {
            @Override
            public int compare(BookItem o1, BookItem o2) {
                switch (id_order) {
                    case 1:
                        if (order == Order.ASCENDANT)
                            return Normalizer.normalize(o1.getNomelivro().toUpperCase(), Normalizer.Form.NFD).compareTo(Normalizer.normalize(o2.getNomelivro().toUpperCase(), Normalizer.Form.NFD));
                        else
                            return Normalizer.normalize(o2.getNomelivro().toUpperCase(), Normalizer.Form.NFD).compareTo(Normalizer.normalize(o1.getNomelivro().toUpperCase(), Normalizer.Form.NFD));
                    case 2:
                        if (order == Order.ASCENDANT)
                            return Normalizer.normalize(o1.getNomeautor().toUpperCase(), Normalizer.Form.NFD).compareTo(Normalizer.normalize(o2.getNomeautor().toUpperCase(), Normalizer.Form.NFD));
                        else
                            return Normalizer.normalize(o2.getNomeautor().toUpperCase(), Normalizer.Form.NFD).compareTo(Normalizer.normalize(o1.getNomeautor().toUpperCase(), Normalizer.Form.NFD));
                    case 3:
                        if (order == Order.ASCENDANT)
                            return Integer.compare(o1.getAnolivro(), o2.getAnolivro());
                        else
                            return Integer.compare(o2.getAnolivro(), o1.getAnolivro());
                    default:
                        return 0;
                }

            }
        });
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bookItems.size();
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

}
