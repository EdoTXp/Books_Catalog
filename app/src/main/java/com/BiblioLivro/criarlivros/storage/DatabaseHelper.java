/*
 * Copyright (c) 2020. Está classe está sendo consedida para uso pessoal
 */

package com.BiblioLivro.criarlivros.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper
{
    //ATRIBUTOS
    private static final String DATABASE_NAME = "catalogo";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context)
    {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db)
    {
        String CREATE_TABLE_CATALOGO = "CREATE TABLE catalogo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT, autor TEXT," +
                "ano INTEGER);";
        db.execSQL(CREATE_TABLE_CATALOGO);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS catalogo");
    }

    public long inserir(ContentValues cv)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert("catalogo", null, cv);
    }

    public List<ContentValues> pesquisarPorTitulo(String titulo)
    {
        String sql = "SELECT * FROM catalogo WHERE titulo LIKE ?";
        String[] where = new String[] {"%"+titulo+"%"};
        return pesquisar (sql, where);
    }

    public List<ContentValues> pesquisarPorAutor(String autor)
    {
        String sql = "SELECT * FROM catalogo WHERE autor LIKE ?";
        String[] where = new String[] {"%"+autor+"%"};
        return pesquisar(sql, where);
    }

    public List<ContentValues> pesquisarPorAno(int ano)
    {
        String sql = "SELECT * FROM catalogo WHERE ano=?";
        String[] where = new String[] {String.valueOf(ano)};
        return pesquisar (sql, where);
    }

    public List<ContentValues> pesquisarPorTodos()
    {
        String sql = "SELECT * FROM catalogo ORDER BY id";
        return pesquisar(sql, null);
    }

    private List<ContentValues> pesquisar (String sql, String[] where)
    {
       List<ContentValues> lista = new ArrayList<>();

       SQLiteDatabase db = this.getReadableDatabase();
       Cursor c = db.rawQuery(sql, where);

       if(c.moveToFirst())
       {
           do {
               ContentValues cv = new ContentValues();
               cv.put("id", c.getInt(c.getColumnIndex("id")));
               cv.put("titulo", c.getString(c.getColumnIndex("titulo")));
               cv.put("autor", c.getString(c.getColumnIndex("autor")));
               cv.put("ano", c.getInt(c.getColumnIndex("ano")));
               lista.add(cv);
           }while (c.moveToNext());
       }
       c.close();
       return lista;
    }

}