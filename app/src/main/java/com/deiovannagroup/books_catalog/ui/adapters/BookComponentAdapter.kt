/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.deiovannagroup.books_catalog.ui.adapters

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.DialogInterface
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.deiovannagroup.books_catalog.R
import com.deiovannagroup.books_catalog.domain.services.app_services.VibratorService
import com.deiovannagroup.books_catalog.domain.models.Book
import com.deiovannagroup.books_catalog.shared.enums.Order
import com.deiovannagroup.books_catalog.ui.views.activities.WindowPopUp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.Normalizer
import java.util.*

class BookComponentAdapter(
    //  private val mContext: Context,
    private val bookItems: ArrayList<Book>,
) : RecyclerView.Adapter<BookComponentAdapter.BookViewHolder>() {
    private lateinit var itemView: View

    inner class BookViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtId: TextView = itemView.findViewById(R.id.book_component_ID)
        val txtTitulo: TextView = itemView.findViewById(R.id.txt_titlebookcomponent)
        val txtAutor: TextView = itemView.findViewById(R.id.txtauthorbookcomponent)
        val txtAno: TextView = itemView.findViewById(R.id.txt_anobookcomponent)
        val imgEdit: ImageView = itemView.findViewById(R.id.img_bookedit)
        val imgExcluir: ImageView = itemView.findViewById(R.id.img_bookdelete)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        itemView = inflater.inflate(
            R.layout.book_component,
            parent,
            false,
        )
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        // Preenchimento dos dados nos textViews
        val normalId = "${position + 1}"

        holder.txtId.text = (normalId)
        holder.txtTitulo.text = bookItems[position].title
        holder.txtAutor.text = bookItems[position].author
        holder.txtAno.text = bookItems[position].year.toString()

        // adicionando eventos
        itemView.setOnLongClickListener { v: View ->
            VibratorService.vibrate(
                v.context,
                100L,
            )

            val shareBook = """
                ${v.resources.getString(R.string.txt_id)}: ${bookItems[holder.adapterPosition].id}
                ${v.resources.getString(R.string.txt_titulo)}: ${bookItems[holder.adapterPosition].title}
                ${v.resources.getString(R.string.txt_autor)}: ${bookItems[holder.adapterPosition].author}
                ${v.resources.getString(R.string.txt_ano)}: ${bookItems[holder.adapterPosition].year}
                """.trimIndent()

            val bookUrl =
                v.resources.getString(R.string.Google_Search) + bookItems[holder.adapterPosition].title + ", " + bookItems[holder.adapterPosition].author

            val windowPopUp = WindowPopUp()
            windowPopUp.showPopUpWindow(
                v,
                bookUrl,
                shareBook,
                (v.context as AppCompatActivity),
            )
            true
        }
        holder.imgEdit.setOnClickListener { v: View ->
            val editOption = arrayOf(
                v.resources.getString(R.string.txt_titulo) + ": " + bookItems[holder.adapterPosition].title,
                v.resources.getString(R.string.txt_autor) + ": " + bookItems[holder.adapterPosition].author,
                v.resources.getString(R.string.txt_ano) + ": " + bookItems[holder.adapterPosition].year.toString()
            )
            val editDialog = MaterialAlertDialogBuilder(v.context)
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
            val deleteDialog = MaterialAlertDialogBuilder(v.context)
            deleteDialog.setTitle(holder.itemView.context.resources.getString(R.string.delete_item_Title) + bookItems[holder.adapterPosition].title)
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
            itemView.context, android.R.anim.slide_in_left
        )
        holder.itemView.startAnimation(animation)
    }

    private fun removeElementAt(position: Int) {

        try {
            val anim = AnimationUtils.loadAnimation(
                itemView.context,
                android.R.anim.slide_out_right,
            )
            anim.duration = 300
            itemView.startAnimation(anim)
        //    val db = DatabaseHelperImpl(itemView.context)
            val actualPosition = itemCount
   //         db.delete(bookItems[position].id)
            bookItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(
                position,
                actualPosition,
            )

            VibratorService.vibrate(
                itemView.context,
                100L,
            )

            Toast.makeText(
                itemView.context,
                R.string.success_msg,
                Toast.LENGTH_SHORT,
            ).show()

        } catch (e: Exception) {
            VibratorService.vibrate(
                itemView.context,
                100L,
            )

            Toast.makeText(
                itemView.context,
                R.string.error_msg.toString() + "\n" + e,
                Toast.LENGTH_LONG,
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
        val context = itemView.context

        // criação do AlertDialog
        val editDialog = MaterialAlertDialogBuilder(context)
        editDialog.setMessage(R.string.fill_field_bellow)
        // criação do EditText
        val textToChange = EditText(context)
        //adicionando um filtro para o editText limitando para 100 caractéres
        textToChange.filters = arrayOf<InputFilter>(LengthFilter(100))
        textToChange.setTextColor(context.getColor(R.color.textColor))

        /*
         * Dependendo do choose,
         * será colocado o ícone e, título do AlertDialog e o hint do editText*/
        when (choose) {
            0 -> {
                editDialog.setTitle(R.string.txt_titulo)
                textToChange.setHint(R.string.hint_titulo)
            }

            1 -> {
                editDialog.setTitle(R.string.txt_autor)
                textToChange.setHint(R.string.hint_autor)
            }

            else -> {
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
                   // val db = DatabaseHelperImpl(context)
                    val cv = ContentValues()
                    when (choose) {
                        0 -> {
                            cv.put("titulo", textToChange.text.toString())
                       //     db.update(bookItems[position].id, cv)
                            bookItems[position].title = textToChange.text.toString()
                        }

                        1 -> {
                            cv.put("autor", textToChange.text.toString())
                         //   db.update(bookItems[position].id, cv)
                            bookItems[position].author = textToChange.text.toString()
                        }

                        2 -> {
                            cv.put("ano", textToChange.text.toString())
                         //   db.update(bookItems[position].id, cv)
                            bookItems[position].year = textToChange.text.toString().toInt()
                        }

                        else -> {}
                    }
                    notifyItemChanged(position)
                    VibratorService.vibrate(
                        context,
                        100L,
                    )

                    Toast.makeText(
                        context,
                        R.string.success_msg,
                        Toast.LENGTH_SHORT,
                    )
                        .show()
                } catch (e: Exception) {
                    VibratorService.vibrate(
                        context,
                        100L,
                    )
                    Toast.makeText(
                        context,
                        R.string.error_msg.toString() + "\n" + e,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else Toast.makeText(
                context,
                R.string.email_notextinsert,
                Toast.LENGTH_SHORT,
            )
                .show()
        }
        editDialog.setNegativeButton(R.string.email_btn_cancel) { _: DialogInterface?, _: Int ->
            Toast.makeText(
                context,
                itemView.resources.getString(R.string.canceled_operation),
                Toast.LENGTH_SHORT
            ).show()
        }
        editDialog.show()
    }

    /**
     * Os livros serão ordenados pelos (título, autor e ano) em ordem (ascendente e descendente)
     *
     * @param orderBy vai dos números 1 ao 3 onde:
     * 1 - título do livro.
     * 2 - autor do livro.
     * 3 - ano do livro.
     * @param order    se divide em:
     * ASCENDANT - em ascendente.
     * DESCENDANT - em descendente.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setSortOfAdapterView(@IntRange(from = 1, to = 3) orderBy: Int, order: Order) {
        bookItems.sortWith { o1: Book, o2: Book ->
            if (bookItems.size > 1) {
                when (orderBy) {
                    1 -> if (order === Order.ASCENDANT) // ascendente
                        return@sortWith Normalizer.normalize(
                            o1.title.uppercase(Locale.getDefault()), Normalizer.Form.NFD
                        ).compareTo(
                            Normalizer.normalize(
                                o2.title.uppercase(Locale.getDefault()), Normalizer.Form.NFD
                            )
                        ) else  // descendente
                        return@sortWith Normalizer.normalize(
                            o2.title.uppercase(Locale.getDefault()), Normalizer.Form.NFD
                        ).compareTo(
                            Normalizer.normalize(
                                o1.title.uppercase(Locale.getDefault()), Normalizer.Form.NFD
                            )
                        )

                    2 -> if (order === Order.ASCENDANT) // ascendente
                        return@sortWith Normalizer.normalize(
                            o1.author.uppercase(Locale.getDefault()), Normalizer.Form.NFD
                        ).compareTo(
                            Normalizer.normalize(
                                o2.author.uppercase(Locale.getDefault()),
                                Normalizer.Form.NFD
                            )
                        ) else  // descendente
                        return@sortWith Normalizer.normalize(
                            o2.author.uppercase(Locale.getDefault()), Normalizer.Form.NFD
                        ).compareTo(
                            Normalizer.normalize(
                                o1.author.uppercase(Locale.getDefault()),
                                Normalizer.Form.NFD
                            )
                        )

                    3 -> if (order === Order.ASCENDANT) // ascendente
                        return@sortWith o1.year.compareTo(o2.year) else  // descendente
                        return@sortWith o2.year.compareTo(o1.year)

                    else -> return@sortWith 0
                }
            } else return@sortWith 0
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = bookItems.size

    fun itemsIsEmpty() = bookItems.isEmpty()

}