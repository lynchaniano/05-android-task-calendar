/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.adapter
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/adapter/TagArrayAdapter.java
    
    Class:
    			public  class TagArrayAdapter
    			private class TagViewHolder    
*/


package com.giltesa.taskcalendar.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.util.Tag;


public class TagArrayAdapter extends ArrayAdapter< Tag >
{

	Activity	context;
	Tag[]		tags;



	/**
	 * @param context
	 * @param tasks
	 */
	public TagArrayAdapter(Activity context, Tag[] tags)
	{
		super(context, R.layout.settings_tags_listitem, tags);
		this.context = context;
		this.tags = tags;
	}



	/**
	 * @param context
	 * @param simpleSpinnerItem
	 * @param tasks
	 */
	public TagArrayAdapter(Activity context, int simpleSpinnerItem, Tag[] tags)
	{
		super(context, simpleSpinnerItem, tags);
		this.context = context;
		this.tags = tags;
	}



	/**
	 * 
	 */
	public View getView(int position, View item, ViewGroup parent)
	{
		TagViewHolder holder;

		if( item == null )
		{
			LayoutInflater inflater = context.getLayoutInflater();
			item = inflater.inflate(R.layout.settings_tags_listitem, null);

			holder = new TagViewHolder();
			holder.id = (TextView)item.findViewById(R.id.tags_listitem_id);
			holder.name = (TextView)item.findViewById(R.id.tags_listitem_name);
			holder.color = (TextView)item.findViewById(R.id.tags_listitem_color);
			holder.counter = (TextView)item.findViewById(R.id.tags_listitem_counter);

			item.setTag(holder);
		}
		else
		{
			holder = (TagViewHolder)item.getTag();
		}

		holder.id.setText(tags[position].getID() + "");
		holder.name.setText(tags[position].getName());
		holder.color.setBackgroundColor(Color.parseColor(tags[position].getColor()));
		holder.counter.setText(tags[position].getCounter() + " " + context.getResources().getString(R.string.settings_tags_task));

		return( item );
	}



	/**
	 * Clase TagViewHolder:
	 */
	private class TagViewHolder
	{
		public TextView	id;
		public TextView	name;
		public TextView	color;
		public TextView	counter;
	}


}
