/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.BiblioLivro.criarlivros.R;

import org.jetbrains.annotations.NotNull;

public class WindowPopUp {

    public void showPopUpWindow(@NotNull final View view, final String URL, final String share, final AppCompatActivity activity) {

        // recebendo o service para preencher
        LayoutInflater inflater = (LayoutInflater) view.getContext()
                .getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);

        // preenchendo a view onde foi criado o layout .xml
        assert inflater != null;
        View popupView = inflater.inflate(R.layout.popupwindow, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        // inicializando o PopuoWindow, adicionando a view, a largura, a altura e se será focalizado
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // adicionando um efeito de escurecimento atrás do PopupWindow
        ColorDrawable colorBackground = new ColorDrawable();
        colorBackground.setColor(Color.BLACK);
        colorBackground.setAlpha(200);

        // Adicionando o efeito de escurecimento e onde será exibido o popupWindow
        popupWindow.setBackgroundDrawable(colorBackground);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // criação do butão para compartilhar e adicionando o evento di click
        Button btnShare = popupView.findViewById(R.id.btnShareWindowPopUp);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                activity.startActivity(shareIntent);
                popupWindow.dismiss();
            }
        });

        // criação do butão para buscar e adicionando o evento di click
        Button btnSearch = popupView.findViewById(R.id.btnSearchWindowPopUp);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(URL)));
                popupWindow.dismiss();
            }
        });

        // Adicionando o evento di click no PopupView
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
