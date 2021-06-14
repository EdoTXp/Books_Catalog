/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.customview;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.IntRange;
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
        holder.txtTitulo.setText(bookItems.get(position).getBookTitle());
        holder.txtAutor.setText(bookItems.get(position).getAuthorName());
        holder.txtAno.setText(String.valueOf(bookItems.get(position).getBookYear()));

        // adicionando eventos
        view.setOnLongClickListener(v -> {
            GestorVibrator.Vibrate(100L, v.getContext());

            String Share = v.getResources().getString(R.string.txt_id).concat(": ").concat(String.valueOf(bookItems.get(holder.getAdapterPosition()).getId()).concat("\n")
                    .concat(v.getResources().getString(R.string.txt_titulo)).concat(": ").concat(bookItems.get(holder.getAdapterPosition()).getBookTitle()).concat("\n")
                    .concat(v.getResources().getString(R.string.txt_autor)).concat(": ").concat(bookItems.get(holder.getAdapterPosition()).getAuthorName()).concat("\n")
                    .concat(v.getResources().getString(R.string.txt_ano)).concat(": ").concat(String.valueOf(bookItems.get(holder.getAdapterPosition()).getBookYear())));

            String URL = v.getResources().getString(R.string.Google_Search)
                    .concat(bookItems.get(holder.getAdapterPosition()).getBookTitle())
                    .concat(", ").concat(bookItems.get(holder.getAdapterPosition()).getAuthorName());

            WindowPopUp windowPopUp = new WindowPopUp();
            windowPopUp.showPopUpWindow(v, URL, Share, (AppCompatActivity) v.getContext());

            return true;
        });


        holder.imgEdit.setOnClickListener(v -> {
            final String[] editOption = {
                    v.getResources().getString(R.string.txt_titulo).concat(": ").concat(bookItems.get(holder.getAdapterPosition()).getBookTitle()),
                    v.getResources().getString(R.string.txt_autor).concat(": ").concat(bookItems.get(holder.getAdapterPosition()).getAuthorName()),
                    v.getResources().getString(R.string.txt_ano).concat(": ").concat(Integer.toString(bookItems.get(holder.getAdapterPosition()).getBookYear()))
            };


            AlertDialog.Builder editDialog = new AlertDialog.Builder(v.getContext());

            editDialog.setTitle(v.getContext().getResources().getString(R.string.edit_item_Title));
            editDialog.setIcon(R.drawable.transparent_icon_app);
            editDialog.setSingleChoiceItems(editOption, -1, (dialog, which) -> {
                dialog.dismiss();
                editElementAt(holder.getAdapterPosition(), which);

            });
            editDialog.setNegativeButton(R.string.email_btn_cancel, null);
            editDialog.show();
        });

        holder.imgExcluir.setOnClickListener(v -> {
            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(v.getContext());

            deleteDialog.setTitle(holder.itemView.getContext().getResources().getString(R.string.delete_item_Title).concat(bookItems.get(holder.getAdapterPosition()).getBookTitle()));
            deleteDialog.setMessage(R.string.delete_item_msg);
            deleteDialog.setIcon(R.drawable.transparent_icon_app);
            deleteDialog.setPositiveButton(R.string.yes, (dialog, which) -> removeElementAt(holder.getAdapterPosition()));
            deleteDialog.setNegativeButton(R.string.no, null);
            deleteDialog.show();
        });

        Animation animation = AnimationUtils.loadAnimation(view.getContext(), android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);
    }


    private void removeElementAt(int position) {
        /* Este método serve para remover cada item através da sua posição.
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
            Toast.makeText(view.getContext(), R.string.success_msg, Toast.LENGTH_SHORT).show();
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

    /**
     * @param position é a posição do livro
     * @param choose   se divide em 0: título,
     *                 1: autor,
     *                 2: ano.
     *                 Será criado um AlertDialog com um EditText para modificar uma das opções
     *                 e atualizá-las na lista e no database e atualizando o recycleView.
     */
    private void editElementAt(final int position, @IntRange(from = 0, to = 2) final int choose) {
        // criação do AlertDialog
        final AlertDialog.Builder editDialog = new AlertDialog.Builder(view.getContext());
        editDialog.setMessage(R.string.fill_field_bellow);
        // criação do EditText
        final EditText textToChange = new EditText(view.getContext());
        //adicionando um filtro para o editText limitando para 100 caractéres
        textToChange.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

        /*
         * Dependendo do choose,
         * será colocado o ícone e o título do AlertDialog,
         * o hint do editText*/
        if (choose == 0) {
            editDialog.setIcon(R.drawable.book_img);
            editDialog.setTitle(R.string.txt_titulo);
            textToChange.setHint(R.string.hint_titulo);
        } else if (choose == 1) {
            editDialog.setIcon(R.drawable.author_img);
            editDialog.setTitle(R.string.txt_autor);
            textToChange.setHint(R.string.hint_autor);
        } else {
            editDialog.setIcon(R.drawable.calendar_img);
            editDialog.setTitle(R.string.txt_ano);
            textToChange.setHint(R.string.hint_ano);
            //adicionando um input de somente 4 números
            textToChange.setInputType(InputType.TYPE_CLASS_NUMBER);
            textToChange.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        }

        editDialog.setView(textToChange);
        editDialog.setPositiveButton(R.string.btn_Accept, (dialog, which) -> {
            if (!textToChange.getText().toString().equals("")) {
                try {
                    DatabaseHelper db = new DatabaseHelper(view.getContext());
                    ContentValues cv = new ContentValues();

                    switch (choose) {
                        case 0:
                            cv.put("titulo", textToChange.getText().toString());
                            db.update(bookItems.get(position).getId(), cv);
                            bookItems.get(position).setBookTitle(textToChange.getText().toString());
                            break;

                        case 1:
                            cv.put("autor", textToChange.getText().toString());
                            db.update(bookItems.get(position).getId(), cv);
                            bookItems.get(position).setAuthorName(textToChange.getText().toString());
                            break;

                        case 2:
                            cv.put("ano", textToChange.getText().toString());
                            db.update(bookItems.get(position).getId(), cv);
                            bookItems.get(position).setBookYear(Integer.parseInt(textToChange.getText().toString()));
                            break;

                        default:
                            break;

                    }

                    notifyItemChanged(position);
                    GestorVibrator.Vibrate(100L, view.getContext());
                    Toast.makeText(view.getContext(), R.string.success_msg, Toast.LENGTH_SHORT).show();

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
            } else
                Toast.makeText(view.getContext(), R.string.email_notextinsert, Toast.LENGTH_SHORT).show();

        });

        editDialog.setNegativeButton(R.string.email_btn_cancel, (dialog, which) -> Toast.makeText(view.getContext(), view.getResources().getString(R.string.canceled_operation), Toast.LENGTH_SHORT).show());
        editDialog.show();
    }

    /**
     * Os livros serão ordenados pelos (título, autor e ano) em ordem (ascendente e descendente)
     *
     * @param order_by vai dos números 1 ao 3 onde:
     *                 1 - título do livro.
     *                 2 - autor do livro.
     *                 3 - ano do livro.
     * @param order    se divide em:
     *                 ASCENDANT - em ascendente.
     *                 DESCENDANT - em descendente.
     */
    public void setSortOfAdapterView(@IntRange(from = 1, to = 3) final int order_by, final Order order) {
        Collections.sort(bookItems, (o1, o2) -> {
            if (bookItems.size() > 1) {
                switch (order_by) {
                    case 1: // ordenar pelo títolo
                        if (order == Order.ASCENDANT) // ascendente
                            return Normalizer.normalize(o1.getBookTitle().toUpperCase(), Normalizer.Form.NFD)
                                    .compareTo(Normalizer.normalize(o2.getBookTitle().toUpperCase(), Normalizer.Form.NFD));
                        else // descendente
                            return Normalizer.normalize(o2.getBookTitle().toUpperCase(), Normalizer.Form.NFD).
                                    compareTo(Normalizer.normalize(o1.getBookTitle().toUpperCase(), Normalizer.Form.NFD));

                    case 2: // ordenar pelo autor
                        if (order == Order.ASCENDANT) // ascendente
                            return Normalizer.normalize(o1.getAuthorName().toUpperCase(), Normalizer.Form.NFD)
                                    .compareTo(Normalizer.normalize(o2.getAuthorName().toUpperCase(), Normalizer.Form.NFD));
                        else // descendente
                            return Normalizer.normalize(o2.getAuthorName().toUpperCase(), Normalizer.Form.NFD)
                                    .compareTo(Normalizer.normalize(o1.getAuthorName().toUpperCase(), Normalizer.Form.NFD));

                    case 3: // ordenar pelo ano
                        if (order == Order.ASCENDANT) // ascendente
                            return Integer.compare(o1.getBookYear(), o2.getBookYear());
                        else // descendente
                            return Integer.compare(o2.getBookYear(), o1.getBookYear());

                    default:
                        return 0;
                }
            } else
                return 0;
        });
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bookItems.size();
    }

    public boolean itemIsEmpty() {
        return getItemCount() == 0;
    }

}
