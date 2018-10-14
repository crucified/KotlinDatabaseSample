package com.example.crucified.myfirstkotlin.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

private const val VERSION = 1
private const val TABLE_NAME = "Art"
private const val NAME_FIELD = "name"
private const val PICTURE_FIELD = "picture"

class Database(context: Context) : SQLiteOpenHelper(context, "art.db", null, VERSION) {

    fun artNames(): ArrayList<String> {
        query(arrayOf(NAME_FIELD)).use {
            val names = ArrayList<String>()
            if (it != null) {
                val index = it.getColumnIndex(NAME_FIELD)
                if (it.moveToFirst()) {
                    do {
                        names.add(it.getString(index))
                    } while (it.moveToNext())
                }
            }
            return names
        }
    }

    fun addNewArt(name: String, image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val imageBytes = stream.toByteArray()

        val values = ContentValues()
        values.put(NAME_FIELD, name)
        values.put(PICTURE_FIELD, imageBytes)

        writableDatabase.insertOrThrow(TABLE_NAME, null, values)
    }

    fun artDetails(name: String): Art? {
        query(arrayOf(NAME_FIELD, PICTURE_FIELD), "$NAME_FIELD = ?", arrayOf(name))
                .use {
                    return if (it != null && it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(NAME_FIELD)
                        val pictureIndex = it.getColumnIndex(PICTURE_FIELD)
                        val artName = it.getString(nameIndex)
                        val artPicture = it.getBlob(pictureIndex)

                        if (artName != null && artPicture != null) {
                            val bitmap = BitmapFactory.decodeByteArray(artPicture, 0, artPicture.size)
                            Art(artName, bitmap)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
    }

    private fun query(columns: Array<String>,
                      selection: String? = null,
                      selectionArgs: Array<String>? = null): Cursor? =
            readableDatabase.query(TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null)

    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            create table if not exists
            $TABLE_NAME (
                $NAME_FIELD VARCHAR,
                $PICTURE_FIELD BLOB,
                PRIMARY KEY ($NAME_FIELD)
            )
        """.trimIndent()
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented")
    }
}