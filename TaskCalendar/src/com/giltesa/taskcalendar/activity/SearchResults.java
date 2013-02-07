/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/activity/SearchResults.java
*/


package com.giltesa.taskcalendar.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.adapter.TaskArrayAdapter;
import com.giltesa.taskcalendar.helper.MySQLiteHelper;
import com.giltesa.taskcalendar.helper.PreferenceHelper;
import com.giltesa.taskcalendar.helper.TagHelper;
import com.giltesa.taskcalendar.helper.TaskHelper;
import com.giltesa.taskcalendar.util.Tag;
import com.giltesa.taskcalendar.util.Task;


public class SearchResults extends Activity
{

	private Bundle			dataSearch;
	private static Activity	context;



	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	@SuppressLint( "NewApi" )
	public void onCreate(Bundle savedInstanceState)
	{
		setTheme(new PreferenceHelper(this).getTheme());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_search_results);

		//Permite que el icono de la barra de name se comporte como el boton atras, y su evento sea tratado desde onOptionsItemSelected()
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		context = this;

		// Se recupera la consulta:
		dataSearch = getIntent().getBundleExtra("dataSearch");
		String query = dataSearch.getString("query");


		// Se recuperan las tareas encontradas segun la consulta:
		Task[] arrayTasks = new TaskHelper(this).getArrayTasks(query);


		// Se muestra el contenido del activity segun el resultado de la busqueda:
		if( arrayTasks.length == 0 )
		{
			( (TextView)findViewById(R.id.main_search_results_list_empty) ).setVisibility(TextView.VISIBLE);
		}
		else
		{
			( (TextView)findViewById(R.id.main_search_results_list_empty) ).setVisibility(TextView.GONE);

			ListView listTask = (ListView)findViewById(R.id.main_search_results_list_items);
			TaskArrayAdapter adapter = new TaskArrayAdapter(this, arrayTasks);

			listTask.setTextFilterEnabled(true);
			listTask.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(final AdapterView< ? > parent, final View view, final int position, long id)
				{
					// Se recupera la tarea que ha lanzado el evento. Tambien su adapter:
					final Task task = (Task)parent.getItemAtPosition(position);
					//final TaskArrayAdapter mAdapter = (TaskArrayAdapter)parent.getAdapter();

					// Se instancia un menu PopupMenu para mostrar las opciones del Item:
					PopupMenu popup = new PopupMenu(parent.getContext(), view);
					MenuInflater inflater = popup.getMenuInflater();
					inflater.inflate(R.menu.main_task_item_menu, popup.getMenu());

					// Se crea el listener del popupMenu para tratar los eventos de cada subitem:
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
					{
						public boolean onMenuItemClick(MenuItem item)
						{
							// Se trata los eventos de los Items de edicion y eliminacion de tareas:
							switch( item.getItemId() )
							{
								case R.id.main_task_item_menu_edit:
									// Se prepara un Intent con toda la informacion necesaria para el Activity de Editar tarea: 
									Intent intent = new Intent(context, NewTask.class);

									// Se preparan los parametros a pasar dentro de un Bundle:
									Bundle bundle = new Bundle();
									bundle.putBoolean("isNewTask", false);

									// Desde esta activity no hay otra forma de saber la posicion del tag en el Spinner si no es recorriendo todos los tags y comparandolos con el tag_id:
									Tag[] arrayTags = new TagHelper(context).getArrayTags();
									int index;
									for( index = 0 ; index < arrayTags.length ; index++ )
										if( arrayTags[index].getID() == task.getIdTag() )
											break;

									bundle.putInt("positionSpinner", index);
									bundle.putInt("id", task.getID());
									bundle.putInt("idTag", task.getIdTag());
									bundle.putString("title", task.getTitle());
									bundle.putString("description", task.getDescription());

									// Y por ultimo se adjunta el Bundle con los parametros al Intent y se envia al nuevo Activity:
									intent.putExtra("dataTask", bundle);
									startActivity(intent);
									return true;


								case R.id.main_task_item_menu_delete:
									// Se crea un AlertDialog de advertencia para la eliminacion de la tarea:
									AlertDialog.Builder alert = new AlertDialog.Builder(context);
									alert.setTitle(getString(R.string.main_task_item_menu_delete_alert_title));

									// Se crea el listener para tratar los eventos de los botones del AlertDialog:
									alert.setNegativeButton(android.R.string.cancel, null);
									alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
									{
										public void onClick(DialogInterface dialog, int whichButton)
										{
											// Se elimina la tarea de la base de datos:
											SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
											db.execSQL("DELETE FROM task WHERE id = ?;", new Object[] { task.getID() });
											db.close();

											// :: Falta implementar el refresco del listview tras borrar la tarea de la BD. No ha habido forma de conseguirlo...
										}
									});
									alert.show();
									return true;

								default:
									return false;
							}
						}
					});

					// Se muestra el Menú por pantalla:
					popup.show();
				}
			});

			listTask.setAdapter(adapter);
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
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
			case android.R.id.home:
				// Se prepara la informacion que se le enviara al Activity Main:
				Intent intent = new Intent(this, Main.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle dataMain = new Bundle();
				dataMain.putInt("positionSpinner", dataSearch.getInt("positionSpinner"));
				intent.putExtra("dataMain", dataMain);
				startActivity(intent);
				break;

			default:
				break;
		}

		return true;
	}
}
