/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/activity/NewTask.java
*/


package com.giltesa.taskcalendar.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.adapter.TagArrayAdapter;
import com.giltesa.taskcalendar.helper.MySQLiteHelper;
import com.giltesa.taskcalendar.helper.PreferenceHelper;
import com.giltesa.taskcalendar.helper.TagHelper;
import com.giltesa.taskcalendar.util.Tag;


public class NewTask extends Activity
{
	protected PreferenceHelper	prefs;

	private EditText			title;
	private EditText			description;
	private Spinner				listTags;
	private String				operation;
	private Bundle				dataTask;



	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	@SuppressLint( "NewApi" )
	public void onCreate(Bundle savedInstanceState)
	{
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_task);

		//Permite que el icono de la barra de name se comporte como el boton atras, y su evento sea tratado desde onOptionsItemSelected()
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Campos del activity:
		title = (EditText)findViewById(R.id.main_newtask_title_text);
		description = (EditText)findViewById(R.id.main_newtask_description_text);
		listTags = (Spinner)findViewById(R.id.main_newtask_tag_spinner);

		// Se carga la lista de etiquetas:
		Tag[] arrayTags = new TagHelper(this).getArrayTags();
		TagArrayAdapter adapter = new TagArrayAdapter(this, R.layout.settings_tags_listitem_spinner, arrayTags);
		listTags.setAdapter(adapter);

		// Ahora se intenta recuperar la informacion que una activity anterior haya podido mandar, como cuando queremos editar la tarea en vez de crear una nueva:
		dataTask = getIntent().getBundleExtra("task");

		if( dataTask == null )
		{
			operation = "insert";
		}
		else
		{
			operation = "update";

			// Se agrega la informacion actual de la tarea a los campos del formulario:
			title.setText(dataTask.getString("title"));
			description.setText(dataTask.getString("description"));


			// Se obtiene el numero de indice del tag del Slinner recorriendolos y comparandolos con el idTag recibido.
			int indice;
			for( indice = 0 ; indice < arrayTags.length ; indice++ )
				if( arrayTags[indice].getID() == dataTask.getInt("idTag") )
					break;

			listTags.setSelection(indice);
		}

	}



	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_task_actionbar, menu);

		return true;
	}



	@SuppressLint( "SimpleDateFormat" )
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent intent = new Intent(this, Main.class);

		switch( item.getItemId() )
		{
			case android.R.id.home:
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;

			case R.id.main_newtask_actionbar_save:


				if( title.getText().toString().equals("") )
				{
					Toast.makeText(this, getString(R.string.main_newtask_requeridFields), Toast.LENGTH_LONG).show();
				}
				else
				{
					SQLiteDatabase db = MySQLiteHelper.getInstance(NewTask.this).getWritableDatabase();

					if( operation.equals("insert") )
					{
						//Se prepara la fecha y el id del tag para la nueva tarea:
						Date date = Calendar.getInstance().getTime();
						String formattedDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
						int idTag = ( (Tag)listTags.getSelectedItem() ).getID();

						// Se inserta la nueva tarea:
						db.execSQL("INSERT INTO task VALUES ( NULL, ?, ?, ?, ?);", new Object[] { idTag, formattedDate, title.getText().toString(), description.getText().toString() });
						Toast.makeText(this, getString(R.string.main_newtask_taskInserted), Toast.LENGTH_LONG).show();
					}
					else if( operation.equals("update") )
					{
						int idTag = ( (Tag)listTags.getSelectedItem() ).getID();

						// Se actualiza la tarea con la nueva informacion
						db.execSQL("UPDATE task SET title = ?, description = ?, tag_id = ? WHERE id = ?;", new Object[] { title.getText(), description.getText().toString(), idTag, dataTask.getInt("id") });
						Toast.makeText(this, getString(R.string.main_newtask_taskUpdated), Toast.LENGTH_LONG).show();
					}

					db.close();

					// Se cierra el activity y se regresa atras:
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}

				return true;

			default:
				break;
		}
		return true;
	}
}
