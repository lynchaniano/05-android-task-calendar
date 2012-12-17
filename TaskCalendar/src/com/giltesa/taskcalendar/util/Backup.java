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
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;


public class Backup
{

	private File	file;
	private String	date;
	private String	length;



	/**
	 * @param fileName
	 * @param date
	 * @param length
	 */
	@SuppressLint( "SimpleDateFormat" )
	public Backup(File file, long date, long length)
	{
		this.file = file;
		this.date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
		this.length = length / Byte.SIZE + " KiB";
	}



	public String getFilePath()
	{
		return file.getAbsolutePath();
	}



	public String getDate()
	{
		return date;
	}



	public String getLength()
	{
		return length;
	}



	public void setFileName(File backup)
	{
		this.file = backup;
	}



	@SuppressLint( "SimpleDateFormat" )
	public void setDate(long date)
	{
		this.date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
	}



	public void setLength(long length)
	{
		this.length = length / Byte.SIZE + " KiB";
	}

}
