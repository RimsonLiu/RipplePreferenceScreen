package com.rimson.ripplepreferencescreen

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.view.View
import android.view.ViewGroup
import com.rimson.ripplepreferencescreen.anim.RippleAnimation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		button_goto_settings.setOnClickListener { v: View ->
			val onAnimationEndListener = object : RippleAnimation.OnAnimationEndListener {
				override fun onAnimationEnd() {
					val intent = Intent(this@MainActivity, SettingsActivity::class.java)
					startActivity(intent)
				}
			}
			RippleAnimation.create(v).setDuration(1000).setOnAnimationEndListener(onAnimationEndListener).start()
			val view = this.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
			view.setBackgroundColor(Color.WHITE)
		}

		PreferenceManager.setDefaultValues(this, R.xml.preference, false)
	}
}
