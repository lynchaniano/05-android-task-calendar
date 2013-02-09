/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/activity/NewTask.java
*/
/*
	NOTAS DEL ACTIVITY:
	
		Desde este Activity no se realiza ninguna modificacion de las tareas. Simplemente se reciben datos y se devuelven.
		Sera el padre, es decir el que llamo a esta tarea, el que se encargue de hacer lo que convenga con esos datos.
		
		Por ejemplo, si es el Activity Main quien abre un NewTask, al hacerlo le enviara los datos, estos pueden ser:
			Solo la posicion del tag en la que nos encotrabamos para que aparezca por defecto en el formulario.
			O ademas los datos de la tarea si le hemos dado a editar.
			
		En ambos casos cuando se termine esos datos se devolveran al Activity y este hara la insercion, actualizacion o nada.
		Esto ha de hacerse asi ya que es la unica forma con la que he sabido refrescar despues la pantalla principal (o la del Activity padre)
*/


package com.giltesa.taskcalendar.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
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
import com.giltesa.taskcalendar.helper.PreferenceHelper;
import com.giltesa.taskcalendar.helper.TagHelper;
import com.giltesa.taskcalendar.helper.TaskHelper;
import com.giltesa.taskcalendar.util.Tag;
import com.giltesa.taskcalendar.util.Task;


public class NewTask extends Activity
{
	private EditText			EditTextTitle;
	private EditText			EditTextDescription;
	private Spinner				ListSpinnerTag;
	private Bundle				dataReceived;
	private static TaskHelper	taskHelper;



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
		EditTextTitle = (EditText)findViewById(R.id.main_newtask_title_text);
		EditTextDescription = (EditText)findViewById(R.id.main_newtask_description_text);
		ListSpinnerTag = (Spinner)findViewById(R.id.main_newtask_tag_spinner);

		// Se carga la lista de etiquetas en el Spinner:
		ArrayList< Tag > tagArrayList = new TagHelper(this).getTagArrayList();
		TagArrayListAdapter tagArrayListAdapter = new TagArrayListAdapter(this, R.layout.settings_tags_listitem_spinner, tagArrayList);
		ListSpinnerTag.setAdapter(tagArrayListAdapter);


		// Al construirse el activity hay que preconfigurar el formulario segun si le hemos dado a "Nueva tarea" o a "Editar tarea".
		dataReceived = getIntent().getBundleExtra("dataActivity");

		// En ambos casos se autoselecciona el item del Spinner para que coincida con la ultima pagina vista:
		ListSpinnerTag.setSelection(dataReceived.getInt("positionSlider"));

		// Ademas si le hemos dado a "Editar Tarea" se mostrara la informacion que ya contuviera la tarea:
		if( !dataReceived.getBoolean("isNewTask") )
		{
			EditTextTitle.setText(dataReceived.getString("title"));
			EditTextDescription.setText(dataReceived.getString("description"));
		}

		taskHelper = new TaskHelper(this);
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
				setResult(Main.BACK, null);
				finish();
				break;

			case R.id.main_newtask_actionbar_save:
				if( EditTextTitle.getText().length() == 0 )
				{
					Toast.makeText(this, getString(R.string.main_newtask_requeridFields), Toast.LENGTH_LONG).show();
				}
				else
				{
					Intent intent = new Intent(this, Main.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle dataReturned = new Bundle();
					dataReturned.putInt("positionSlider", ListSpinnerTag.getSelectedItemPosition());
					intent.putExtra("dataActivity", dataReturned);
					
					Task task = new Task(dataReceived.getInt("id"), ( (Tag)ListSpinnerTag.getSelectedItem() ).getID(), null, EditTextTitle.getText().toString(), EditTextDescription.getText().toString(), null);


					if( dataReceived.getBoolean("isNewTask") )
					{
						taskHelper.insertTask(task);
						Toast.makeText(this, getString(R.string.main_newtask_taskInserted), Toast.LENGTH_LONG).show();
					}
					else
					{
						taskHelper.updateTask(task);
						Toast.makeText(this, getString(R.string.main_newtask_taskUpdated), Toast.LENGTH_LONG).show();
					}

					startActivity(intent);
				}
				break;

			default:
				break;
		}

		return true;
	}
}
