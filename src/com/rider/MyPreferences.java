package com.rider;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class MyPreferences extends PreferenceActivity{
	
	public final static String CUSTOM_PREFS = "myPrefs";
	public final static String PREFS_LINES_UPDATE = "linesUpdate";
	public final static String PREFS_CONTACT = "contact";
	public final static String PREFS_SHOW_ALL_STATAIONS = "showAllStations";
	
	SharedPreferences customSharedPreference;
	SharedPreferences.Editor editor;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		customSharedPreference = getSharedPreferences(CUSTOM_PREFS, Activity.MODE_PRIVATE);
		Preference contact = (Preference) findPreference(PREFS_CONTACT);
		Preference linesUpdate = (Preference) findPreference(PREFS_LINES_UPDATE);
		contact.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				editor = customSharedPreference.edit();
				editor.putBoolean(PREFS_CONTACT,true);
				editor.commit();
				finish();
				return true;
			}
		});
		
		linesUpdate.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				editor = customSharedPreference.edit();
				editor.putBoolean(PREFS_LINES_UPDATE,true);
				editor.commit();
				finish();
				return true;
			}
		});
	}
}

