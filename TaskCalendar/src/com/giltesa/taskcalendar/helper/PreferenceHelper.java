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


	/**
	 * @param context
	 */
	public PreferenceHelper(Activity context)
	{
		this.context = context;
	}



	/**
	 * @return
	 */
	public String getSortTask()
	{
		String order = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SORT, "");
		String result = "";

		String[] listOrders = context.getResources().getStringArray(R.array.main_menu_settings_app_sortTasksBy_listOptions);


		if( order.equals(listOrders[0]) ) // Oldest first
			result += "ORDER BY creation_date ASC";
		else if( order.equals(listOrders[1]) ) // Newest first
			result += "ORDER BY creation_date DESC";

		return result;
	}



	/**
	 * @return
	 */
	public int getTheme()
	{
		String[] listThemes = context.getResources().getStringArray(R.array.main_menu_settings_app_theme_listOptions);
		String theme = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_THEME, "");

		if( theme.equals(listThemes[0]) )
			return android.R.style.Theme_Holo;
		else
			return android.R.style.Theme_Holo_Light_DarkActionBar;
	}



	/**
	 * @return
	 */
	public boolean isConfirmExit()
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_EXIT, false);
	}



	/**
	 * @return
	 */
	public String getDirectory()
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_DIR, context.getResources().getString(R.string.main_menu_settings_calendar_directoryStorage_defaultValue));
	}



	/**
	 * @return
	 */
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
