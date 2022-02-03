package ru.vaxoid.qualitysurvey

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.blue
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.vaxoid.qualitysurvey.SetupActivity.SettingsFragment
import ru.vaxoid.testsql.db.AnswerToDb
import ru.vaxoid.testsql.db.MyDbManager
import kotlin.math.absoluteValue


const val TAG = "MyActivity"

class MainActivity : AppCompatActivity() {
    private val myDBManager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN )
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON )

        setContentView(R.layout.activity_main)

        button_bad.setOnClickListener(this::onClickButtons)
        button_good.setOnClickListener(this::onClickButtons)

        button_setup.setOnClickListener(this::onClickSetup)

        myDBManager.openDB()
    }

    override fun onResume() {
        loadCaptions()
        super.onResume()
    }

    private fun onClickButtons(view: View) {
        val txt = (view as Button).text
        val id = (view as Button).id
        Log.i(TAG, "Click button: $txt, $id")
        //view.isEnabled = false
        button_bad.isEnabled = false
        button_good.isEnabled = false
        when (view.id) {

            button_bad.id -> {

                val answer = AnswerToDb()
                answer.value=answer.ANSWER_NEGATIVE
                myDBManager.insertToDb(answer)
            }
            R.id.button_good -> {
                val answer = AnswerToDb()
                answer.value=answer.ANSWER_POSITIVE
                myDBManager.insertToDb(answer)
            }
        }

        val animBut = AnimationUtils.loadAnimation(this, R.anim.rotate)
        view.bringToFront()
        animBut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                //view.isEnabled = true
                button_bad.isEnabled = true
                button_good.isEnabled = true
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        view.startAnimation(animBut)
    }

    fun onClickSetup(view: View){
        val scoreIntent = Intent(this, ScoreActivity::class.java)
        startActivity(scoreIntent)
        button_setup.visibility = View.GONE
        Log.i(TAG, "New Intent: $intent")

    }

    private fun showToast (message:String){

        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, message, duration)
        toast.show()

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) or (keyCode == KeyEvent.KEYCODE_MENU)) {
            button_setup.visibility != button_setup.visibility
            when(button_setup.visibility) {
                View.VISIBLE -> {button_setup.visibility=View.GONE}
                View.GONE -> {button_setup.visibility=View.VISIBLE}
            }
                //(TODO("Добавить скрытие кнопки через время, отдельная сопрограмма"))
        }
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finishAffinity()
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun loadCaptions() {
        //Читаем настройки заголовков
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, true) //Строка для установки дефолтных значений

        caption_text.text = sharedPreferences.getString("Title_survey", "").toString()
        button_good.text = sharedPreferences.getString("Title_button_good", "").toString()
        button_bad.text = sharedPreferences.getString("Title_button_bad", "").toString()
        button_good.textSize = sharedPreferences.getString("Font_size_buttons", "")!!.toFloat()
        button_bad.textSize = sharedPreferences.getString("Font_size_buttons", "")!!.toFloat()
        caption_text.textSize = sharedPreferences.getString("Font_size_title", "")!!.toFloat()

        if (sharedPreferences.getBoolean("Max_screen_on", true )){
            Log.i(TAG,"Включить максимальную яркость")
            window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            window.attributes.screenBrightness
        } else {
            Log.i(TAG,"Устанавливаем обычную яркость")
            window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        }
    }

}

