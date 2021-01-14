/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.customview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class BookComponentAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private final Context mContext;
    private View view;
    private final ArrayList<BookItem> bookItems;

    /**
     * @param context   onde vai ser inserido o BookComponentAdapter
     * @param bookitems ArrayList de bookitem (int: Id, String: Título, String: Autor, int: Ano)
     */
    public BookComponentAdapter(Context context, ArrayList<BookItem> bookitems) {
        mContext = context;
        bookItems = bookitems;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.book_component, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookViewHolder holder, int position) {
        // Preenchimento dos dados nos textViews
        holder.txtId.setText(String.valueOf(position + 1));
        holder.txtTitulo.setText(bookItems.get(position).getBookName());
        holder.txtAutor.setText(bookItems.get(position).getAuthorName());
        holder.txtAno.setText(String.valueOf(bookItems.get(position).getBookYear()));

        // adicionando eventos
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                GestorVibrator.Vibrate(100L, v.getContext());

                String Share = v.getResources().getString(R.string.txt_id).concat(": ").concat(String.valueOf(bookItems.get(holder.getAdapterPosition()).getId()).concat("\n")
                        .concat(v.getResources().getString(R.string.txt_titulo)).concat(": ").concat(bookItems.get(holder.getAdapterPosition()).getBookName()).concat("\n")
                        .concat(v.getResources().getString(R.string.txt_autor)).concat(": ").concat(bookItems.get(holder.getAdapterPosition()).getAuthorName()).concat("\n")
                        .concat(v.getResources().getString(R.string.txt_ano)).concat(": ").concat(String.valueOf(bookItems.get(holder.getAdapterPosition()).getBookYear())));

                String URL = v.getResources().getString(R.string.Google_Search)
                        .concat(bookItems.get(holder.getAdapterPosition()).getBookName())
                        .concat(", ").concat(bookItems.get(holder.getAdapterPosition()).getAuthorName());

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
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(v.getContext());

                deleteDialog.setTitle(holder.itemView.getContext().getResources().getString(R.string.delete_item_Title).concat(bookItems.get(holder.getAdapterPosition()).getBookName()));
                deleteDialog.setMessage(R.string.delete_item_msg);
                deleteDialog.setIcon(R.drawable.transparent_icon_app);
                deleteDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeElementAt(holder.getAdapterPosition());
                    }
                });
                deleteDialog.setNegativeButton(R.string.no, null);
                deleteDialog.show();
            }
        });

        Animation animation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);
    }


    private void removeElementAt(int position) {

        /* Este método serve para remover cada item através sua posição.
         *  Removendo da lista e do database e atualizando o recycleView
         */

        try {
            Animation anim = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.slide_out_right);
            anim.setDuration(300);

            view.startAnimation(anim);
            DatabaseHelper db = new DatabaseHelper(view.getContext());
            int actualPosition = getItemCount();

            db.delete(bookItems.get(position).getId());
            bookItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, actualPosition);

            GestorVibrator.Vibrate(100L, view.getContext());
            Toast.makeText(view.getContext(), R.string.success_msg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            GestorVibrator.Vibrate(100L, view.getContext());
            Toast.makeText
                    (
                            view.getContext(),
                            R.string.error_msg
                                    + "\n"
                                    + e.toString(),
                            Toast.LENGTH_LONG
                    ).show();
        }
    }

   /* TODO "FINIRE QUESTO METODO" private void editElementAt(int position) {
        try {

        } catch (Exception e) {

        }
    } */

    public void setSortOfAdapterView(final int order_by, final Order order) {
        Collections.sort(bookItems, new Comparator<BookItem>() {
            @Override
            public int compare(BookItem o1, BookItem o2) {
                if (bookItems.size() > 1) {
                    switch (order_by) {
                        case 1:
                            if (order == Order.ASCENDANT)
                                return Normalizer.normalize(o1.getBookName().toUpperCase(), Normalizer.Form.NFD)
                                        .compareTo(Normalizer.normalize(o2.getBookName().toUpperCase(), Normalizer.Form.NFD));
                            else
                                return Normalizer.normalize(o2.getBookName().toUpperCase(), Normalizer.Form.NFD).
                                        compareTo(Normalizer.normalize(o1.getBookName().toUpperCase(), Normalizer.Form.NFD));

                        case 2:
                            if (order == Order.ASCENDANT)
                                return Normalizer.normalize(o1.getAuthorName().toUpperCase(), Normalizer.Form.NFD)
                                        .compareTo(Normalizer.normalize(o2.getAuthorName().toUpperCase(), Normalizer.Form.NFD));
                            else
                                return Normalizer.normalize(o2.getAuthorName().toUpperCase(), Normalizer.Form.NFD)
                                        .compareTo(Normalizer.normalize(o1.getAuthorName().toUpperCase(), Normalizer.Form.NFD));

                        case 3:
                            if (order == Order.ASCENDANT)
                                return Integer.compare(o1.getBookYear(), o2.getBookYear());
                            else
                                return Integer.compare(o2.getBookYear(), o1.getBookYear());

                        default:
                            return 0;
                    }
                } else
                    return 0;
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bookItems.size();
    }

}
