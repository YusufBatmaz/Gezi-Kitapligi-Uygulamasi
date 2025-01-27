package com.yusufbatmaz.gezikitapligi

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.yusufbatmaz.gezikitapligi.models.GeziModel

class DB(context: Context) : SQLiteOpenHelper(context, DBName, null , version) {
    companion object {
        private val DBName = "notedb.db"
        private val version = 1
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        val noteTable = """
    CREATE TABLE "note" (
        "NID" INTEGER PRIMARY KEY AUTOINCREMENT,
        "Title" TEXT,
        "Detail" TEXT,
        "Date" TEXT,
        "ImageUri" TEXT
    );
"""
        p0?.execSQL(noteTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        val noteTable = "DROP TABLE IF EXISTS note"
        p0?.execSQL(noteTable)
        onCreate(p0)
    }

    fun addNote(title: String, detail: String, date: String, imageUri: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("Title", title)
        values.put("Detail", detail)
        values.put("Date", date)
        values.put("ImageUri", imageUri)

        val affectedRow = db.insert("note", null, values)

        db.close()
        return affectedRow
    }


    fun showNotes(): List<GeziModel> {
        val arr = mutableListOf<GeziModel>()
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query("note", null, null, null, null, null, null)

            // Veritabanı boşsa, cursor null olabilir. Bunun için kontrol ekleyelim.
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val nID = cursor.getInt(0)
                    val title = cursor.getString(1)
                    val detail = cursor.getString(2)
                    val date = cursor.getString(3)
                    val imageUrl = cursor.getString(4)

                    val note = GeziModel(nID, title, detail, date, imageUrl)
                    arr.add(note)
                }
            } else {
                // Eğer cursor boşsa, boş bir liste döndürebiliriz.
                println("Veritabanında hiçbir not bulunamadı.")
            }
        } catch (e: Exception) {
            e.printStackTrace()  // Hata mesajını logcat'e yazdırır
            println("Veritabanı sorgusunda hata oluştu");
        } finally {
            // cursor ve db nesnelerini kapatmayı unutmayalım
            cursor?.close()  // Cursor kapanmalı
            db.close()  // Veritabanı bağlantısı kapanmalı
        }

        return arr
    }

    fun deleteNote(nID: Int): Int {
        val db = this.writableDatabase
        val status = db.delete("note", "NID = $nID", null)

        db.close()
        return status
    }

    fun updateNote(nID: Int, title: String, detail: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("Title", title)
        values.put("Detail", detail)

        val status = db.update("note", values, "NID = ?", arrayOf(nID.toString()))

        db.close()
        return status
    }

    fun searchNotes(query: String): List<GeziModel> {
        val arr = mutableListOf<GeziModel>()
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            // Başlık kısmında query'yi içeren notları sorgulama
            val selection = "Title LIKE ?"
            val selectionArgs = arrayOf("%$query%")

            cursor = db.query("note", null, selection, selectionArgs, null, null, null)

            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val nID = cursor.getInt(0)
                    val title = cursor.getString(1)
                    val detail = cursor.getString(2)
                    val date = cursor.getString(3)
                    val imageUrl = cursor.getString(4)

                    val note = GeziModel(nID, title, detail, date, imageUrl)
                    arr.add(note)
                }
            } else {
                println("Veritabanında hiçbir not bulunamadı.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Veritabanı sorgusunda hata oluştu")
        } finally {
            cursor?.close()
            db.close()
        }

        return arr
    }
}



