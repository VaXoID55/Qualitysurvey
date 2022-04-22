package ru.vaxoid.qualitysurvey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_score.*
import ru.vaxoid.qualitysurvey.databinding.ActivityScoreBinding
import ru.vaxoid.qualitysurvey.db.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScoreActivity : AppCompatActivity() {
    lateinit var binding: ActivityScoreBinding
    private val adapter = WeekAdapter()
    private val myDBManager = MyDbManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        answerCalendar.firstDayOfWeek = Calendar.MONDAY //Настраиваем календарь на понедельник

        answerCalendar.setOnDateChangeListener(this::onDataChangeListener)
        onDataChangeListener(answerCalendar,0,0,0)

        buttonShowSetup.setOnClickListener{
            val setupIntent = Intent(this, SetupActivity::class.java)
            startActivity(setupIntent)
            Log.i(TAG, "New Intent: $setupIntent")
        }

        buttonSaveToFile.setOnClickListener(this::omButtonSaveClick)

        rcView.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                Log.i(TAG,"childCount " + rcView.childCount.toString())
//                Log.i(TAG,rcView.getChildAt(0).toString())
//                Log.i(TAG,"Touch rcView.setOnTouchListener (PARENT)")
                //lv_Types.parent.requestDisallowInterceptTouchEvent(false)
                //findViewById(R.id.child_scroll).getParent().requestDisallowInterceptTouchEvent(false);
                return v?.onTouchEvent(event) ?: false
            }
        })

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
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@ScoreActivity) //Задали тип размещения шаблонных элементов
            rcView.adapter = adapter
        }
    }


    private fun showToast (message:String){

        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, message, duration)
        toast.show()

    }

    private fun onDataChangeListener(view:CalendarView, year:Int, month:Int, dayOfMont:Int) {
        val cal = Calendar.getInstance()
            val cDate = Date(view.date)//Выбранная дата
            val weekStart: String
            val weekEnd: String
            cal.time = cDate;
            view.firstDayOfWeek = Calendar.MONDAY //Настраиваем календарь на понедельник
           adapter.clearAnswerWeek()
//           adapterExpand.clearAnswerWeek()
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
               adapter.addAnswerWeek(AnswerWeek( it.day,it.val_negative,it.val_positive,false,it.answersInDay))
           }

    }


}



