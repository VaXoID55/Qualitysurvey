package ru.vaxoid.qualitysurvey.db

import android.provider.BaseColumns
import java.text.SimpleDateFormat
import java.util.*

object MyDBNameClass {
    const val TABLE_NAME = "answers"
    const val VIEW_NAME = "view_answers_date"
    const val COLUMN_NAME_DATE = "answer_date"
    const val COLUMN_NAME_TIME = "answer_time"
    const val COLUMN_NAME_VAL = "answer_val"
    const val COLUMN_NAME_TYPE = "answer_type"


    const val DATABASE_VERSION = 3
    const val DATABASE_NAME = "MyAnswers.db"

    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_DATE TEXT," +
            "$COLUMN_NAME_TIME TEXT," +
            "$COLUMN_NAME_VAL TEXT," +
            "$COLUMN_NAME_TYPE INTEGER)"
    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

    const val CREATE_VIEW_GROUP_DATE =
        "CREATE VIEW $VIEW_NAME " +
                "AS " +
                "SELECT " +
                "    $COLUMN_NAME_DATE," +
                "    SUM($COLUMN_NAME_TYPE = 0) as Neg," +
                "    SUM($COLUMN_NAME_TYPE = 1) as Pos" +
                " FROM $TABLE_NAME" +
               // "WHERE answer_date BETWEEN '2021-10-29' AND '2021-10-30'\n" +
                " GROUP by $COLUMN_NAME_DATE"
    const val SQL_DROP_VIEW = "DROP VIEW \"view_answers_date\";"

//    const val ALARM_TABLE_NAME = "alarm_events"
//    const val ALARM_COLUMN_PARAMS = "params"
//    const val ALARM_COLUMN_ENABLE = "enable_date"
//    const val ALARM_COLUMN_SEND_TIME = "send_time"

//    const val UPDATE_DB_TO_v3 = "" +
//            "CREATE TABLE IF NOT EXISTS $ALARM_TABLE_NAME " +
//            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
//            "$ALARM_COLUMN_PARAMS TEXT," + //d,w,m (DAY,WEEK, MOUNTH) ; c,p (CURRENT, PREVIOS); time; days;
//            "$ALARM_COLUMN_ENABLE TEXT," + //дата активации. Если пусто, то disable
//            "$ALARM_COLUMN_SEND_TIME TEXT"  //время последней отправки

}

data class CollTextAnswerCntInDay(
    var answerTxt:String,
    var answerCnt:Int,
    var answerType:Int,
)

data class AnswerDaySum(
    var day: Date,
    var val_negative: Int = 0,
    var val_positive: Int = 0,
    var answersInDay: ArrayList<CollTextAnswerCntInDay>,
)


class AnswerToDb() {
    private val cDate = Date()

    var day:String = SimpleDateFormat("yyyy-MM-dd").format(cDate)
    var time:String= SimpleDateFormat("HH:mm:ss").format(cDate)
    var type:Int = 0
    var valText:String = ""


    val ANSWER_POSITIVE = 1
    val ANSWER_NEGATIVE = 0
}