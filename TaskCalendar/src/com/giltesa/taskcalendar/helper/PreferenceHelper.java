/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.util
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/helper/PreferenceHelper.java
*/

package com.giltesa.taskcalendar.helper;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

import com.giltesa.taskcalendar.R;


public final class PreferenceHelper
{

	private Activity			context;
	private static final String	PREF_SORT	= "main_menu_settings_app_sortTasksBy_key";
	private static final String	PREF_THEME	= "main_menu_settings_app_theme_key";
	private static final String	PREF_EXIT	= "main_menu_settings_app_confirmExit_key";
	private static final String	PREF_DIR	= "main_menu_settings_calendar_directoryStorage_key";
	//private static final String	PREF_ABOUT	= "main_menu_settings_about_about_key";
	//private static final String	PREF_SHARE	= "main_menu_settings_about_share_key";



	public PreferenceHelper(Activity context)
	{
		this.context = context;
	}



	public String getSortTask()
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SORT, "");
	}



	public int getTheme()
	{
		String[] listThemes = context.getResources().getStringArray(R.array.main_menu_settings_app_theme_listOptions);
		String theme = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_THEME, "");

		if( theme.equals(listThemes[0]) )
			return android.R.style.Theme_Holo;
		else
			return android.R.style.Theme_Holo_Light_DarkActionBar;
	}



	public boolean isConfirmExit()
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_EXIT, false);
	}



	public String getDirectory()
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_DIR, context.getResources().getString(R.string.main_menu_settings_calendar_directoryStorage_defaultValue));
	}



	public String getVersionName()
	{
		PackageManager pm = context.getPackageManager();
		try
		{
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		}
		catch( NameNotFoundException e )
		{
			return "";
		}
	}


}


//MODO DE ORDENAR LAS TAREAS
//"main_menu_settings_app_sortTasksBy_key"

//APARIENCIA VISUAL
//"main_menu_settings_app_theme_key"

//CONFIRMACION AL SALIR
//"main_menu_settings_app_confirmExit_key"

//GESTIONAR ETIQUETAS

//DIRECTORIO DE ADJUNTOS
//"main_menu_settings_calendar_directoryStorage_key"

//COPIA DE SEGURIDAD LOCAL

//COPIA DE SEGURIDAD EN DROPBOX

//ACERCA DE
//"main_menu_settings_about_about_key"

//COMPARTIR APLICACION
//"main_menu_settings_about_share_key"

//PUNTUAR EN EL MARKET

//INFORMAR SOBRE UN ERROR

// LICENCIA DE SOFTWARE
