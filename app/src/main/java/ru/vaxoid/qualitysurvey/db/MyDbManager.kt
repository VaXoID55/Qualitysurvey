package ru.vaxoid.testsql.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import ru.vaxoid.qualitysurvey.TAG
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyDbManager( context: Context) {
    val myDbHelper = MyDBHelper(context)
    var db: SQLiteDatabase ?= null

    fun openDB (){
        db = myDbHelper.writableDatabase
    }
    fun insertToDb(answer:AnswerToDb) {
    val values = ContentValues().apply {
        put(MyDBNameClass.COLUMN_NAME_DATE, answer.day)
        put(MyDBNameClass.COLUMN_NAME_TIME, answer.time)
        put(MyDBNameClass.COLUMN_NAME_VAL, answer.value)
        }
        Log.i(TAG,"Записана дата: ${answer.day}")
    db?.insert(MyDBNameClass.TABLE_NAME,null, values)
    }

    fun readDbByDate(date_start: String, date_end: String):ArrayList<AnswerDaySum> {
        openDB()
        val dataList = ArrayList<AnswerDaySum>()
        val cursor = db?.query(MyDBNameClass.VIEW_NAME,null,
            "answer_date BETWEEN \"$date_start\" AND \"$date_end\"",null,null,null,null,null)

            while (cursor?.moveToNext()!!){
            //al dataText = cursor?.getString(cursor.getColumnIndex(MyDBNameClass.COLUMN_NAME_DATE))
                var answerDaySum = AnswerDaySum()
                answerDaySum.day = SimpleDateFormat("yyyy-MM-dd").parse(cursor?.getString(cursor.getColumnIndex("answer_date")))
                answerDaySum.val_negative = cursor?.getInt(cursor.getColumnIndex("Neg"))
                answerDaySum.val_positive = cursor?.getInt(cursor.getColumnIndex("Pos"))
                dataList.add(answerDaySum)
            }
            cursor.close()
        closeDB()

        return dataList
    }

    fun closeDB(){
        myDbHelper.close()
    }
}