package ru.vaxoid.qualitysurvey.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import ru.vaxoid.qualitysurvey.ScoreActivity
import ru.vaxoid.qualitysurvey.TAG
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyDbManager( context: Context) {
    val myDbHelper = MyDBHelper(context)
    var db: SQLiteDatabase ?= null

    fun openDB (){
        db = myDbHelper.writableDatabase
    }
    fun insertToDb(answer: AnswerToDb) {
    val values = ContentValues().apply {
        put(MyDBNameClass.COLUMN_NAME_DATE, answer.day)
        put(MyDBNameClass.COLUMN_NAME_TIME, answer.time)
        put(MyDBNameClass.COLUMN_NAME_VAL, answer.valText)
        put(MyDBNameClass.COLUMN_NAME_TYPE, answer.type)
        }
        Log.i(TAG,"Записана дата: ${answer.day} и значение ${answer.valText}")
    db?.insert(MyDBNameClass.TABLE_NAME,null, values)
    }


    @SuppressLint("Range")
    fun readDbByDate(date_start: String, date_end: String):ArrayList<AnswerDaySum> {
        openDB()
        val dataList = ArrayList<AnswerDaySum>()
        val cursor = db?.query(MyDBNameClass.VIEW_NAME,null,
            "answer_date BETWEEN \"$date_start\" AND \"$date_end\"",null,null,null,null,null)

            while (cursor?.moveToNext()!!){
                val dte :Date = SimpleDateFormat("yyyy-MM-dd").parse(cursor?.getString(cursor.getColumnIndex("answer_date")))
                var answerList : ArrayList<CollTextAnswerCntInDay> = fillCollTextAnswerCntInDay(dte)
                val answerDaySum = AnswerDaySum(
                    dte,
                    cursor?.getInt(cursor.getColumnIndex("Neg")),
                    cursor?.getInt(cursor.getColumnIndex("Pos")),
                    answerList)
                Log.i(TAG,answerDaySum.toString() )
                Log.i(TAG,answerDaySum.answersInDay.toString() )
                dataList.add(answerDaySum)
            }
            cursor.close()
        closeDB()

        return dataList
    }

    @SuppressLint("Range")
    private fun fillCollTextAnswerCntInDay(_date:Date): ArrayList<CollTextAnswerCntInDay> {
    val resDataList = ArrayList<CollTextAnswerCntInDay>()
        val cursor = db?.rawQuery(
            "SELECT answer_val, count(*) as cnt, answer_type FROM answers\n" +
                    //"WHERE answer_date = \"${_date.toString()}\"\n" +
                    "WHERE answer_date = \"${SimpleDateFormat("yyyy-MM-dd").format(_date)}\"\n" +
                    "group by answer_val, answer_type\n" +
                    "order by answer_type, cnt DESC",null)

        while (cursor?.moveToNext()!!) {
            resDataList.add(CollTextAnswerCntInDay(
                cursor?.getString(cursor.getColumnIndex("answer_val")),
                cursor?.getInt(cursor.getColumnIndex("cnt")),
                cursor?.getInt(cursor.getColumnIndex("answer_type"))
                )
            )
            Log.i(TAG,"Добавлено в CollTextAnswerCntInDay: "+resDataList.toString())
        }

        cursor.close()
        return resDataList
    }

    @SuppressLint("Range")
    fun fillListAnswersValInDay(_date:String, _value:String ): Array<String?> {
        var tempDataArray = ArrayList<String>()
        openDB()
        val cursor = db?.rawQuery(
            "SELECT answer_time FROM answers WHERE answer_date = \"$_date\" AND answer_val = \"$_value\"",null)
        var i=0
        while (cursor?.moveToNext()!!) {
            i+=1
            tempDataArray.add(" $i. " + cursor?.getString(cursor.getColumnIndex("answer_time")))
            //Log.i(TAG,"Добавлено в CollTextAnswerCntInDay: "+resDataList.toString())
        }
        cursor.close()
        val resDataArray = arrayOfNulls<String>(tempDataArray.size)
        tempDataArray.toArray(resDataArray)
        //System.arraycopy(tempDataArray,0,resDataArray,0,tempDataArray.size)
        return resDataArray
    }

    fun closeDB(){
        myDbHelper.close()
    }
}

