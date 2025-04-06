/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */

package com.libry_book.books_catalog.viewmodel


import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.text.TextUtils
import android.widget.EditText
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.libry_book.books_catalog.R
import com.libry_book.books_catalog.services.app_services.VibratorService
import com.libry_book.books_catalog.repositories.BookRepository
import com.libry_book.books_catalog.services.app_services.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsertBookViewModel @Inject constructor(private val bookRepository: BookRepository) :
    ViewModel() {

    fun insertBook(titleBook: String, authorBook: String, yearBook: String): Boolean {
        return bookRepository.insert(titleBook, authorBook, yearBook) > 0
    }

    fun checkEditText(edt: EditText): Boolean {
        /*Este método verifica se o EditText edt, passado como parametro, está vazio ou não.
         * Caso estiver vazio, o background do edt será alterado para o error_border e
         *  será mostrado uma animação e um text dizendo que o campo está vazio
         * No outro caso será resetado o edt com os valores padrão*/
        val actualPadding =
            edt.paddingTop // serve para pegar o atual padding e coloca-lo nas próximas alterações
        return if (TextUtils.isEmpty(edt.text)) {
            //criação da animação
            val errorAnimation = ObjectAnimator.ofFloat(edt, "translationX", 20f)
            errorAnimation.duration = 150 // a duração total da animação será de 150 millisegundos
            errorAnimation.repeatMode = ValueAnimator.REVERSE // volta no local padrão
            errorAnimation.repeatCount = 3 // repetir a animação 3 vezes
            edt.hint = edt.context.getString(R.string.hint_error)
            edt.background = ContextCompat.getDrawable(edt.context, R.drawable.layout_border_error)
            edt.setPadding(
                actualPadding,
                actualPadding,
                actualPadding,
                actualPadding
            ) //ajustando o padding
            errorAnimation.start()
            VibratorService.vibrate(
                edt.context,
                200L,
            )
            false
        } else {
            edt.background = ContextCompat.getDrawable(edt.context, R.drawable.layout_border)
            edt.setPadding(
                actualPadding,
                actualPadding,
                actualPadding,
                actualPadding
            ) //ajustando o padding

            when (edt.id) {
                R.id.edtTitulo -> edt.hint =
                    edt.context.getString(R.string.hint_titulo)

                R.id.edtAutor -> edt.hint =
                    edt.context.getString(R.string.hint_autor)

                else -> edt.hint = edt.context.getString(R.string.hint_ano)
            }
            true
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun pushNotification(context: Context, textTitle: String, message: String) {
        NotificationService.showNotification(
            context,
            textTitle,
            message,
        )

    }

}