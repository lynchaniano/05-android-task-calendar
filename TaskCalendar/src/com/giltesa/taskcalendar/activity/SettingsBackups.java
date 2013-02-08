/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:       /TaskCalendar/src/com/giltesa/taskcalendar/activity/SettingsBackups.java
*/


package com.giltesa.taskcalendar.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.adapter.BackupArrayAdapter;
import com.giltesa.taskcalendar.helper.BackupHelper;
import com.giltesa.taskcalendar.helper.PreferenceHelper;
import com.giltesa.taskcalendar.util.Backup;


public class SettingsBackups extends Activity
{
	protected PreferenceHelper	prefs;
	private BackupHelper		backupHelper;
	private Backup[]			arrayBackups;



	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// Se aplica el theme que corresponda:
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		// Se llama al padre y se establece el layout para el activity:
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_backups);

		//Permite que el icono de la barra de name se comporte como el boton atras, y su evento sea tratado desde onOptionsItemSelected()
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);


		// Se recuperan todos los Backups de la memoria:
		// Se intentan recuperar los backups: 
		backupHelper = new BackupHelper(this);

		if( !backupHelper.checkPathBackups() )
		{
			arrayBackups = new Backup[0];
			Toast.makeText(this, getString(R.string.settings_backup_errorPath), Toast.LENGTH_LONG).show();
		}
		else
		{
			arrayBackups = backupHelper.getArrayBackupsInMemorySD();
		}


		// Una vez con toda la información en el array se traspasa al ListView:
		BackupArrayAdapter adapter = new BackupArrayAdapter(this, arrayBackups);
		ListView list = (ListView)findViewById(R.id.backup_list_items);
		list.setAdapter(adapter);


		// Se añade el evento a los item del ListView:
		list.setTextFilterEnabled(true);
		list.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(final AdapterView< ? > parent, final View view, final int position, long id)
			{
				// Se recupera el backup que lanzo el evento y su taskArrayListAdapter:
				final Backup backup = (Backup)parent.getItemAtPosition(position);

				// Se instancia un PopupMenu para mostrar las opciones del backup:
				PopupMenu popup = new PopupMenu(parent.getContext(), view);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.settings_backup_menu, popup.getMenu());

				// Se crea el listener del PopupMenu para tratar los eventos de los subitems:
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
				{
					public boolean onMenuItemClick(MenuItem item)
					{
						AlertDialog.Builder alert;

						// Se actualiza el nombre, o el color o se elimina el backup:
						switch( item.getItemId() )
						{
							case R.id.settings_backup_popupmenu_restore:
								// Se crea un AlertDialog y se le asigna un titulo y un mensaje:
								alert = new AlertDialog.Builder(SettingsBackups.this);
								alert.setTitle(getString(R.string.settings_backup_popupmenu_restore_title));
								alert.setMessage(getString(R.string.settings_backup_popupmenu_restore_message));

								// Se crean los listeners para los botones del AlertDialog:
								alert.setNegativeButton(android.R.string.cancel, null);
								alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Se restaura el backup en la BD:
										backupHelper.restoreBackup(backup.getFile());

										// Se recuperan los backups:
										if( backupHelper.checkPathBackups() )
											arrayBackups = backupHelper.getArrayBackupsInMemorySD();


										// Se actualiza el ListView con los cambios:
										BackupArrayAdapter adapter = new BackupArrayAdapter(SettingsBackups.this, arrayBackups);
										ListView list = (ListView)findViewById(R.id.backup_list_items);
										list.setAdapter(adapter);
									}
								});
								alert.show();
								return true;

							case R.id.settings_backup_popupmenu_delete:
								// Se crea un AlertDialog y se le asigna un titulo y un mensaje:
								alert = new AlertDialog.Builder(SettingsBackups.this);
								alert.setTitle(getString(R.string.settings_backup_popupmenu_delete_title));
								alert.setMessage(getString(R.string.settings_backup_popupmenu_delete_message));

								// Se crean los listeners para los botones del AlertDialog:
								alert.setNegativeButton(android.R.string.cancel, null);
								alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int whichButton)
									{
										// Se elimina el backup de la BD:
										backupHelper.deleteBackup(backup.getFile());

										// Se recuperan los backups:
										if( backupHelper.checkPathBackups() )
											arrayBackups = backupHelper.getArrayBackupsInMemorySD();


										// Se actualiza el ListView con los cambios:
										BackupArrayAdapter adapter = new BackupArrayAdapter(SettingsBackups.this, arrayBackups);
										ListView list = (ListView)findViewById(R.id.backup_list_items);
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
	 * 
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
	}



	/**
	 * Se infla el menu del ActionBar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings_backup_actionbar, menu);
		return true;
	}



	/**
	 * 
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

			case R.id.backup_actionbar_new:
				// Se instancia un AlertDialog y se le asigna un titulo y un mensaje:
				AlertDialog.Builder alert = new AlertDialog.Builder(SettingsBackups.this);
				alert.setTitle(getString(R.string.settings_backup_actionbar_new));

				// Se crean los listeners para los botones del AlertDialog:
				alert.setNegativeButton(android.R.string.cancel, null);
				alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						// Se crea la copia de seguridad
						if( backupHelper.createBackup() )
							Toast.makeText(SettingsBackups.this, getString(R.string.settings_backup_correctCopy), Toast.LENGTH_LONG).show();
						else
							Toast.makeText(SettingsBackups.this, getString(R.string.settings_backup_incorrectCopy), Toast.LENGTH_LONG).show();

						// Se recuperan los backups:
						if( backupHelper.checkPathBackups() )
							arrayBackups = backupHelper.getArrayBackupsInMemorySD();

						// Se actualiza el ListView con los cambios:
						BackupArrayAdapter adapter = new BackupArrayAdapter(SettingsBackups.this, arrayBackups);
						ListView list = (ListView)findViewById(R.id.backup_list_items);
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
}
