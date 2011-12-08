package com.rider;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class MyPreferences extends PreferenceActivity{

	SharedPreferences customSharedPreference;
	SharedPreferences.Editor editor;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		customSharedPreference = getSharedPreferences("myCustomSharedPrefs", Activity.MODE_PRIVATE);
		Preference contact = (Preference) findPreference("contact");
		Preference linesUpdate = (Preference) findPreference("updateLines");
		contact.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				editor = customSharedPreference.edit();
				editor.putBoolean("contact",true);
				editor.commit();
				finish();
				return true;
			}
		});
		
		linesUpdate.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				editor = customSharedPreference.edit();
				editor.putBoolean("linesUpdate",true);
				editor.commit();
				finish();
				return true;
			}
		});
	}
}

