package com.example.crucified.myfirstkotlin.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class Database: SQLiteOpenHelper {

    companion object {
        const val VERSION = 1
        const val TABLE_NAME = "Art"
        const val NAME_FIELD = "name"
        const val PICTURE_FIELD = "picture"
    }

    constructor(context: Context): super( context, "art.db", null, VERSION)

    fun artNames(): ArrayList<String> {
        val cursor = readableDatabase.query(TABLE_NAME,
                arrayOf(NAME_FIELD),
                null,
                null,
                null,
                null,
                null)
        var names = ArrayList<String>()
        val index = cursor.getColumnIndex(NAME_FIELD)
        cursor.moveToFirst()
        var isOk = cursor.count > 0
        while (isOk) {
            val value = cursor.getString(index)
            names.add(value)
            isOk = cursor.moveToNext()
        }
        return names

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
        val cursor = readableDatabase.query(TABLE_NAME,
                arrayOf(NAME_FIELD, PICTURE_FIELD),
                "$NAME_FIELD = ?",
                arrayOf(name),
                null,
                null,
                null )
        cursor.moveToFirst()
        val nameIndex = cursor.getColumnIndex("name")
        val pictureIndex = cursor.getColumnIndex("picture")
        val artName = cursor.getString(nameIndex)
        val artPicture = cursor.getBlob(pictureIndex)

        if (artName != null && artPicture != null) {
            val bitmap = BitmapFactory.decodeByteArray(artPicture, 0, artPicture.size)
            return Art(artName, bitmap)
        } else {
            return null
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query =  """
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