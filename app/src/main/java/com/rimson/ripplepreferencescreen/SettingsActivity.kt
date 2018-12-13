package com.rimson.ripplepreferencescreen

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {

	companion object {
		const val KEY_SAVE_NET_SWITCH = "save_net_mode"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportFragmentManager.beginTransaction()
			.replace(android.R.id.content, SettingsFragment())
			.commit()
		val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
		val switchPref = sharedPreferences.getBoolean(SettingsActivity.KEY_SAVE_NET_SWITCH, false)
		Toast.makeText(this, switchPref.toString(), Toast.LENGTH_SHORT).show()
	}
}
