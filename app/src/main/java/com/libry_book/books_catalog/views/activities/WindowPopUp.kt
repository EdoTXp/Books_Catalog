/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.libry_book.books_catalog.views.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.libry_book.books_catalog.R
import androidx.core.net.toUri

class WindowPopUp {
    fun showPopUpWindow(
        view: View,
        url: String?,
        share: String?,
        activity: AppCompatActivity,
    ) {
        // recebendo o service para preencher
        val inflater = (view.context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val popupView = inflater.inflate(
            R.layout.popupwindow,
            LinearLayout(view.context),
        )
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT

        // inicializando o PopupWindow, adicionando a view, a largura, a altura e se será focalizado
        val popupWindow = PopupWindow(
            popupView,
            width,
            height,
            true,
        )

        // adicionando um efeito de escurecimento atrás do PopupWindow
        val colorBackground = ColorDrawable()
        colorBackground.color = Color.BLACK
        colorBackground.alpha = 200

        // Adicionando o efeito de escurecimento e onde será exibido o popupWindow
        popupWindow.setBackgroundDrawable(colorBackground)
        popupWindow.showAtLocation(
            view,
            Gravity.CENTER,
            0,
            0,
        )

        // criação do butão para compartilhar e adicionando o evento di click
        val btnShare = popupView.findViewById<Button>(R.id.btnShareWindowPopUp)
        btnShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, share)
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            activity.startActivity(shareIntent)
            popupWindow.dismiss()
        }

        // criação do botão para buscar e adicionando o evento di click
        val btnSearch = popupView.findViewById<Button>(R.id.btnSearchWindowPopUp)
        btnSearch.setOnClickListener {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    url?.toUri()
                )
            )
            popupWindow.dismiss()
        }

        // Adicionando o evento di click no PopupView
        popupView.setOnTouchListener { v: View, _: MotionEvent? ->
            v.performClick()
            //Close the window when clicked
            popupWindow.dismiss()
            true
        }
    }
}