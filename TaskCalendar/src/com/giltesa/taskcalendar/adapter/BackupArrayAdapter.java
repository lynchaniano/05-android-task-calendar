/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.adapter
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/adapter/TagArrayAdapter.java
    
    Class:
    			public  class BackupArrayAdapter
    			private class BackupViewHolder
*/


package com.giltesa.taskcalendar.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.util.Backup;


public class BackupArrayAdapter extends ArrayAdapter< Backup >
{

	Activity	context;
	Backup[]	arrayBackups;



	/**
	 * @param context
	 * @param backup
	 */
	public BackupArrayAdapter(Activity context, Backup[] backup)
	{
		super(context, R.layout.settings_backup_listitem, backup);
		this.context = context;
		this.arrayBackups = backup;
	}



	/**
	 * 
	 */
	public View getView(int position, View item, ViewGroup parent)
	{
		BackupViewHolder holder;

		if( item == null )
		{
			LayoutInflater inflater = context.getLayoutInflater();
			item = inflater.inflate(R.layout.settings_backup_listitem, null);

			holder = new BackupViewHolder();
			holder.path = (TextView)item.findViewById(R.id.backup_listitem_filePath);
			holder.date = (TextView)item.findViewById(R.id.backup_listitem_date);
			holder.length = (TextView)item.findViewById(R.id.backup_listitem_length);


			item.setTag(holder);
		}
		else
		{
			holder = (BackupViewHolder)item.getTag();
		}

		holder.path.setText(arrayBackups[position].getFilePath());
		holder.date.setText(arrayBackups[position].getDate());
		holder.length.setText(arrayBackups[position].getLength());


		return( item );
	}



	/**
	 * Clase TagViewHolder:
	 */
	private class BackupViewHolder
	{
		public TextView	path;
		public TextView	date;
		public TextView	length;
	}

}
