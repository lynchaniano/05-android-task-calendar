/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/activity/SettingsBackups.java
*/


package com.giltesa.taskcalendar.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.helper.PreferenceHelper;


public class SettingsBackups extends Activity
{
	protected PreferenceHelper	prefs;



	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_backups);

		//Permite que el icono de la barra de name se comporte como el boton atras, y su evento sea tratado desde onOptionsItemSelected()
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}



	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
			case android.R.id.home:
				Intent intent = new Intent(this, Settings.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;

			default:
				break;
		}
		return true;
	}

}
