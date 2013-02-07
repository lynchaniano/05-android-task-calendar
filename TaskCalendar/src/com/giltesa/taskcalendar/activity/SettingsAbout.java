/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/activity/SettingsAbout.java
*/


package com.giltesa.taskcalendar.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.helper.PreferenceHelper;


public class SettingsAbout extends Activity implements OnClickListener
{
	protected PreferenceHelper	prefs;



	/**
	 * 
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	@SuppressLint( "NewApi" )
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_about);

		//Permite que el icono de la barra de name se comporte como el boton atras, y su evento sea tratado desde onOptionsItemSelected()
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		TextView version = (TextView)findViewById(R.id.settings_about_version);
		version.setText(getString(R.string.about_version) + " " + prefs.getVersionName());
	}



	/**
	 * 
	 */
	public void onClick(View view)
	{
		if( view.getId() == R.id.settings_about_url )
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_blog_url))));
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
				finish();
				break;

			default:
				break;
		}
		return true;
	}

}
