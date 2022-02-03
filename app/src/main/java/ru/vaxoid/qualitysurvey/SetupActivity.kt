package ru.vaxoid.qualitysurvey

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*

class SetupActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            }
        }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        TODO("Not yet implemented")
      //  val mActivity = MainActivity()
      //  mActivity.caption_text.text = sharedPreferences?.getString("Title_survey", getString(R.string.name_survey)).toString()
            //.caption_text.text = sharedPreferences.getString("Title_survey", getString(R.string.name_survey)).toString()
    }
}