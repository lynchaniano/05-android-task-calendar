/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.util
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/helper/TaskHelper.java
*/


package com.giltesa.taskcalendar.helper;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.giltesa.taskcalendar.util.Task;


public class TaskHelper
{
	private Activity			context;
	private PreferenceHelper	prefs;



	/**
	 * @param context
	 */
	public TaskHelper(Activity context)
	{
		this.context = context;
		prefs = new PreferenceHelper(context);
	}



	/**
	 * @return
	 */
	public Task[] getArrayTasks(int idTag)
	{
		Task[] tasks;

		// Se realiza una conexion a la base de datos "TaskCalendar":
		SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();


		// Después se recupera el número de etiquetas que hay en la tabla, se instancia el array de "tags" con el tamaño de tags recuperado de la consulta a la base de datos:
		Cursor cTask = db.rawQuery("SELECT count() FROM task WHERE tag_id = " + idTag, null);
		cTask.moveToFirst();
		tasks = new Task[cTask.getInt(0)];


		if( tasks.length > 0 )
		{
			// 
			cTask = db.rawQuery("SELECT id, tag_id, creation_date, title, description FROM task WHERE tag_id = " + idTag + " ORDER BY " + prefs.getSortTask(), null);
			if( cTask.moveToFirst() )
			{
				int i = 0;
				Cursor cTags;
				do
				{
					String color;

					cTags = db.rawQuery("SELECT color FROM tags WHERE id = " + idTag, null);
					cTags.moveToFirst();
					color = cTags.getString(0);


					tasks[i] = new Task(cTask.getInt(0), cTask.getInt(1), cTask.getString(2), cTask.getString(3), cTask.getString(4), color);
					i++;
				}
				while( cTask.moveToNext() );
			}
		}

		// Y por ultimo se cierra la base de datos
		db.close();

		return tasks;

	}
}
