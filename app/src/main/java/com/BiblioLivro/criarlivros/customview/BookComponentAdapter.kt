/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */
package com.BiblioLivro.criarlivros.customview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.BiblioLivro.criarlivros.R
import com.BiblioLivro.criarlivros.activities.WindowPopUp
import com.BiblioLivro.criarlivros.gestores.GestorVibrator.vibrate
import com.BiblioLivro.criarlivros.model.BookItem
import com.BiblioLivro.criarlivros.model.Order
import com.BiblioLivro.criarlivros.storage.DatabaseHelper
import java.text.Normalizer
import java.util.*

class BookComponentAdapter

 (private val mContext: Context, private val bookItems: ArrayList<BookItem>) :
    RecyclerView.Adapter<BookViewHolder>() {
    private lateinit var view: View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val inflater = LayoutInflater.from(mContext)
        view = inflater.inflate(R.layout.book_component, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        // Preenchimento dos dados nos textViews
        val normalId = "${position + 1}"

        holder.txtId.text = (normalId)
        holder.txtTitulo.text = bookItems[position].bookTitle
        holder.txtAutor.text = bookItems[position].authorName
        holder.txtAno.text = bookItems[position].bookYear.toString()

        // adicionando eventos
        view.setOnLongClickListener { v: View ->
            vibrate(100L, v.context)
            val shareBook = """
                ${v.resources.getString(R.string.txt_id)}: ${bookItems[holder.adapterPosition].id}
                ${v.resources.getString(R.string.txt_titulo)}: ${bookItems[holder.adapterPosition].bookTitle}
                ${v.resources.getString(R.string.txt_autor)}: ${bookItems[holder.adapterPosition].authorName}
                ${v.resources.getString(R.string.txt_ano)}: ${bookItems[holder.adapterPosition].bookYear}
                """.trimIndent()
            val bookUrl = v.resources.getString(R.string.Google_Search)+bookItems[holder.adapterPosition].bookTitle + ", " + bookItems[holder.adapterPosition].authorName
            val windowPopUp = WindowPopUp()
            windowPopUp.showPopUpWindow(v, bookUrl, shareBook, (v.context as AppCompatActivity))
            true
        }
        holder.imgEdit.setOnClickListener { v: View ->
            val editOption = arrayOf(
                v.resources.getString(R.string.txt_titulo) + ": " + bookItems[holder.adapterPosition].bookTitle,
                v.resources.getString(R.string.txt_autor) + ": " + bookItems[holder.adapterPosition].authorName,
                v.resources.getString(R.string.txt_ano) + ": " + bookItems[holder.adapterPosition].bookYear.toString()
            )
            val editDialog = AlertDialog.Builder(v.context)
            editDialog.setTitle(v.context.resources.getString(R.string.edit_item_Title))
            editDialog.setIcon(R.drawable.transparent_icon_app)
            editDialog.setSingleChoiceItems(editOption, -1) { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
                editElementAt(holder.adapterPosition, which)
            }
            editDialog.setNegativeButton(R.string.email_btn_cancel, null)
            editDialog.show()
        }
        holder.imgExcluir.setOnClickListener { v: View ->
            val deleteDialog = AlertDialog.Builder(v.context)
            deleteDialog.setTitle(holder.itemView.context.resources.getString(R.string.delete_item_Title) + bookItems[holder.adapterPosition].bookTitle)
            deleteDialog.setMessage(R.string.delete_item_msg)
            deleteDialog.setIcon(R.drawable.transparent_icon_app)
            deleteDialog.setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int ->
                removeElementAt(
                    holder.adapterPosition
                )
            }
            deleteDialog.setNegativeButton(R.string.no, null)
            deleteDialog.show()
        }
        val animation = AnimationUtils.loadAnimation(
            view.context, android.R.anim.slide_in_left
        )
        holder.itemView.startAnimation(animation)
    }

    private fun removeElementAt(position: Int) {
        /* Este método serve para remover cada item através da sua posição.
         *  Removendo da lista e do database e atualizando o recycleView
         */
        try {
            val anim = AnimationUtils.loadAnimation(
                view.context, android.R.anim.slide_out_right
            )
            anim.duration = 300
            view.startAnimation(anim)
            val db = DatabaseHelper(view.context)
            val actualPosition = itemCount
            db.delete(bookItems[position].id)
            bookItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, actualPosition)
            vibrate(100L, view.context)
            Toast.makeText(view.context, R.string.success_msg, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            vibrate(100L, view.context)
            Toast.makeText(
                view.context,
                R.string.error_msg
                    .toString() + "\n"
                        + e,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * @param position é a posição do livro
     * @param choose   se divide em 0: título,
     * 1: autor,
     * 2: ano.
     * Será criado um AlertDialog com um EditText para modificar uma das opções
     * e atualizá-las na lista e no database e atualizando o recycleView.
     */
    private fun editElementAt(position: Int, @IntRange(from = 0, to = 2) choose: Int) {
        // criação do AlertDialog
        val editDialog = AlertDialog.Builder(view.context)
        editDialog.setMessage(R.string.fill_field_bellow)
        // criação do EditText
        val textToChange = EditText(view.context)
        //adicionando um filtro para o editText limitando para 100 caractéres
        textToChange.filters = arrayOf<InputFilter>(LengthFilter(100))

        /*
         * Dependendo do choose,
         * será colocado o ícone e o título do AlertDialog,
         * o hint do editText*/
        when (choose) {
            0 -> {
                editDialog.setIcon(R.drawable.book_img)
                editDialog.setTitle(R.string.txt_titulo)
                textToChange.setHint(R.string.hint_titulo)
            }
            1 -> {
                editDialog.setIcon(R.drawable.author_img)
                editDialog.setTitle(R.string.txt_autor)
                textToChange.setHint(R.string.hint_autor)
            }
            else -> {
                editDialog.setIcon(R.drawable.calendar_img)
                editDialog.setTitle(R.string.txt_ano)
                textToChange.setHint(R.string.hint_ano)
                //adicionando um input de somente 4 números
                textToChange.inputType = InputType.TYPE_CLASS_NUMBER
                textToChange.filters = arrayOf<InputFilter>(LengthFilter(4))
            }
        }
        editDialog.setView(textToChange)
        editDialog.setPositiveButton(R.string.btn_Accept) { _: DialogInterface?, _: Int ->
            if (textToChange.text.toString() != "") {
                try {
                    val db = DatabaseHelper(view.context)
                    val cv = ContentValues()
                    when (choose) {
                        0 -> {
                            cv.put("titulo", textToChange.text.toString())
                            db.update(bookItems[position].id, cv)
                            bookItems[position].bookTitle = textToChange.text.toString()
                        }
                        1 -> {
                            cv.put("autor", textToChange.text.toString())
                            db.update(bookItems[position].id, cv)
                            bookItems[position].authorName = textToChange.text.toString()
                        }
                        2 -> {
                            cv.put("ano", textToChange.text.toString())
                            db.update(bookItems[position].id, cv)
                            bookItems[position].bookYear = textToChange.text.toString().toInt()
                        }
                        else -> {}
                    }
                    notifyItemChanged(position)
                    vibrate(100L, view.context)
                    Toast.makeText(view.context, R.string.success_msg, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    vibrate(100L, view.context)
                    Toast.makeText(
                        view.context,
                        R.string.error_msg
                            .toString() + "\n"
                                + e,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else Toast.makeText(view.context, R.string.email_notextinsert, Toast.LENGTH_SHORT)
                .show()
        }
        editDialog.setNegativeButton(R.string.email_btn_cancel) { _: DialogInterface?, _: Int ->
            Toast.makeText(
                view.context,
                view.resources.getString(R.string.canceled_operation),
                Toast.LENGTH_SHORT
            ).show()
        }
        editDialog.show()
    }

    /**
     * Os livros serão ordenados pelos (título, autor e ano) em ordem (ascendente e descendente)
     *
     * @param order_by vai dos números 1 ao 3 onde:
     * 1 - título do livro.
     * 2 - autor do livro.
     * 3 - ano do livro.
     * @param order    se divide em:
     * ASCENDANT - em ascendente.
     * DESCENDANT - em descendente.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setSortOfAdapterView(@IntRange(from = 1, to = 3) order_by: Int, order: Order) {
        bookItems.sortWith { o1: BookItem, o2: BookItem ->
            if (bookItems.size > 1) {
                when (order_by) {
                    1 -> if (order === Order.ASCENDANT) // ascendente
                        return@sortWith Normalizer.normalize(
                            o1.bookTitle.uppercase(Locale.getDefault()),
                            Normalizer.Form.NFD
                        )
                            .compareTo(
                                Normalizer.normalize(
                                    o2.bookTitle.uppercase(Locale.getDefault()),
                                    Normalizer.Form.NFD
                                )
                            ) else  // descendente
                        return@sortWith Normalizer.normalize(
                            o2.bookTitle.uppercase(Locale.getDefault()),
                            Normalizer.Form.NFD
                        ).compareTo(
                            Normalizer.normalize(
                                o1.bookTitle.uppercase(Locale.getDefault()),
                                Normalizer.Form.NFD
                            )
                        )
                    2 -> if (order === Order.ASCENDANT) // ascendente
                        return@sortWith Normalizer.normalize(
                            o1.authorName.uppercase(Locale.getDefault()),
                            Normalizer.Form.NFD
                        )
                            .compareTo(
                                Normalizer.normalize(
                                    o2.authorName.uppercase(Locale.getDefault()),
                                    Normalizer.Form.NFD
                                )
                            ) else  // descendente
                        return@sortWith Normalizer.normalize(
                            o2.authorName.uppercase(Locale.getDefault()),
                            Normalizer.Form.NFD
                        )
                            .compareTo(
                                Normalizer.normalize(
                                    o1.authorName.uppercase(Locale.getDefault()),
                                    Normalizer.Form.NFD
                                )
                            )
                    3 -> if (order === Order.ASCENDANT) // ascendente
                        return@sortWith o1.bookYear.compareTo(o2.bookYear) else  // descendente
                        return@sortWith o2.bookYear.compareTo(o1.bookYear)
                    else -> return@sortWith 0
                }
            } else return@sortWith 0
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return bookItems.size
    }

    fun itemIsEmpty(): Boolean {
        return itemCount == 0
    }
}