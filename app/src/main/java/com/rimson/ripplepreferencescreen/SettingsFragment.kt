package com.rimson.ripplepreferencescreen

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

	override fun onCreatePreferences(p0: Bundle?, p1: String?) {
		setPreferencesFromResource(R.xml.preference, p1)
	}
}