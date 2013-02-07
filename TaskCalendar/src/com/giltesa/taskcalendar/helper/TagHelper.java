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
	 * @return
	 */
	public Tag[] getArrayTags()
	{
		Tag[] tags;

		// Se realiza una conexion a la base de datos "TaskCalendar":
		SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();


		// Después se recupera el número de etiquetas que hay en la tabla, se instancia el array de "tags" con el tamaño de tags recuperado de la consulta a la base de datos:
		Cursor cursorTags = db.rawQuery("SELECT count() FROM tags", null);
		cursorTags.moveToFirst();
		tags = new Tag[cursorTags.getInt(0)];


		// Si no hay etiquetas, se muestra un mensaje para invitar al usuario a que las cree. Si las hay, se carga la información.
		if( tags.length > 0 )
		{
			// Se recupera toda la información de las etiquetas y se guarda en cada componente del array:
			cursorTags = db.rawQuery("SELECT id , name , color FROM tags", null);
			if( cursorTags.moveToFirst() )
			{
				int i = 0;
				int idTag;
				Cursor cursorTask;
				do
				{
					idTag = cursorTags.getInt(0);
					tags[i] = new Tag(idTag, cursorTags.getString(1), cursorTags.getString(2), 0);

					cursorTask = db.rawQuery("SELECT count() FROM task WHERE tag_id = " + idTag, null);
					cursorTask.moveToFirst();
					tags[i].setCounter(cursorTask.getInt(0));

					i++;
				}
				while( cursorTags.moveToNext() );
			}
		}

		// Y por ultimo se cierra la base de datos
		db.close();

		return tags;

	}

}
