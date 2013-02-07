/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:      /TaskCalendar/src/com/giltesa/taskcalendar/activity/Settings.java
*/
/*
	NOTAS:
	PreferenceFragment
		http://developer.android.com/reference/android/preference/PreferenceFragment.html
		http://www.java2s.com/Code/Android/Core-Class/DemonstrationofPreferenceFragmentshowingasinglefragmentinanactivity.htm
	
*/


package com.giltesa.taskcalendar.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.helper.PreferenceHelper;


public class Settings extends PreferenceActivity
{
	protected PreferenceHelper	prefs;



	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	@SuppressLint( "NewApi" )
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		super.onCreate(savedInstanceState);
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

		//Permite que el icono de la barra de name se comporte como el boton atras, y su evento sea tratado desde onOptionsItemSelected()
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}



	/**
	 * Se capturan todas las pulsaciones de las teclas físicas del dispositivo para poder personalizar los eventos que lanzan.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		//Si la tecla pulsada es la de Atrás, se vuelve al activity anterior pero haciendo que se cree de nuevo, asi el cambio de theme surtirá efecto.
		if( keyCode == KeyEvent.KEYCODE_BACK )
		{
			Intent intent = new Intent(this, Main.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		// Si aquí no se ha tratado el evento se le envía al padre para que lo trate él.
		return super.onKeyDown(keyCode, event);
	}



	/**
	 * 
	 */
	@SuppressLint( "NewApi" )
	public static class PrefsFragment extends PreferenceFragment
	{

		@TargetApi( Build.VERSION_CODES.HONEYCOMB )
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);


			// Se le añade un evento el boton de compartir:
			Preference share = (Preference)findPreference("main_menu_settings_about_share_key");
			share.setOnPreferenceClickListener(new OnPreferenceClickListener()
			{
				@TargetApi( Build.VERSION_CODES.HONEYCOMB )
				@SuppressLint( "NewApi" )
				public boolean onPreferenceClick(Preference preference)
				{
					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.main_menu_settings_about_share_text));
					sendIntent.setType("text/plain");
					startActivity(sendIntent);
					return true;
				}
			});

		}
	}



	/**
	 * 
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
	}



	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}



	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
			case android.R.id.home:
				Intent intent = new Intent(this, Main.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;

			default:
				break;
		}
		return true;
	}


}
