/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.util
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/util/Backup.java
*/


package com.giltesa.taskcalendar.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;


public class Backup
{

	private File	file;
	private String	date;
	private String	length;



	/**
	 * @param file
	 */
	@SuppressLint( "SimpleDateFormat" )
	public Backup(File file)
	{
		this.file = file;
		this.date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(file.lastModified());
		this.length = file.length() / Byte.SIZE + " KB";
	}



	/**
	 * @param file
	 * @param modified
	 * @param size
	 */
	@SuppressLint( "SimpleDateFormat" )
	public Backup(File file, String modified, String size)
	{
		String[] words = modified.split(" ");

		try
		{
			Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(words[2]);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int month = cal.get(Calendar.MONTH) + 1;
			words[2] = ( month < 10 ) ? "0" + month : "" + month;
		}
		catch( ParseException e )
		{
			e.printStackTrace();
		}

		this.file = file;
		this.date = words[3] + "/" + words[2] + "/" + words[1] + " " + words[4];
		this.length = size;
	}



	public File getFile()
	{
		return file;
	}



	public String getDate()
	{
		return date;
	}



	public String getLength()
	{
		return length;
	}

}
