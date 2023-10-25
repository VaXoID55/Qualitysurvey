package ru.vaxoid.qualitysurvey

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ru.vaxoid.qualitysurvey.databinding.SettingsActivityBinding

private const val TITLE_TAG = "settingsActivityTitle"

class SetupActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
               // setTitle(R.string.title)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
        }
    class GoodPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.good_adv_preferences, rootKey)
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
            val cnt = sharedPreferences.getInt("button_good_count", 1)
            Log.i(TAG, "Create good pref count : $cnt")
            val mCategory = findPreference("good_adv_pref") as PreferenceCategory?
            var i = 1
            while (i<=cnt) {
                with(mCategory?.let { EditTextPreference(it.context) }) {
                    this?.setKey("Title_button_good_$i")
                    this?.setDefaultValue("Я доволен")
                    this?.setTitle("Подпись кнопки $i" )
                    this?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
                    this?.let { mCategory?.addPreference(it) }
                }
                i++
            }
        }
    }
    class BadPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.bad_adv_preferences, rootKey)
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
            val cnt = sharedPreferences.getInt("button_bad_count", 1)
            Log.i(TAG, "Create bad pref count : $cnt")
            val mCategory = findPreference("bad_adv_pref") as PreferenceCategory?
            var i = 1
            while (i<=cnt) {
                with(mCategory?.let { EditTextPreference(it.context) }) {
                    this?.setKey("Title_button_bad_$i")
                    this?.setDefaultValue("Я не доволен")
                    this?.setTitle("Подпись кнопки $i" )
                    this?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
                    this?.let { mCategory?.addPreference(it) }
                }
            i++
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        TODO("Not yet implemented")
//        getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE).getInt()
//        if (key = "button_bad_count")
//        sharedPreferences?.getString("Title_survey", getString(R.string.name_survey)).toString()
    //  val mActivity = MainActivity()
      //  mActivity.caption_text.text = sharedPreferences?.getString("Title_survey", getString(R.string.name_survey)).toString()
            //.caption_text.text = sharedPreferences.getString("Title_survey", getString(R.string.name_survey)).toString()
    }

}