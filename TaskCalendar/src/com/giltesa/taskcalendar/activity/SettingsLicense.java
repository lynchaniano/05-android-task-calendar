/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/activity/SettingsLicense.java
*/


package com.giltesa.taskcalendar.activity;


import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.helper.PreferenceHelper;


public class SettingsLicense extends Activity
{
	protected PreferenceHelper	prefs;



	/**
	 * 
	 */
	@SuppressLint( "NewApi" )
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_license);

		// Permite que el icono de la barra de name se comporte como el boton atras, y su evento sea tratado desde onOptionsItemSelected()
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);


		// Carga la página web de la licencia Creative Commons - Desde internet:
		/*
		WebView webview = (WebView)findViewById(R.id.settings_license_webview);
		webview.setInitialScale(100);
		WebSettings webSettings = webview.getSettings();
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(true);
		webSettings.setDefaultTextEncodingName("utf-8");
		String locale = getResources().getConfiguration().locale.getLanguage();
		webview.loadUrl("http://creativecommons.org/licenses/by-nc-sa/3.0/deed." + locale);
		*/


		// Carga la página web de la licencia Creative Commons - Desde la propia aplicación:
		WebView webview = (WebView)findViewById(R.id.settings_license_webview);
		WebSettings webSettings = webview.getSettings();
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(true);
		webSettings.setDefaultTextEncodingName("utf-8");
		try
		{
			InputStream input = getResources().openRawResource(R.raw.license); // Se carga el fichero license.html que contiene el html y css de la página.
			byte[] buffer = new byte[input.available()];
			input.read(buffer);
			input.close();
			webview.loadDataWithBaseURL("file:///android_res/raw/", new String(buffer), "text/html", "UTF-8", null);
		}
		catch( IOException e )
		{
			Log.e("SettingsLicense", "", e);
		}
	}



	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings_license_actionbar, menu);

		return true;
	}



	/**
	 * Trata los eventos del ActionBar: Retrocede a atras o abre la licencia en el navegador.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
			case android.R.id.home:
				finish();
				break;

			case R.id.license_actionbar_web:
				String locale = getResources().getConfiguration().locale.getLanguage();
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.settings_license_actionbar_url_base) + locale)));
				break;

			default:
				break;
		}
		return true;
	}

}
