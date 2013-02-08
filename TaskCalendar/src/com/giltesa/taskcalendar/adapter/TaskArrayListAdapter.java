/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.adapter
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/taskArrayListAdapter/TaskArrayListAdapter.java
    
    Class:
    			public  class TaskArrayListAdapter
    			private class TaskViewHolder    
*/


package com.giltesa.taskcalendar.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.util.Task;


public class TaskArrayListAdapter extends ArrayAdapter< Task >
{

	Activity			context;
	ArrayList< Task >	taskArrayList;



	/**
	 * @param context
	 * @param tasks
	 */
	public TaskArrayListAdapter(Activity context, ArrayList< Task > taskArrayList)
	{
		super(context, R.layout.settings_tags_listitem, taskArrayList);
		this.context = context;
		this.taskArrayList = taskArrayList;
	}



	/**
	 * @param context
	 * @param simpleSpinnerItem
	 * @param tasks
	 */
	public TaskArrayListAdapter(Activity context, int simpleSpinnerItem, ArrayList< Task > taskArrayList)
	{
		super(context, simpleSpinnerItem, taskArrayList);
		this.context = context;
		this.taskArrayList = taskArrayList;
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

		holder.id.setText(taskArrayList.get(position).getID() + "");
		holder.idTag.setText(taskArrayList.get(position).getIdTag() + "");
		holder.date.setText(taskArrayList.get(position).getDate());
		holder.title.setText(taskArrayList.get(position).getTitle());
		holder.description.setText(taskArrayList.get(position).getDescription());
		holder.color.setBackgroundColor(Color.parseColor(taskArrayList.get(position).getColor()));

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
