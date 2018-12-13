package com.rimson.ripplepreferencescreen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
	    button_goto_settings.setOnClickListener {
		    val intent = Intent(this, SettingsActivity::class.java)
		    startActivity(intent)
	    }
	    PreferenceManager.setDefaultValues(this, R.xml.preference, false)
    }
}
