/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.util
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/helper/BackupHelper.java
*/


package com.giltesa.taskcalendar.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;

import com.giltesa.taskcalendar.util.Backup;


public class BackupHelper
{
	private File	pathBackups;
	private File	pathDataBase;



	public BackupHelper(Activity context)
	{
		pathBackups = new File(new PreferenceHelper(context).getDirectory());
		pathDataBase = new File(Environment.getDataDirectory() + "/data/" + MySQLiteHelper.PACKAGE_NAME + "/databases/", MySQLiteHelper.DATABASE_NAME);
	}



	/**
	 * @return
	 */
	@SuppressLint( "SimpleDateFormat" )
	public Boolean createBackup()
	{
		Date date = Calendar.getInstance().getTime();
		String formattedDate = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
		File newOutput = new File(pathBackups, MySQLiteHelper.DATABASE_NAME + "_" + formattedDate + ".db");

		return copyFile(pathDataBase, newOutput);
	}



	public Boolean restoreBackup(String input)
	{
		return copyFile(new File(input), pathDataBase);
	}



	/**
	 * Copia el fichero de origen al fichero de destino.
	 * 
	 * @param input
	 *            File de origen, debería de apuntar al fichero a copiar.
	 * @param output
	 *            File de destino, apunta a un fichero, que no debería de existir, donde se desea realizar la copia.
	 * @return
	 *         Devuleve true si se realizo la copia correctamente, false en caso contrario.
	 */
	@SuppressWarnings( "resource" )
	private Boolean copyFile(File input, File output)
	{
		try
		{
			FileChannel inChannel = new FileInputStream(input).getChannel();
			FileChannel outChannel = new FileOutputStream(output).getChannel();
			try
			{
				inChannel.transferTo(0, inChannel.size(), outChannel);
			}
			finally
			{
				if( inChannel != null )
					inChannel.close();
				if( outChannel != null )
					outChannel.close();
			}
		}
		catch( Exception e )
		{
			return false;
		}
		return true;
	}



	/**
	 * Devuelve un array de objetos Backup.
	 * Antes de llamar a este metodo habria que haber llamado a checkPathBackups() para asegurarse de que el directorio existe.
	 * 
	 * @return
	 */
	public Backup[] getArrayBackupsInMemorySD()
	{
		File[] listFiles = pathBackups.listFiles();
		Backup[] arrayBackups = new Backup[listFiles.length];

		int i = 0;
		for( File f : listFiles )
		{
			arrayBackups[i] = new Backup(f, f.lastModified(), f.length());
			i++;
		}

		return arrayBackups;
	}



	/**
	 * Intenta eliminar el backup de la SD indicado.
	 * 
	 * @param path
	 * @return
	 */
	public Boolean deleteBackup(String path)
	{
		Boolean result = false;
		File file = new File(path);

		if( file.exists() )
			result = file.delete();

		return result;
	}



	/**
	 * Si el directorio existe se devuelve true, si no existe se intenta crear, si se consigue crear se devuelve true. En caso contrario false.
	 * 
	 * @return
	 */
	public Boolean checkPathBackups()
	{
		Boolean result = true;

		if( !pathBackups.exists() )
		{
			result = pathBackups.mkdirs();
		}

		return result;
	}
}
