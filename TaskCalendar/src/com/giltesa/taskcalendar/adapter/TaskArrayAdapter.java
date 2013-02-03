/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.adapter
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/adapter/TaskArrayAdapter.java
    
    Class:
    			public  class TaskArrayAdapter
    			private class TaskViewHolder    
*/


package com.giltesa.taskcalendar.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.activity.SettingsTags;
import com.giltesa.taskcalendar.helper.MySQLiteHelper;
import com.giltesa.taskcalendar.util.Task;


public class TaskArrayAdapter extends ArrayAdapter< Task >
{

	Activity	context;
	Task[]		tasks;



	/**
	 * @param context
	 * @param tasks
	 */
	public TaskArrayAdapter(Activity context, Task[] tasks)
	{
		super(context, R.layout.settings_tags_listitem, tasks);
		this.context = context;
		this.tasks = tasks;
	}



	/**
	 * @param context
	 * @param simpleSpinnerItem
	 * @param tasks
	 */
	public TaskArrayAdapter(Activity context, int simpleSpinnerItem, Task[] tasks)
	{
		super(context, simpleSpinnerItem, tasks);
		this.context = context;
		this.tasks = tasks;
	}



	/**
	 * 
	 */
	public View getView(int position, View item, ViewGroup parent)
	{
		TaskViewHolder holder;

		if( item == null )
		{
			LayoutInflater inflater = context.getLayoutInflater();
			item = inflater.inflate(R.layout.main_tasks_listitem, null);

			holder = new TaskViewHolder();

			holder.id = (TextView)item.findViewById(R.id.task_listitem_id);
			holder.idTag = (TextView)item.findViewById(R.id.task_listitem_idTag);
			holder.date = (TextView)item.findViewById(R.id.task_listitem_date);
			holder.title = (TextView)item.findViewById(R.id.task_listitem_title);
			holder.description = (TextView)item.findViewById(R.id.task_listitem_description);
			holder.color = (TextView)item.findViewById(R.id.task_listitem_color);

			item.setTag(holder);
		}
		else
		{
			holder = (TaskViewHolder)item.getTag();
		}

		holder.id.setText(tasks[position].getID() + "");
		holder.idTag.setText(tasks[position].getIdTag() + "");
		holder.date.setText(tasks[position].getDate());
		holder.title.setText(tasks[position].getTitle());
		holder.description.setText(tasks[position].getDescription());
		holder.color.setBackgroundColor(Color.parseColor(tasks[position].getColor()));

		// Se obtiene el color que corresponde a la etiqueta:
		//SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
		//Cursor cursorTags = db.rawQuery("SELECT color FROM tags WHERE id = " + tasks[position].getId(), null);
		//cursorTags.moveToFirst();
		//holder.color.setBackgroundColor(Color.parseColor(cursorTags.getString(0)));
		//db.close();

		return( item );
	}



	/**
	 * Clase TaskViewHolder:
	 */
	private class TaskViewHolder
	{
		public TextView	id;
		public TextView	idTag;
		public TextView	date;
		public TextView	title;
		public TextView	description;
		public TextView	color;

	}


}
