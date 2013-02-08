/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/activity/SettingsTags.java
*/
/*
	POR IMPLEMENTAR:
		Falta por añadir la consulta que cuente el número de tareas que pertenecen a cada etiqueta. No se podrá hacer hasta que este terminada la parte de las tareas y su BD.
		
	NOTAS:
		CAMBIO DE COLOR DEL TAG:
			Se ha tenido que ajustar el ancho del Diaño para que no ocupe todo el ancho de la pantalla. Por alguna razon no lo hace automaticamente, pero si lo hace con el alto.
			Habria que revisar porque sucede eso. Por el momento la clase ColorPickerDialog no tiene nada raro ni que ajuste los tamaños del Dialog por lo que es mas raro aun que una medida salga bien y la otra no, 
			quiza pueda ser debido a las propiedades de ancho y alto de los XML, aunque no tiene mucho sentido ya que es un Dialog que es contenido en la pantalla, no en un item, lisview, etc.
			Ademas este ajuste no es del todo preciso si la pantalla es de distinto tamaño...
			En el ejemplo que encontre tambien sucede lo mismo, quizas tenga que ver con la version de android ya que en las fotos si que se ve bien...
			
	INFO:
		http://android.okhelp.cz/color-picker-dialog-android-example/
		http://stackoverflow.com/questions/4506708/android-convert-color-int-to-hexa-string
*/


package com.giltesa.taskcalendar.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.adapter.TagArrayListAdapter;
import com.giltesa.taskcalendar.helper.PreferenceHelper;
import com.giltesa.taskcalendar.helper.TagHelper;
import com.giltesa.taskcalendar.util.ColorPickerDialog;
import com.giltesa.taskcalendar.util.ColorPickerDialog.OnColorChangedListener;
import com.giltesa.taskcalendar.util.Tag;


public class SettingsTags extends Activity
{
	private static ArrayList< Tag >		tagArrayList;
	private static TagArrayListAdapter	tagArrayListAdapter;
	private static TagHelper			tagHelper;
	private static TextView				listEmpty;



	/**
	 * 
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// Se aplica el theme que corresponda:
		setTheme(new PreferenceHelper(this).getTheme());

		// Se llama al padre y se establece el layout para el activity:
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_tags);

		// Permite al icono del activity funcionar como boton:
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);


		// Se recupera el TextView que indica si hay o no etiquetas:
		listEmpty = (TextView)findViewById(R.id.tags_list_empty);


		// Se instancia un TagHelper para hacer todas las consultas SQL:
		tagHelper = new TagHelper(this);

		// Se recuperan todas las etiquetas de la BD:
		tagArrayList = tagHelper.getTagArrayList();

		// Se muestra o oculta el mensaje que indica si hay etiquetas o no:
		listEmpty.setVisibility(( tagArrayList.size() <= 0 ) ? TextView.VISIBLE : TextView.GONE);


		// Se crea un taskArrayListAdapter pasandole el array de etiquetas y despues se añade a la lista del activity:
		tagArrayListAdapter = new TagArrayListAdapter(this, tagArrayList);
		ListView listView = (ListView)findViewById(R.id.tags_list_items);
		listView.setAdapter(tagArrayListAdapter);

		// A continuacion se añaden los eventos de la lista y sus items:
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(getOnItemClickListener());

	}



	/**
	 * Metodo privado que devuelve el listener del listView del activity.
	 * Esta separado por simple legibilidad del codigo.
	 * 
	 * @return
	 */
	private OnItemClickListener getOnItemClickListener()
	{
		return new OnItemClickListener()
		{
			@TargetApi( Build.VERSION_CODES.HONEYCOMB )
			@SuppressLint( "NewApi" )
			public void onItemClick(AdapterView< ? > parent, final View view, final int position, long id)
			{
				// Se recupera el Tag que ha lanzado el evento:
				final Tag tag = (Tag)parent.getItemAtPosition(position);

				// Se crea un PopupMenu para mostrar las opciones del tag:
				PopupMenu popupMenu = new PopupMenu(parent.getContext(), view);
				popupMenu.getMenuInflater().inflate(R.menu.settings_tags_menu, popupMenu.getMenu());

				// Se crea el listener del PopupMenu para tratar los eventos de los subitems:
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{
					public boolean onMenuItemClick(MenuItem item)
					{
						AlertDialog.Builder alertDialog;

						// Se actualiza el nombre, o el color o se elimita el tag:
						switch( item.getItemId() )
						{

							case R.id.settings_tags_popupmenu_name:
								// Se instancia un AlertDialog y se le asigna un titulo y un mensaje:
								alertDialog = new AlertDialog.Builder(SettingsTags.this);
								alertDialog.setTitle(getString(R.string.settings_tags_popupmenu_name_title));
								alertDialog.setMessage(getString(R.string.settings_tags_popupmenu_name_message) + " " + tag.getName());

								// Se le inserta un EditText:
								final EditText input = new EditText(SettingsTags.this);
								alertDialog.setView(input);

								// Se crean los listeners para los botones del AlertDialog:
								alertDialog.setNegativeButton(android.R.string.cancel, null);
								alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Se edita el nombre de la etiqueta en la base de datos, antes se valida el nombre, si es correcto se deja tal cual, si no el de por defecto:
										String name = ( input.getText().toString().length() > 0 ) ? input.getText().toString() : getString(R.string.settings_tags_popupmenu_name_defaultName);
										tag.setName(name);
										tagHelper.updateTag(tag);

										// Se actualiza el ListView con los cambios:
										tagArrayListAdapter.notifyDataSetChanged();
									}
								});
								alertDialog.show();
								return true;


							case R.id.settings_tags_popupmenu_color:
								// Se crea un Listener para el ColorPickerDialog que permite cambiar el color del tag:
								OnColorChangedListener colorPickerListener = new OnColorChangedListener()
								{
									public void colorChanged(int color)
									{
										// Se pasa el color a hexadecimal, luego a formato #000000 y por ultimo se actualiza la propiedad del tag:
										String temp = Integer.toHexString(color);
										tag.setColor("#" + temp.substring(2, temp.length()));

										// Se actualiza la BD:
										tagHelper.updateTag(tag);

										// Se actualiza el ListView con los cambios:
										tagArrayListAdapter.notifyDataSetChanged();
									}
								};

								// Se crea el dialog ColorPickerDialog:
								ColorPickerDialog cpd = new ColorPickerDialog(view.getContext(), colorPickerListener, Color.parseColor(tag.getColor()));
								cpd.setTitle(getString(R.string.settings_tags_popupmenu_color_title));
								cpd.show();

								// Se ajusta a mano el ancho del ColorPickerDialog:
								WindowManager.LayoutParams params = cpd.getWindow().getAttributes();
								params.width = 324;
								cpd.getWindow().setAttributes(params);
								return true;


							case R.id.settings_tags_popupmenu_delete:
								// Se crea un AlertDialog y se le asigna un titulo y un mensaje:
								alertDialog = new AlertDialog.Builder(SettingsTags.this);
								alertDialog.setTitle(getString(R.string.settings_tags_popupmenu_delete_title));
								alertDialog.setMessage(getString(R.string.settings_tags_popupmenu_delete_message1) + tag.getName() + getString(R.string.settings_tags_popupmenu_delete_message2));

								// Se crean los listeners para los botones del AlertDialog:
								alertDialog.setNegativeButton(android.R.string.cancel, null);
								alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Se borra la etiqueta y sus tareas asociadas de la base de datos:
										tagHelper.deleteTagAndTasks(tag.getID());

										// Se actualiza el ListView con los cambios:
										tagArrayList.remove(position);
										tagArrayListAdapter.notifyDataSetChanged();

										// Se muestra o oculta el mensaje que indica si hay etiquetas o no:
										listEmpty.setVisibility(( tagArrayList.size() <= 0 ) ? TextView.VISIBLE : TextView.GONE);
									}
								});
								alertDialog.show();
								return true;

							default:
								return false;
						}
					}

				});
				// Se muestra el PopupMenu por pantalla:
				popupMenu.show();

			}
		};
	}



	/**
	 * Se crea el actionBar del Activity
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings_tags_actionbar, menu);
		return true;
	}



	/**
	 * Eventos lanzados desde los botones del ActionBar.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
			case android.R.id.home:
				finish();
				break;

			case R.id.tags_actionbar_add:
				// Se instancia un AlertDialog y se le asigna un titulo y un mensaje:
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsTags.this);
				alertDialog.setTitle(getString(R.string.settings_tags_popupmenu_name_title));

				// Se le inserta un EditText:
				final EditText input = new EditText(SettingsTags.this);
				alertDialog.setView(input);

				// Se crean los listeners para los botones del AlertDialog:
				alertDialog.setNegativeButton(android.R.string.cancel, null);
				alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						// Se inserta la nueva etiqueta en la base de datos, antes se valida el nombre, si es correcto se deja tal cual, si no el de por defecto:
						String name = ( input.getText().toString().length() > 0 ) ? input.getText().toString() : getString(R.string.settings_tags_popupmenu_name_defaultName);
						tagHelper.insertTag(name);

						// Se recupera la etiquete recien insertada (para obtener su id):
						Tag tag = tagHelper.getTagLast();

						// Se inserta la nueva etiqueta en la pantalla y se refresca:
						tagArrayList.add(tag);
						tagArrayListAdapter.notifyDataSetChanged();

						// Se muestra o oculta el mensaje que indica si hay etiquetas o no:
						listEmpty.setVisibility(( tagArrayList.size() <= 0 ) ? TextView.VISIBLE : TextView.GONE);
					}
				});
				alertDialog.show();
				break;

			default:
				break;
		}
		return true;
	}


}
