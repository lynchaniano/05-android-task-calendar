/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.util
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/helper/MySQLiteHelper.java
*/

package com.giltesa.taskcalendar.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public final class MySQLiteHelper extends SQLiteOpenHelper
{
	public static final String		PACKAGE_NAME		= "com.giltesa.taskcalendar";
	public static final String		DATABASE_NAME		= "TaskCalendar";
	private static final int		DATABASE_VERSION	= 13;
	private static MySQLiteHelper	instance;


	// Consultas SQL predefinidas:
	private String					sqlCreateTask		= "";
	private String					sqlCreateTags		= "CREATE TABLE tags(id INTEGER PRIMARY KEY, name TEXT, color TEXT );";



	/**
	 * @param context
	 */
	public MySQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}



	/**
	 * @param context
	 * @return
	 */
	public static MySQLiteHelper getInstance(Context context)
	{
		if( instance == null )
		{
			instance = new MySQLiteHelper(context);
		}
		return instance;
	}



	/**
	 * El método onCreate solo se ejecuta en el caso de que la base de datos recibida en el constructor no exista.
	 * Es entonces cuando se ha de aprovechar para crear las tablas y de rellenarlas de ser necesario:
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//Se ejecuta la sentencia SQL de creación de la tabla
		db.execSQL(sqlCreateTags);

	}



	/**
	 * El método onUpgrade se ejecuta únicamente cuando el número de versión recibido en el constructor es diferente del anterior.
	 * Útil si necesitamos modificar la base de datos ya sea con nuevas tablas o columnas. Todos estos cambios se deberán de controlar aquí.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva)
	{
		//Por ahora, si se indica que la version de la base de datos se ha cambiado, se elimina para luego crearse de nuevo:
		db.execSQL("DROP TABLE IF EXISTS tags");
		db.execSQL(sqlCreateTags);
	}

}
