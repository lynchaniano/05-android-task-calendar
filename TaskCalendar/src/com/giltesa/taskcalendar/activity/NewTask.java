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
import java.util.ArrayList;
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
import com.giltesa.taskcalendar.adapter.TagArrayListAdapter;
import com.giltesa.taskcalendar.helper.MySQLiteHelper;
import com.giltesa.taskcalendar.helper.PreferenceHelper;
import com.giltesa.taskcalendar.helper.TagHelper;
import com.giltesa.taskcalendar.util.Tag;


public class NewTask extends Activity
{
	private EditText	tagTitle;
	private EditText	tagDescription;
	private Spinner		tagListSpinner;
	private Bundle		dataReceived;




	/**
	 * Al crearse el activity se recuperan todos los controles y se autorellenan si es necesario.
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	@SuppressLint( "NewApi" )
	public void onCreate(Bundle savedInstanceState)
	{
		// Se establece el theme del Activity:
		setTheme(new PreferenceHelper(this).getTheme());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_task);

		// Activa el icono del ActionBar como boton Return:
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Se recuperan los campos del activity:
		tagTitle = (EditText)findViewById(R.id.main_newtask_title_text);
		tagDescription = (EditText)findViewById(R.id.main_newtask_description_text);
		tagListSpinner = (Spinner)findViewById(R.id.main_newtask_tag_spinner);

		// Se carga la lista de etiquetas en el Spinner:
		ArrayList< Tag > tagArrayList = new TagHelper(this).getTagArrayList();
		TagArrayListAdapter tagArrayListAdapter = new TagArrayListAdapter(this, R.layout.settings_tags_listitem_spinner, tagArrayList);
		tagListSpinner.setAdapter(tagArrayListAdapter);

		// Al construirse el activity hay que preconfigurar el formulario segun si le hemos dado a "Nueva tarea" o a "Editar tarea".
		dataReceived = getIntent().getBundleExtra("dataActivity");

		// En ambos casos se autoselecciona el item del Spinner para que coincida con la ultima pagina vista:
		tagListSpinner.setSelection(dataReceived.getInt("positionTag"));

		// Ademas si le hemos dado a "Editar Tarea" se mostrara la informacion que ya contuviera la tarea:
		if( !dataReceived.getBoolean("isNewTask") )
		{
			tagTitle.setText(dataReceived.getString("title"));
			tagDescription.setText(dataReceived.getString("description"));
		}
	}



	/**
	 * Se carga el ActionBar en el activity
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_task_actionbar, menu);
		return true;
	}



	/**
	 * Se tratan los eventos de los botones del ActionBar
	 */
	@SuppressLint( "SimpleDateFormat" )
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
			case android.R.id.home:
				finish();
				break;

			case R.id.main_newtask_actionbar_save:
				if( tagTitle.getText().length() == 0 )
				{
					Toast.makeText(this, getString(R.string.main_newtask_requeridFields), Toast.LENGTH_LONG).show();
				}
				else
				{
					SQLiteDatabase db = MySQLiteHelper.getInstance(NewTask.this).getWritableDatabase();
					int idTag = ( (Tag)tagListSpinner.getSelectedItem() ).getID();

					if( dataReceived.getBoolean("isNewTask") )
					{
						// Se prepara la fecha:
						Date date = Calendar.getInstance().getTime();
						String formattedDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);

						// Se inserta la nueva tarea:
						db.execSQL("INSERT INTO task VALUES ( NULL, ?, ?, ?, ?);", new Object[] { idTag, formattedDate, tagTitle.getText().toString(), tagDescription.getText().toString() });
						Toast.makeText(this, getString(R.string.main_newtask_taskInserted), Toast.LENGTH_LONG).show();
					}
					else
					{
						// Se actualiza la tarea con la nueva informacion
						db.execSQL("UPDATE task SET title = ?, description = ?, tag_id = ? WHERE id = ?;", new Object[] { tagTitle.getText(), tagDescription.getText().toString(), idTag, dataReceived.getInt("id") });
						Toast.makeText(this, getString(R.string.main_newtask_taskUpdated), Toast.LENGTH_LONG).show();
					}
					db.close();

					Intent intent = new Intent();
					Bundle dataReturned = new Bundle();
					dataReturned.putInt("positionTag", tagListSpinner.getSelectedItemPosition());
					intent.putExtra("dataActivity", dataReturned);
					setResult(11, intent);
					finish();
				}
				break;

			default:
				break;
		}

		return true;
	}
}
