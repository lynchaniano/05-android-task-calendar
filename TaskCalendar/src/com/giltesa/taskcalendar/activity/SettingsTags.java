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
			Se ha tenido que ajustar el ancho del Diañog para que no ocupe todo el ancho de la pantalla. Por alguna razon no lo hace automaticamente, pero si lo hace con el alto.
			Habria que revisar porque sucede eso. Por el momento la clase ColorPickerDialog no tiene nada raro ni que ajuste los tamaños del Dialog por lo que es mas raro aun que una medida salga bien y la otra no, 
			quiza pueda ser debido a las propiedades de ancho y alto de los XML, aunque no tiene mucho sentido ya que es un Dialog que es contenido en la pantalla, no en un item, lisview, etc.
			Ademas este ajuste no es del todo preciso si la pantalla es de distinto tamaño...
			En el ejemplo que encontre tambien sucede lo mismo, quizas tenga que ver con la version de android ya que en las fotos si que se ve bien...
	
		ELIMINACION DE TAGS:
			No ha habido forma de conseguir eliminar Items del ListView, se podian recuperar correctamente y modificarlos pero no eliminarlos, siempre da error debido seguramente a un mal uso de los metodos.
			Se ha obtado por actualizar el array de tags y cargarlo de nuevo al TexView, asi se actualizan todos los tags de golpe. (solucionar los problemas a cañonazos)
			
	INFO:
		http://android.okhelp.cz/color-picker-dialog-android-example/
		http://stackoverflow.com/questions/4506708/android-convert-color-int-to-hexa-string
*/


package com.giltesa.taskcalendar.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
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

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.adapter.TagArrayAdapter;
import com.giltesa.taskcalendar.helper.MySQLiteHelper;
import com.giltesa.taskcalendar.helper.PreferenceHelper;
import com.giltesa.taskcalendar.helper.TagHelper;
import com.giltesa.taskcalendar.util.ColorPickerDialog;
import com.giltesa.taskcalendar.util.ColorPickerDialog.OnColorChangedListener;
import com.giltesa.taskcalendar.util.Tag;


public class SettingsTags extends Activity
{
	private PreferenceHelper	prefs;
	private Tag[]				tags;



	/**
	 * Desde este onCreate se carga toda la interfaz, se recupera la información de la base de datos y se representa como una lista en pantalla.
	 */
	@SuppressLint( "NewApi" )
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// Se aplica el theme que corresponda:
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		// Se llama al padre y se establece el layout para el activity:
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_tags);

		// Permite que el icono del ActionBar se comporte como el botón atrás, y su evento sea tratado desde onOptionsItemSelected()
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);


		// Se recuperan todos los Tags de la BD:
		tags = new TagHelper(this).getArrayTags();//getTagsInDataBase();


		// Una vez con toda la información en el array se traspasa al ListView:
		TagArrayAdapter adapter = new TagArrayAdapter(this, tags);
		ListView list = (ListView)findViewById(R.id.tags_list_items);
		list.setAdapter(adapter);


		// Se añade el evento a los item del ListView:
		list.setTextFilterEnabled(true);
		list.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(final AdapterView< ? > parent, final View view, final int position, long id)
			{
				// Se recupera el tag que lanzo el evento y su adapter:
				final Tag tag = (Tag)parent.getItemAtPosition(position);
				final TagArrayAdapter adapter = (TagArrayAdapter)parent.getAdapter();

				// Se instancia un PopupMenu para mostrar las opciones del tag:
				PopupMenu popup = new PopupMenu(parent.getContext(), view);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.settings_tags_menu, popup.getMenu());

				// Se crea el listener del PopupMenu para tratar los eventos de los subitems:
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{
					public boolean onMenuItemClick(MenuItem item)
					{
						AlertDialog.Builder alert;

						// Se actualiza el nombre, o el color o se elimita el tag:
						switch( item.getItemId() )
						{
							case R.id.settings_tags_popupmenu_name:
								// Se instancia un AlertDialog y se le asigna un titulo y un mensaje:
								alert = new AlertDialog.Builder(SettingsTags.this);
								alert.setTitle(getString(R.string.settings_tags_popupmenu_name_title));
								alert.setMessage(getString(R.string.settings_tags_popupmenu_name_message) + " " + tag.getName());

								// Se le inserta un EditText:
								final EditText input = new EditText(SettingsTags.this);
								alert.setView(input);

								// Se crean los listeners para los botones del AlertDialog:
								alert.setNegativeButton(android.R.string.cancel, null);
								alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Se recupera el nuevo nombre:
										String name = validateNameTag(input.getText(), tag.getName());
										tag.setName(name);


										// Se actualiza la BD:
										SQLiteDatabase db = MySQLiteHelper.getInstance(SettingsTags.this).getWritableDatabase();
										db.execSQL("UPDATE tags SET name = ? WHERE id = ?;", new Object[] { tag.getName(), tag.getID() });
										db.close();

										// Se actualiza el ListView con los cambios:
										adapter.notifyDataSetChanged();
									}
								});
								alert.show();
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
										SQLiteDatabase db = MySQLiteHelper.getInstance(SettingsTags.this).getWritableDatabase();
										db.execSQL("UPDATE tags SET color = ? WHERE id = ?;", new Object[] { tag.getColor(), tag.getID() });
										db.close();

										// Se actualiza el ListView con los cambios:
										adapter.notifyDataSetChanged();
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
								alert = new AlertDialog.Builder(SettingsTags.this);
								alert.setTitle(getString(R.string.settings_tags_popupmenu_delete_title));
								alert.setMessage(getString(R.string.settings_tags_popupmenu_delete_message1) + tag.getName() + getString(R.string.settings_tags_popupmenu_delete_message2));

								// Se crean los listeners para los botones del AlertDialog:
								alert.setNegativeButton(android.R.string.cancel, null);
								alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Al borrar la etiqueta tambien se borran sus tareas, no se dejan huerfanas en la base de datos:
										SQLiteDatabase db = MySQLiteHelper.getInstance(SettingsTags.this).getWritableDatabase();
										db.execSQL("DELETE FROM tags WHERE id = ?;", new Object[] { tag.getID() });
										db.execSQL("DELETE FROM task WHERE tag_id = ?;", new Object[] { tag.getID() });
										db.close();

										// Se recuperan todos los Tags de la BD:
										tags = new TagHelper(SettingsTags.this).getArrayTags();

										// Se actualiza el ListView con los cambios:
										TagArrayAdapter adapter = new TagArrayAdapter(SettingsTags.this, tags);
										ListView list = (ListView)findViewById(R.id.tags_list_items);
										list.setAdapter(adapter);
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


	} //Fin onCreate



	/**
	 * Devuelve las etiquetas de la base de datos como un array de Tags.
	 * 
	 * @return
	 */
	/*
	private Tag[] getTagsInDataBase()
	{
		Tag[] tags;

		// Se realiza una conexion a la base de datos "TaskCalendar":
		SQLiteDatabase db = MySQLiteHelper.getInstance(SettingsTags.this).getWritableDatabase();


		// Después se recupera el número de etiquetas que hay en la tabla, se instancia el array de "tags" con el tamaño de tags recuperado de la consulta a la base de datos:
		Cursor cursor = db.rawQuery("SELECT count() FROM tags", null);
		cursor.moveToFirst();
		tags = new Tag[cursor.getInt(0)];


		// Si no hay etiquetas, se muestra un mensaje para invitar al usuario a que las cree. Si las hay, se carga la información.
		if( tags.length == 0 )
		{
			( (TextView)findViewById(R.id.tags_list_empty) ).setVisibility(TextView.VISIBLE);
		}
		else
		{
			( (TextView)findViewById(R.id.tags_list_empty) ).setVisibility(TextView.GONE);

			// Se recupera toda la información de las etiquetas y se guarda en cada componente del array:
			cursor = db.rawQuery("SELECT id , name , color FROM tags", null);
			if( cursor.moveToFirst() )
			{
				int i = 0;
				do
				{
					int counter = 0; // Revisar Numero de tareas que usan la etiqueta actual (añadir consulta)
					tags[i] = new Tag(cursor.getInt(0), cursor.getString(1), cursor.getString(2), counter);
					i++;
				}
				while( cursor.moveToNext() );
			}
		}

		// Y por ultimo se cierra la base de datos
		db.close();

		return tags;
	}
	*/


	/**
	 * Se infla el menu del ActionBar.
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
				Intent intent = new Intent(this, Settings.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;

			case R.id.tags_actionbar_add:
				// Se instancia un AlertDialog y se le asigna un titulo y un mensaje:
				AlertDialog.Builder alert = new AlertDialog.Builder(SettingsTags.this);
				alert.setTitle(getString(R.string.settings_tags_popupmenu_name_title));

				// Se le inserta un EditText:
				final EditText input = new EditText(SettingsTags.this);
				alert.setView(input);

				// Se crean los listeners para los botones del AlertDialog:
				alert.setNegativeButton(android.R.string.cancel, null);
				alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						// Se inserta la nueva tag en la BD:
						SQLiteDatabase db = MySQLiteHelper.getInstance(SettingsTags.this).getWritableDatabase();
						String name = validateNameTag(input.getText(), getString(R.string.settings_tags_popupmenu_name_defaultName));
						db.execSQL("INSERT INTO tags VALUES ( NULL, ?, '#BDBDBD' );", new Object[] { name });
						db.close();

						// Se recuperan todos los Tags de la BD:
						tags = new TagHelper(SettingsTags.this).getArrayTags();//getTagsInDataBase();

						// Se actualiza el ListView con los cambios:
						TagArrayAdapter adapter = new TagArrayAdapter(SettingsTags.this, tags);
						ListView list = (ListView)findViewById(R.id.tags_list_items);
						list.setAdapter(adapter);
					}
				});
				alert.show();
				break;

			default:
				break;
		}
		return true;
	}



	/**
	 * Devuelve un string con el nuevo nombre de la etiqueta si no esta en blanco, o en caso contrario el nombre por defecto facilitado.
	 * 
	 * @param nameTag
	 * @param nameDefault
	 * @return
	 */
	private String validateNameTag(Editable nameTag, String nameDefault)
	{
		return ( nameTag.toString().equals("") ) ? nameDefault : "" + nameTag;
	}

}
