package ru.vaxoid.testsql.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper (context: Context) : SQLiteOpenHelper (context, MyDBNameClass.TABLE_NAME,
    null, MyDBNameClass.DATABASE_VERSION){
    override fun onCreate(db: SQLiteDatabase?) {
          db?.execSQL(MyDBNameClass.CREATE_TABLE)
          db?.execSQL(MyDBNameClass.CREATE_VIEW_GROUP_DATE)
        }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //db?.execSQL(MyDBNameClass.SQL_DELETE_TABLE)
        //onCreate(db)
    }
}