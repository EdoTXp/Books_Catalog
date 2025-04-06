/*
 * Copyright (c) 2023. Está classe está sendo consedida para uso pessoal
 */
package com.libry_book.books_catalog.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelperImpl(context: Context?) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME,
        null,
        DATABASE_VERSION,
    ), DatabaseHelper {

    companion object {
        private const val DATABASE_NAME = "catalogo"
        private const val DATABASE_VERSION = 3
        private const val TABLE_NAME = "catalogo"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableCatalogo = "CREATE TABLE $TABLE_NAME (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titulo TEXT, autor TEXT," +
                "ano INTEGER);"
        db.execSQL(createTableCatalogo)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    override fun insert(cv: ContentValues?): Long {
        val db = this.writableDatabase
        return db.insert(DATABASE_NAME, null, cv)
    }

    override fun update(id: Int, updateValue: ContentValues?) {
        val db = this.writableDatabase
        db.update(TABLE_NAME, updateValue, "id LIKE ?", arrayOf("%$id%"))
    }

    override fun delete(id: Int) {
        val db = this.writableDatabase
        db.delete(DATABASE_NAME, "id LIKE ?", arrayOf("%$id%"))
    }

    override fun searchByTitle(title: String): List<ContentValues> {
        val sql = "SELECT * FROM $TABLE_NAME WHERE titulo LIKE ?"
        val where = arrayOf("%$title%")
        return search(sql, where)
    }

    override fun searchByAuthor(author: String): List<ContentValues> {
        val sql = "SELECT * FROM $TABLE_NAME WHERE autor LIKE ?"
        val where = arrayOf("%$author%")
        return search(sql, where)
    }

    override fun searchByYear(year: Int): List<ContentValues> {
        val sql = "SELECT * FROM $TABLE_NAME WHERE ano=?"
        val where = arrayOf(year.toString())
        return search(sql, where)
    }

    override fun searchAll(): List<ContentValues> {
        val sql = "SELECT * FROM $TABLE_NAME ORDER BY id"
        return search(sql, null)
    }

    override fun tableExist(): Boolean {
        val sql = "SELECT * FROM $TABLE_NAME"
        val verifyTable = search(sql, null)
        return verifyTable.isNotEmpty()
    }

    private fun search(sql: String, where: Array<String>?): List<ContentValues> {
        val lista: MutableList<ContentValues> = ArrayList()
        val db = this.readableDatabase
        val c = db.rawQuery(sql, where)
        if (c.moveToFirst()) {
            do {
                val cv = ContentValues()
                cv.put("id", c.getInt(c.getColumnIndexOrThrow("id")))
                cv.put("titulo", c.getString(c.getColumnIndexOrThrow("titulo")))
                cv.put("autor", c.getString(c.getColumnIndexOrThrow("autor")))
                cv.put("ano", c.getInt(c.getColumnIndexOrThrow("ano")))
                lista.add(cv)
            } while (c.moveToNext())
        }
        c.close()
        return lista
    }
}
