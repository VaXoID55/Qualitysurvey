package ru.vaxoid.qualitysurvey

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list_answer.*
import ru.vaxoid.qualitysurvey.db.MyDbManager

class ListAnswerActivity : AppCompatActivity() {
    private val myDBManager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_answer)

        val arguments = intent.extras
        val answerDay = arguments!!["AnswerDay"].toString()
        val answerVal = arguments!!["AnswerVal"].toString()
        val answerType = arguments!!["AnswerType"].toString().toInt()

        tvCaption.text = "Дата: $answerDay  -  \"$answerVal\""
        if (answerType==1) {tvCaption.setBackgroundResource(R.color.bgItem_Good)}
        else {tvCaption.setBackgroundResource(R.color.bgItem_Bad)}

        //Заполняем массив адаптера
        val dataList : Array<String?> = myDBManager.fillListAnswersValInDay(answerDay,answerVal )
        val arrayAdapter: ArrayAdapter<*>
        val mListView = findViewById<ListView>(R.id.lvAnswers)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
        mListView.adapter = arrayAdapter
    }
}