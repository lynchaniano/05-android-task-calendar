/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.adapter
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/taskArrayListAdapter/TagArrayListAdapter.java
    
    Class:
    			public  class TagArrayListAdapter
    			private class TagViewHolder    
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
import com.giltesa.taskcalendar.util.Tag;


public class TagArrayListAdapter extends ArrayAdapter< Tag >
{

	Activity			context;
	ArrayList< Tag >	tagArrayList;



	/**
	 * @param context
	 * @param tagArrayList
	 */
	public TagArrayListAdapter(Activity context, ArrayList< Tag > tagArrayList)
	{
		super(context, R.layout.settings_tags_listitem, tagArrayList);
		this.context = context;
		this.tagArrayList = tagArrayList;
	}



	/**
	 * @param context
	 * @param simpleSpinnerItem
	 * @param tagArrayList
	 */
	public TagArrayListAdapter(Activity context, int simpleSpinnerItem, ArrayList< Tag > tagArrayList)
	{
		super(context, simpleSpinnerItem, tagArrayList);
		this.context = context;
		this.tagArrayList = tagArrayList;
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

		holder.id.setText(tagArrayList.get(position).getID() + "");
		holder.name.setText(tagArrayList.get(position).getName());
		holder.color.setBackgroundColor(Color.parseColor(tagArrayList.get(position).getColor()));
		holder.counter.setText(tagArrayList.get(position).getCounter() + " " + context.getResources().getString(R.string.settings_tags_task));

		return( item );
	}



	/**
	 * Clase TagViewHolder
	 */
	private class TagViewHolder
	{
		public TextView	id;
		public TextView	name;
		public TextView	color;
		public TextView	counter;
	}


}
