package ru.vaxoid.qualitysurvey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import ru.vaxoid.qualitysurvey.databinding.ActivityMainBinding
import ru.vaxoid.qualitysurvey.db.AnswerToDb
import ru.vaxoid.qualitysurvey.db.MyDbManager


const val TAG = "MyActivity"

class MainActivity : AppCompatActivity() {
    private val myDBManager = MyDbManager(this)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN )
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_main)
        //button_setup.setOnClickListener(this::onClickSetup)
        createButtons()
        binding.captionText.setOnLongClickListener{
            onClickSetup(it)
            }

        myDBManager.openDB()
    }

    override fun onResume() {
        createButtons()
        super.onResume()
    }

    private fun onClickGBButtons(view:View, typeButton: Int) {
        val txt = (view as Button).text
        //showToast("$txt - ТЕСТ - $typeButton")

        val answer = AnswerToDb()
        answer.type = typeButton
        answer.valText = txt as String
        myDBManager.insertToDb(answer)
        animateButton(view)
    }

    private fun animateButton(view: View) {

        val animBut = AnimationUtils.loadAnimation(this, R.anim.rotate)
//        val animBut = AnimationUtils.loadAnimation(this, R.anim.downfall)
        view.bringToFront()
        animBut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                view.isEnabled = true
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        view.startAnimation(animBut)
    }

    private fun onClickSetup(view: View):Boolean{
        val scoreIntent = Intent(this, ScoreActivity::class.java)
        startActivity(scoreIntent)
        //button_setup.visibility = View.GONE
        Log.i(TAG, "New Intent: $intent")
        return true;

    }

    private fun showToast (message:String){

        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, message, duration)
        toast.show()

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) or (keyCode == KeyEvent.KEYCODE_MENU)) {
//            //button_setup.visibility != button_setup.visibility
////            when(button_setup.visibility) {
////                View.VISIBLE -> {button_setup.visibility=View.GONE}
////                View.GONE -> {button_setup.visibility=View.VISIBLE
////                    button_setup.bringToFront()}
////            }
//                //(TODO("Добавить скрытие кнопки через время, отдельная сопрограмма"))
//        }
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finishAffinity()
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun createButtons() {
        //Читаем настройки заголовков
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, true) //Строка для установки дефолтных значений
        PreferenceManager.setDefaultValues(this, R.xml.bad_adv_preferences, true)
        PreferenceManager.setDefaultValues(this, R.xml.good_adv_preferences, true)

        val cnt_button_good = sharedPreferences.getInt("button_good_count", 1)
        val cnt_button_bad = sharedPreferences.getInt("button_bad_count", 1)

        Log.i(TAG, "Set param buttons good ($cnt_button_good) and bad ($cnt_button_bad)")

            for (i in 1..4) {
                var idString = "button_good_$i"
                var buttonID = resources.getIdentifier(idString, "id", packageName)

                with (findViewById<View>(buttonID)) {
                    setOnClickListener {
                        onClickGBButtons(it, 1)
                    }
                if (i<=cnt_button_good) {
                   this.visibility = View.VISIBLE
                   (this as Button).text = sharedPreferences.getString("Title_button_good_$i", "Доволен").toString()
                   this.textSize = sharedPreferences.getString("Font_size_good_buttons", "10")!!.toFloat()
                }else {
                    this.visibility = View.GONE
                }
                }

                idString = "button_bad_$i"
                buttonID = resources.getIdentifier(idString, "id", packageName)

                with (findViewById<View>(buttonID)) {
                    setOnClickListener {
                        onClickGBButtons(it, 0)
                    }
                    if (i<=cnt_button_bad) {
                        this.visibility = View.VISIBLE
                        (this as Button).text = sharedPreferences.getString("Title_button_bad_$i", "Не доволен").toString()
                        this.textSize = sharedPreferences.getString("Font_size_bad_buttons", "10")!!.toFloat()
                    }else {
                        this.visibility = View.GONE
                    }
                }
            }

        binding.captionText.text = sharedPreferences.getString("Title_survey", "").toString()
        binding.captionText.textSize = sharedPreferences.getString("Font_size_title", "")!!.toFloat()

        if (sharedPreferences.getBoolean("Max_screen_on", true )){
            Log.i(TAG,"Включить максимальную яркость")
            window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            window.attributes.screenBrightness
        } else {
            Log.i(TAG,"Устанавливаем обычную яркость")
            window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        }

        binding.guidelineSeparateScreen.setGuidelinePercent((sharedPreferences.getInt("Split_percent",50)).toFloat()/100)
    }

}
