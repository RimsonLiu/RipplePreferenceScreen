package com.rimson.ripplepreferencescreen

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportFragmentManager.beginTransaction()
			.replace(android.R.id.content, SettingsFragment())
			.commit()
	}
}
