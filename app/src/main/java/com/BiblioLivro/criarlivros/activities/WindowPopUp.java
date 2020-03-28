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

    public void showPopUpWindow(@NotNull final View v, final String URL, final String share, final AppCompatActivity activity)
    {


        LayoutInflater inflater = (LayoutInflater) v.getContext()
                .getSystemService(v.getContext().LAYOUT_INFLATER_SERVICE);


        assert inflater != null;
        View popupView = inflater.inflate(R.layout.popupwindow, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;


        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        ColorDrawable colorBackground = new ColorDrawable();
        colorBackground.setColor(Color.BLACK);
        colorBackground.setAlpha(200);

        popupWindow.setBackgroundDrawable(colorBackground);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);


        Button btnShare = popupView.findViewById(R.id.btnShareWindowPopUp);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                activity.startActivity(shareIntent);
                popupWindow.dismiss();
            }
        });

        Button btnSearch = popupView.findViewById(R.id.btnSearchWindowPopUp);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(URL)));
                popupWindow.dismiss();
            }
        });

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
