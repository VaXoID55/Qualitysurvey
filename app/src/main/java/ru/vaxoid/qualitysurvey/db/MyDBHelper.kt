package ru.vaxoid.qualitysurvey.db

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
        if ((oldVersion==1)and(newVersion==2)){
            db?.beginTransaction();
            try {
            //Сюда код изменения DB
                db?.execSQL(MyDBNameClass.SQL_DROP_VIEW); //Удаляем View
                db?.execSQL("CREATE TABLE \"sqlb_temp_table_3\" ( "+
                        "\"_id\"	INTEGER, "+
                        "\"answer_date\"	TEXT, "+
                        "\"answer_time\"	TEXT, "+
                        "\"answer_val\"	TEXT, "+
                        "\"answer_type\"	INTEGER, "+
                        "PRIMARY KEY(\"_id\"));")
                db?.execSQL("INSERT INTO \"main\".\"sqlb_temp_table_3\" (\"_id\",\"answer_date\",\"answer_time\",\"answer_type\") SELECT \"_id\",\"answer_date\",\"answer_time\",\"answer_val\" FROM \"main\".\"answers\";")
                db?.execSQL("DROP TABLE \"main\".\"answers\"")
                db?.execSQL("ALTER TABLE \"main\".\"sqlb_temp_table_3\" RENAME TO \"answers\"")
                db?.execSQL("UPDATE answers \n" +                        //Обновляем значения
                        "SET answer_val = \"Я доволен\"\n" +
                        "WHERE answer_type = \"1\";")
                db?.execSQL("UPDATE answers \n" +
                        "SET answer_val = \"Я не доволен\"\n" +
                        "WHERE answer_type = \"0\";")

                db?.execSQL(MyDBNameClass.CREATE_VIEW_GROUP_DATE)
                db?.setTransactionSuccessful();
        } finally {
            db?.endTransaction();
        }
        }
    }
}