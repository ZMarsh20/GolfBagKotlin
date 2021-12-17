package com.example.mygolfbagkotlin

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyBagDatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    val count: Int
        @SuppressLint("Recycle")
        get() {
            val sql = readableDatabase
            val c = sql.rawQuery("SELECT COUNT(*) FROM $TABLE WHERE OWNER=$owner", null)
            c.moveToFirst()
            return c.getInt(0)
        }
    val countUsers: Int
        @SuppressLint("Recycle")
        get() {
            val sql = readableDatabase
            val c = sql.rawQuery("SELECT COUNT(*) FROM $USERTABLE", null)
            c.moveToFirst()
            return c.getInt(0)
        }

    @SuppressLint("SQLiteString")
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val query = ("CREATE TABLE clubs( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "type INTEGER, "
                + "loft FLOAT, "
                + "brand STRING, "
                + "shaft STRING, "
                + "flex INTEGER, "
                + "yards INTEGER, "
                + "description STRING, "
                + "image STRING, "
                + "owner INTEGER);")
        sqLiteDatabase.execSQL(query)
        val query2 = ("CREATE TABLE users( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username STRING, "
                + "name STRING, "
                + "password STRING);")
        sqLiteDatabase.execSQL(query2)
    }

    var owner: Int = -1


    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {}

    companion object {
        const val DB_NAME = "clubs.sqlite"
        const val DB_VERSION = 1
        const val TABLE_ID = "_id"
        const val TABLE = "clubs"
        const val TYPE = "type"
        const val LOFT = "loft"
        const val BRAND = "brand"
        const val SHAFT = "shaft"
        const val FLEX = "flex"
        const val YARDS = "yards"
        const val DESC = "description"
        const val IMAGE = "image"
        const val OWNER = "owner"
        const val USERTABLE = "users"
        const val USER = "username"
        const val PASS = "password"
        const val NAME = "name"
    }
}