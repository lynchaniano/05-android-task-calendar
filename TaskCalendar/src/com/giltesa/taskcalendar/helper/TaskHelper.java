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

import java.util.ArrayList;

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
	 * Metodo que devuelve las tareas que coincidan con el numero de id del tag que se le indique.
	 * 
	 * @return
	 */
	public ArrayList< Task > getArrayTasks(int idTag)
	{
		ArrayList< Task > taskArrayList;

		// Se realiza una conexion a la base de datos "TaskCalendar":
		SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();


		// Después se recupera el número de etiquetas que hay en la tabla, se instancia el array de "task" con el tamaño de task recuperado de la consulta a la base de datos:
		Cursor cTask = db.rawQuery("SELECT count() FROM task WHERE tag_id = " + idTag, null);
		cTask.moveToFirst();
		taskArrayList = new ArrayList< Task >();


		if( cTask.getInt(0) > 0 )
		{
			// Se realiza la consulta SQL con las columnas que se necesitan y ordenando el resultado segun este configurado en las opciones de configuracion:
			cTask = db.rawQuery("SELECT id, tag_id, creation_date, title, description FROM task WHERE tag_id = " + idTag + " " + prefs.getSortTask(), null);
			if( cTask.moveToFirst() )
			{
				// Se recorren todas las tareas y se introducen en la lista de tareas que se devolvera por valor.
				do
				{
					// Se realiza otra consulta para obtener el color de la etiqueta y asignarselo a la tarea.
					Cursor cTags = db.rawQuery("SELECT color FROM tags WHERE id = " + idTag, null);
					cTags.moveToFirst();
					taskArrayList.add(new Task(cTask.getInt(0), cTask.getInt(1), cTask.getString(2), cTask.getString(3), cTask.getString(4), cTags.getString(0)));
					cTags.close();
				}
				while( cTask.moveToNext() );
			}
			cTask.close();
		}

		// Y por ultimo se cierra la base de datos
		db.close();

		return taskArrayList;
	}



	/**
	 * Metodo que devuleve las tareas que contengan el string recibido por parametro en el titulo o la descripcion.
	 * 
	 * @return
	 */
	public ArrayList< Task > getArrayTasks(String query)
	{
		ArrayList< Task > taskArrayList;

		// Se realiza una conexion a la base de datos "TaskCalendar":
		SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();


		// Después se recupera el número de etiquetas que hay en la tabla, se instancia el array de "tags" con el tamaño de tags recuperado de la consulta a la base de datos:
		Cursor cTask = db.rawQuery("SELECT count() FROM task WHERE title LIKE '%" + query + "%' OR description LIKE '%" + query + "%';", null);
		cTask.moveToFirst();
		taskArrayList = new ArrayList< Task >();


		if( cTask.getInt(0) > 0 )
		{
			//  Se recuperan las columnas teniendo en cuenta el texto a buscar en las tareas y el orden de obtencion:
			cTask = db.rawQuery("SELECT id, tag_id, creation_date, title, description FROM task WHERE title LIKE '%" + query + "%' OR description LIKE '%" + query + "%' " + prefs.getSortTask(), null);
			if( cTask.moveToFirst() )
			{
				do
				{
					Cursor cTags = db.rawQuery("SELECT color FROM tags WHERE id = " + cTask.getInt(1), null);
					cTags.moveToFirst();
					taskArrayList.add(new Task(cTask.getInt(0), cTask.getInt(1), cTask.getString(2), cTask.getString(3), cTask.getString(4), cTags.getString(0)));
					cTags.close();
				}
				while( cTask.moveToNext() );
			}
			cTask.close();
		}

		// Y por ultimo se cierra la base de datos
		db.close();

		return taskArrayList;
	}



	/**
	 * @param task
	 */
	public void deleteTask(Task task)
	{
		SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
		db.execSQL("DELETE FROM task WHERE id = ?;", new Object[] { task.getID() });
		db.close();
	}
}
