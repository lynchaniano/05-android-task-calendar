/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/activity/Main.java
*/
/*
	NOTAS:
		Campo de busqueda:
			http://www.edumobile.org/android/android-development/action-bar-search-view/
			
		PopPup Menu:
			http://developer.android.com/guide/topics/ui/menus.html#PopupMenu
			
		Paso de parametros entre Activities:
			http://stackoverflow.com/questions/12233106/really-not-getting-setresult-and-onactivityresult
*/


package com.giltesa.taskcalendar.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.adapter.TaskArrayListAdapter;
import com.giltesa.taskcalendar.helper.PreferenceHelper;
import com.giltesa.taskcalendar.helper.TagHelper;
import com.giltesa.taskcalendar.helper.TaskHelper;
import com.giltesa.taskcalendar.util.Tag;
import com.giltesa.taskcalendar.util.Task;


@SuppressLint( "NewApi" )
public class Main extends FragmentActivity implements SearchView.OnQueryTextListener
{
	private static SectionsPagerAdapter	mSectionsPagerAdapter;
	private static ViewPager			mViewPager;
	private SearchView					mSearchView;

	protected PreferenceHelper			prefs;
	private static Activity				context;
	static ArrayList< Tag >				tagArrayList;
	static ArrayList< Task >			taskArrayList;
	static ListView						listTask;
	static TaskArrayListAdapter			taskArrayListAdapter;
	private static TaskHelper			taskHelper;



	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Create the taskArrayListAdapter that will return a fragment for each of the three primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections taskArrayListAdapter.
		mViewPager = (ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		tagArrayList = new TagHelper(this).getTagArrayList();
		taskHelper = new TaskHelper(this);
		context = this;
	}



	/**
	 * 
	 */
	@Override
	public void onStart()
	{
		super.onStart();
	}



	/**
	 * 
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
	}



	/**
	 * Se tratan los posibles Intents recibidos de otros activities:
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		// Si el result es del activity NewTask:
		if( requestCode == 1 && resultCode == 11 )
		{
			mViewPager.setCurrentItem(data.getBundleExtra("dataActivity").getInt("positionTag"));


			// :: Falta actualizar el ListView despues de editar...
			taskArrayListAdapter.notifyDataSetChanged(); // Revisar no hace nada
		}
	}



	/**
	 * Se capturan todas las pulsaciones de las teclas físicas del dispositivo para poder personalizar los eventos que lanzan.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		//Si la tecla pulsada es la de Atrás, y el usuario ha configurado la solicitud de confirmación. Se pide dicha solicitud:
		if( keyCode == KeyEvent.KEYCODE_BACK && prefs.isConfirmExit() )
		{
			// Se muestra el mensaje por pantalla:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setIcon(android.R.drawable.ic_dialog_alert);
			alert.setTitle(getResources().getString(R.string.main_exit_title));
			alert.setMessage(getResources().getString(R.string.main_exit_summary));
			alert.setNegativeButton(android.R.string.cancel, null);
			alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					finish();
				}
			});
			alert.show();
			// Como se ha tratado el evento de la tecla atrás, se devuelve true.
			return true;
		}
		// Si aquí no se ha tratado el evento se le envía al padre para que lo trate él.
		return super.onKeyDown(keyCode, event);
	}



	/**
	 * 
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
	}



	/**
	 * Se carga el ActionBar y se recupera el Campo de Busqueda.
	 */
	@SuppressLint( "NewApi" )
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		inflater.inflate(R.menu.main_actionbar, menu);

		//Cuadro de busqueda
		MenuItem searchItem = menu.findItem(R.id.main_actionbar_search);
		mSearchView = (SearchView)searchItem.getActionView();
		setupSearchView(searchItem);

		return true;
	}



	/**
	 * Desde el metodo onOptionsItemSelected(), se tratan los eventos que produzcan los diferentes Items de los Menus.
	 * Se tratan tanto los eventos del menu del boton fisico como los producidos por el boton del ActionBar.
	 */
	@SuppressLint( "NewApi" )
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
		//	Se trata el evento del boton Menu, y de sus SubMenus metidos en un PopupMenu:
			case R.id.main_actionbar_menu:
				PopupMenu popup = new PopupMenu(this, findViewById(R.id.main_actionbar_menu));
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.main_menu, popup.getMenu());

				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{
					public boolean onMenuItemClick(MenuItem item)
					{
						switch( item.getItemId() )
						{
							case R.id.main_menu_newtask:
								// Se crea un Intent y un Bundle con la informacion a enviar al nuevo Activity:
								Intent intent = new Intent(context, NewTask.class);
								Bundle bundle = new Bundle();
								bundle.putBoolean("isNewTask", true);
								bundle.putInt("positionTag", mViewPager.getCurrentItem());
								intent.putExtra("dataActivity", bundle);
								startActivityForResult(intent, 1);
								return true;

							case R.id.main_menu_settings:
								startActivity(new Intent(Main.this, Settings.class));
								return true;

							case R.id.main_menu_exit:
								finish();
								return true;

							default:
								return false;
						}
					}
				});
				popup.show();
				return true;


			case R.id.main_actionbar_search:
				return true;


			case R.id.main_menu_newtask:
				// Se crea un Intent y un Bundle con la informacion a enviar al nuevo Activity:
				Intent intent = new Intent(context, NewTask.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNewTask", true);
				bundle.putInt("positionTag", mViewPager.getCurrentItem());
				intent.putExtra("dataActivity", bundle);
				startActivityForResult(intent, 1);
				return true;

			case R.id.main_menu_settings:
				startActivity(new Intent(Main.this, Settings.class));
				return true;

			case R.id.main_menu_exit:
				finish();
				return true;

			default:
				return false;
		}
	}



	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}



		/**
		 * 
		 */
		@Override
		public Fragment getItem(int i)
		{
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
			fragment.setArguments(args);
			return fragment;
		}



		/**
		 * Devuelve el numero de elementos/menus del slider.
		 */
		@Override
		public int getCount()
		{
			return tagArrayList.size();
		}



		/**
		 * Devuelve el nombre del elemento del slider segun la posicion correspondiente.
		 */
		@Override
		public CharSequence getPageTitle(int position)
		{
			if( tagArrayList.size() > 0 )
				return tagArrayList.get(position).getName();
			else
				return null;
		}
	}



	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment
	{
		public DummySectionFragment()
		{
		}

		public static final String	ARG_SECTION_NUMBER	= "section_number";



		/**
		 * Devuelve el View que contiene la informacion de la pagina del slider que se esta viendo.
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			taskArrayList = new TaskHelper(context).getArrayTasks(tagArrayList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getID());

			listTask = new ListView(context);
			listTask.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView< ? > parent, final View view, final int position, long id)
				{
					// Se recupera la tarea que ha lanzado el evento. Tambien su taskArrayListAdapter:
					final Task task = (Task)parent.getItemAtPosition(position);

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
									bundle.putInt("positionTag", mViewPager.getCurrentItem());
									bundle.putInt("id", task.getID());
									bundle.putInt("idTag", task.getIdTag());
									bundle.putString("title", task.getTitle());
									bundle.putString("description", task.getDescription());

									// Y por ultimo se adjunta el Bundle con los parametros al Intent y se envia al nuevo Activity:
									intent.putExtra("dataActivity", bundle);
									startActivityForResult(intent, 1);
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
											taskHelper.deleteTask(task);

											// Se elimina la etiqueta y se refresca la pantalla:
											taskArrayList.remove(position);
											taskArrayListAdapter.notifyDataSetChanged();
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


			taskArrayListAdapter = new TaskArrayListAdapter(context, taskArrayList);
			listTask.setAdapter(taskArrayListAdapter);
			return listTask;
		}
	}



	/**
	 * @param searchItem
	 */
	private void setupSearchView(MenuItem searchItem)
	{
		searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		mSearchView.setOnQueryTextListener(this);
	}



	/**
	 * Este metodo permite obtener el texto del campo de busqueda en tiempo real (caracter a caracter)
	 */
	public boolean onQueryTextChange(String newText)
	{
		return false;
	}



	/**
	 * Obtiene el texto del cuadro de busqueda al pulsar ENVIAR
	 * y lo envia al activity que muestra el resultado por pantalla:
	 */
	public boolean onQueryTextSubmit(String query)
	{
		if( query.length() > 0 )
		{
			Intent intent = new Intent(context, SearchResults.class);
			Bundle bundle = new Bundle();
			bundle.putString("query", query);
			intent.putExtra("dataSearch", bundle);
			startActivity(intent);
			return true;
		}
		return false;
	}

}
