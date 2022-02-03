package ru.vaxoid.qualitysurvey

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_score.*
import kotlinx.android.synthetic.main.activity_score.view.*
import ru.vaxoid.qualitysurvey.databinding.ActivityScoreBinding
import java.util.*
import ru.vaxoid.testsql.db.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.time.days

class ScoreActivity : AppCompatActivity() {
    lateinit var binding: ActivityScoreBinding
    private val adapter = WeekAdapter()
    private val myDBManager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_score)
        binding = ActivityScoreBinding.inflate(layoutInflater)
       // binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        answerCalendar.firstDayOfWeek = Calendar.MONDAY //Настраиваем календарь на понедельник
        //initRcView()//Инициализировали адаптер

        answerCalendar.setOnDateChangeListener(this::onDataChangeListener)
        onDataChangeListener(answerCalendar,0,0,0)

        buttonShowSetup.setOnClickListener{
            val setupIntent = Intent(this, SetupActivity::class.java)
            startActivity(setupIntent)
            Log.i(TAG, "New Intent: $setupIntent")
        }
        buttonSaveToFile.setOnClickListener(this::omButtonSaveClick)

    }

    private fun omButtonSaveClick(view: View) {
        TODO("Not yet implemented")
        val file:String = "Survey_data.csv"
        var data:String = ""




        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        }catch (e: NumberFormatException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun initRcView(){
        //var cDate = Date(answerCalendar.date)
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@ScoreActivity) //Задали тип размещения шаблонных элементов
            rcView.adapter = adapter
            //adapter.addAnswerWeek(AnswerWeek(answerCalendar.date,3,6))
        }
    }


    private fun showToast (message:String){

        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, message, duration)
        toast.show()

    }

    private fun onDataChangeListener(view:CalendarView, year:Int, month:Int, dayOfMont:Int) {
        val cal = Calendar.getInstance()
            var cDate = Date(view.date)//Выбранная дата
            var weekStart: String
            var weekEnd: String
            cal.time = cDate;
            view.firstDayOfWeek = Calendar.MONDAY //Настраиваем календарь на понедельник
           adapter.clearAnswerWeek()
           initRcView()
           when (cDate.day) {
               0 -> {
                   weekEnd = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
                   cal.add(Calendar.DATE, -6)
                   weekStart = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
               }
               else -> { // обратите внимание на блок
                   cal.add(Calendar.DATE, -cDate.day+1)
                   weekStart = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
                   cal.add(Calendar.DATE, 6)
                   weekEnd = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
               }
           }
           val dataList : ArrayList<AnswerDaySum> = myDBManager.readDbByDate(weekStart,weekEnd )
           dataList.forEach{
               adapter.addAnswerWeek(AnswerWeek( it.day,it.val_negative,it.val_positive))
           }

//            adapter.addAnswerWeek(AnswerWeek(  SimpleDateFormat("yyyy-MM-dd").parse("2021-11-29") ,0,130))
//        adapter.addAnswerWeek(AnswerWeek(  SimpleDateFormat("yyyy-MM-dd").parse("2021-12-01") ,0,139))
//        adapter.addAnswerWeek(AnswerWeek(  SimpleDateFormat("yyyy-MM-dd").parse("2021-12-02") ,2,121))
//        adapter.addAnswerWeek(AnswerWeek(  SimpleDateFormat("yyyy-MM-dd").parse("2021-12-03") ,1,140))
    }

}



