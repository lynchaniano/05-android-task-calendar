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
	private EditText	title;
	private EditText	description;
	private Spinner		listTags;
	private Bundle		dataTask;



	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	@SuppressLint( "NewApi" )
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(new PreferenceHelper(this).getTheme());

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


		// Al construirse el activity hay que preconfigurar el formulario segun si le hemos dado a "Nueva tarea" o a "Editar tarea".
		dataTask = getIntent().getBundleExtra("dataTask");

		// En ambos casos se autoselecciona el item del Spinner para que coincida con la ultima pagina vista.
		listTags.setSelection(dataTask.getInt("positionSpinner"));

		// Ademas si le hemos dado a Editar Tarea se mostrara la informacion que ya contubiera la tarea
		if( !dataTask.getBoolean("isNewTask") )
		{
			title.setText(dataTask.getString("title"));
			description.setText(dataTask.getString("description"));
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
		// Se prepara la informacion que se le enviara al Activity Main al finalizar el formulario:
		Intent intent = new Intent(this, Main.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle dataMain = new Bundle();


		switch( item.getItemId() )
		{
			case android.R.id.home:
				dataMain.putInt("positionSpinner", dataTask.getInt("positionSpinner"));
				intent.putExtra("dataMain", dataMain);
				startActivity(intent);
				break;

			case R.id.main_newtask_actionbar_save:
				// Como minimo se ha de haber introducido un titulo para la tarea:
				if( title.getText().length() <= 0 )
				{
					Toast.makeText(this, getString(R.string.main_newtask_requeridFields), Toast.LENGTH_LONG).show();
				}
				else
				{
					SQLiteDatabase db = MySQLiteHelper.getInstance(NewTask.this).getWritableDatabase();
					int idTag = ( (Tag)listTags.getSelectedItem() ).getID();

					if( dataTask.getBoolean("isNewTask") )
					{
						// Se prepara la fecha:
						Date date = Calendar.getInstance().getTime();
						String formattedDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);

						// Se inserta la nueva tarea:
						db.execSQL("INSERT INTO task VALUES ( NULL, ?, ?, ?, ?);", new Object[] { idTag, formattedDate, title.getText().toString(), description.getText().toString() });
						Toast.makeText(this, getString(R.string.main_newtask_taskInserted), Toast.LENGTH_LONG).show();
					}
					else
					{
						// Se actualiza la tarea con la nueva informacion
						db.execSQL("UPDATE task SET title = ?, description = ?, tag_id = ? WHERE id = ?;", new Object[] { title.getText(), description.getText().toString(), idTag, dataTask.getInt("id") });
						Toast.makeText(this, getString(R.string.main_newtask_taskUpdated), Toast.LENGTH_LONG).show();
					}
					db.close();

					// Se carga el Activity principal:
					dataMain.putInt("positionSpinner", listTags.getSelectedItemPosition());
					intent.putExtra("dataMain", dataMain);
					startActivity(intent);
				}
				return true;

			default:
				break;
		}

		return true;
	}
}
