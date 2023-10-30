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
import ru.vaxoid.qualitysurvey.databinding.ActivityScoreBinding
import ru.vaxoid.qualitysurvey.db.AnswerDaySum
import ru.vaxoid.qualitysurvey.db.MyDbManager
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ScoreActivity : AppCompatActivity() {
    lateinit var binding: ActivityScoreBinding
    private val adapter = WeekAdapter()
    private val myDBManager = MyDbManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.answerCalendar.firstDayOfWeek = Calendar.MONDAY //Настраиваем календарь на понедельник

        binding.answerCalendar.setOnDateChangeListener(this::onDataChangeListener)
        onDataChangeListener(binding.answerCalendar,0,0,0)

        binding.buttonShowSetup.setOnClickListener{
            val setupIntent = Intent(this, SetupActivity::class.java)
            startActivity(setupIntent)
            Log.i(TAG, "New Intent: $setupIntent")
        }

        binding.buttonSaveToFile.setOnClickListener(this::onButtonSaveClick)

        binding.rcView.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                Log.i(TAG,"childCount " + rcView.childCount.toString())
//                Log.i(TAG,rcView.getChildAt(0).toString())
//                Log.i(TAG,"Touch rcView.setOnTouchListener (PARENT)")
                //lv_Types.parent.requestDisallowInterceptTouchEvent(false)
                //findViewById(R.id.child_scroll).getParent().requestDisallowInterceptTouchEvent(false);
                return v?.onTouchEvent(event) ?: false
            }
        })

        /**
         * Тест по отправке почты
         */
        binding.buttonSendStats.setOnClickListener {

            val mailIntent = Intent(this, MailActivity::class.java).apply {
                startActivity(this)
            }
            Log.i(TAG, "New Intent: $mailIntent")
        }

//            val auth = EmailServiceOld.UserPassAuthenticator("lvs83@mail.ru", "argCxUcD3e1UmEUfh8e0")
//            val to = listOf(InternetAddress("lvs83@mail.ru"))
//            val from = InternetAddress("lvs83@mail.ru")
//            val email = EmailServiceOld.Email(auth, to, from, "Test Subject", "Hello Body World")
//            val emailServiceOld = EmailServiceOld("smtp.mail.ru", 465)
//
//           // GlobalScope.launch {
//                emailServiceOld.send(email)
//           // }

            //Рабочий блок отправки TODO Перенести
//
//
//            var mEmailService : EmailService
//
//
//            mEmailService = EmailService("smtp.mail.ru", 587) //465
//                .setEnableSelfSigned(true)
//                .setStartTls(true)
//                .setAuth(
//                    EmailService.Authenticator(
//                        "lvs83@mail.ru",
//                        "argCxUcD3e1UmEUfh8e0"
//                    )
//                )
//showToast("Попытка отправить")
//            try {
//
//                mEmailService.reset()
//                    .setFrom("lvs83@mail.ru")
//                    .setToList("lvs83@mail.ru")
//                    .setBccList("")
//                    .setSubject("mySubject")
//                    .setHtmlBody("notice.buildHTML()")
//                    .setTxtBody("notice.buildTXT()")
//                    //.setAttachments(attachment)
//                mEmailService.send(true) { e: java.lang.Exception? ->
//                    if (e != null) {
//
//                        return@send
//                    }
//                    Log.i(TAG, "Отправка почты")
//                }
//            } catch (e: MessagingException) {
//                Log.i(TAG, e.toString())
//            } catch (e: IOException) {
//                Log.i(TAG, e.toString())
//            }
//
//
//
//        }
    }

     fun onButtonSaveClick(view: View) {
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
        cal.time = cDate
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
