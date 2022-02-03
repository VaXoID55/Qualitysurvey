package ru.vaxoid.testsql.db

import android.provider.BaseColumns
import java.text.SimpleDateFormat
import java.util.*

object MyDBNameClass {
    const val TABLE_NAME = "answers"
    const val VIEW_NAME = "view_answers_date"
    const val COLUMN_NAME_DATE = "answer_date"
    const val COLUMN_NAME_TIME = "answer_time"
    const val COLUMN_NAME_VAL = "answer_val"

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "MyAnswers.db"

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_DATE TEXT," +
            "$COLUMN_NAME_TIME TEXT," +
            "$COLUMN_NAME_VAL INTEGER)"
    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

    const val CREATE_VIEW_GROUP_DATE =
        "CREATE VIEW $VIEW_NAME " +
                "AS " +
                "SELECT " +
                "    $COLUMN_NAME_DATE," +
                "    SUM(answer_val = 0) as Neg," +
                "    SUM(answer_val = 1) as Pos" +
                " FROM $TABLE_NAME" +
               // "WHERE answer_date BETWEEN '2021-10-29' AND '2021-10-30'\n" +
                " GROUP by $COLUMN_NAME_DATE"


}
class AnswerDaySum(
    var day: Date = Date(),
    var val_negative:Int = 0,
    var val_positive:Int = 0
) {}


//class AnswerToDb (
//    var day:String,
//    var time:String,
//    val value:Int
//) {
//    val ANSWER_POSITIVE = 1
//    val ANSWER_NEGATIVE = 0
//}

class AnswerToDb() {
    private val cDate = Date()

    var day:String = SimpleDateFormat("yyyy-MM-dd").format(cDate)
    var time:String= SimpleDateFormat("HH:mm:ss").format(cDate)
    var value:Int = 0


    val ANSWER_POSITIVE = 1
    val ANSWER_NEGATIVE = 0
}
