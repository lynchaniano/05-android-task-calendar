/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.util
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/helper/TagHelper.java
*/


package com.giltesa.taskcalendar.helper;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.giltesa.taskcalendar.util.Tag;


public class TagHelper
{
	private Activity	context;



	/**
	 * @param context
	 */
	public TagHelper(Activity context)
	{
		this.context = context;
	}



	/**
	 * Devuelve todas las etiquetas de la base de datos.
	 * 
	 * @return
	 */
	public ArrayList< Tag > getTagArrayList()
	{
		ArrayList< Tag > tagArrayList;

		// Se realiza una conexion a la base de datos "TaskCalendar":
		SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();


		// Después se recupera el número de etiquetas que hay en la tabla, se instancia el array de "tags" con el tamaño de tags recuperado de la consulta a la base de datos:
		Cursor cTags = db.rawQuery("SELECT count() FROM tags", null);
		cTags.moveToFirst();
		tagArrayList = new ArrayList< Tag >();


		if( cTags.getInt(0) > 0 )
		{
			// Se recupera toda la información de las etiquetas y se guarda en cada componente del array:
			cTags = db.rawQuery("SELECT id , name , color FROM tags", null);
			if( cTags.moveToFirst() )
			{
				int i = 0;
				Cursor cTask;
				do
				{
					tagArrayList.add(new Tag(cTags.getInt(0), cTags.getString(1), cTags.getString(2), 0));

					cTask = db.rawQuery("SELECT count() FROM task WHERE tag_id = " + cTags.getInt(0), null);
					cTask.moveToFirst();
					tagArrayList.get(i).setCounter(cTask.getInt(0));
					cTask.close();

					i++;
				}
				while( cTags.moveToNext() );
			}
			cTags.close();
		}

		// Y por ultimo se cierra la base de datos
		db.close();

		return tagArrayList;
	}



	/**
	 * Inserta una etiqueta en la base de datos.
	 * 
	 * @param tag
	 */
	public void insertTag(String name)
	{
		SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
		db.execSQL("INSERT INTO tags VALUES ( NULL, ?, '#BDBDBD' );", new Object[] { name });
		db.close();
	}



	/**
	 * Actualiza la etiqueta en la base de datos.
	 * 
	 * @param tag
	 */
	public void updateTag(Tag tag)
	{
		SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
		db.execSQL("UPDATE tags SET name = ?, color = ? WHERE id = ?;", new Object[] { tag.getName(), tag.getColor(), tag.getID() });
		db.close();
	}



	/**
	 * Elimina la etiqueta indicada y todas sus tareas asociadas de la base de datos
	 * 
	 * @param idTag
	 */
	public void deleteTagAndTasks(int idTag)
	{
		SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
		db.execSQL("DELETE FROM tags WHERE id = ?;", new Object[] { idTag });
		db.execSQL("DELETE FROM task WHERE tag_id = ?;", new Object[] { idTag });
		db.close();
	}



	/**
	 * Devuelve la ultima etiqueta de la base de datos.
	 * 
	 * @return
	 */
	public Tag getTagLast()
	{
		Tag temp = null;

		SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
		Cursor cTags = db.rawQuery("SELECT id , name , color FROM tags ORDER BY id DESC LIMIT 1", null);

		if( cTags.moveToFirst() )
		{
			temp = new Tag(cTags.getInt(0), cTags.getString(1), cTags.getString(2), 0);

			Cursor cTask = db.rawQuery("SELECT count() FROM task WHERE tag_id = " + cTags.getInt(0), null);
			cTask.moveToFirst();
			temp.setCounter(cTask.getInt(0));
		}

		cTags.close();
		db.close();

		return temp;
	}


}
