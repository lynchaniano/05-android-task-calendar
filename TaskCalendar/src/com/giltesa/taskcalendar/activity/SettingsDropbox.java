/*
    Author:     Alberto Gil Tesa
    WebSite:    http://giltesa.com
    License:    CC BY-NC-SA 3.0
                http://goo.gl/CTYnN

    Project:    Task Calendar
    Package:    com.giltesa.taskcalendar.activity
    File:        /TaskCalendar/src/com/giltesa/taskcalendar/activity/SettingsDropbox.java
*/
/*
	NOTAS:
		Dropbox
			https://www.dropbox.com/developers/start/setup#android
			https://www.dropbox.com/developers/app_info/134736
			App key		s2wemjz17ezxn05 
			App secret	z36cclycvqqo7xt
			
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SettingsDropbox.this);
				Toast.makeText(SettingsDropbox.this, pref.getString("main_menu_settings_calendar_directoryStorage_key", "fail"), Toast.LENGTH_LONG).show();			
*/


package com.giltesa.taskcalendar.activity;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.adapter.BackupArrayAdapter;
import com.giltesa.taskcalendar.helper.BackupHelper;
import com.giltesa.taskcalendar.helper.PreferenceHelper;
import com.giltesa.taskcalendar.util.Backup;


@SuppressLint( "NewApi" )
public class SettingsDropbox extends Activity
{
	///////////////////////////////////////////////////////////////////////////
	//                      Your app-specific settings.                      //
	///////////////////////////////////////////////////////////////////////////

	// Replace this with your app key and secret assigned by Dropbox.
	// Note that this is a really insecure way to do this, and you shouldn't
	// ship code which contains your key & secret in such an obvious way.
	// Obfuscation is good.
	final static private String			APP_KEY				= "s2wemjz17ezxn05";
	final static private String			APP_SECRET			= "z36cclycvqqo7xt";

	// If you'd like to change the access type to the full Dropbox instead of
	// an app folder, change this value.
	final static private AccessType		ACCESS_TYPE			= AccessType.APP_FOLDER;

	///////////////////////////////////////////////////////////////////////////
	//                      End app-specific settings.                       //
	///////////////////////////////////////////////////////////////////////////

	// You don't need to change these, leave them alone.
	final static private String			ACCOUNT_PREFS_NAME	= "prefs";
	final static private String			ACCESS_KEY_NAME		= "ACCESS_KEY";
	final static private String			ACCESS_SECRET_NAME	= "ACCESS_SECRET";


	DropboxAPI< AndroidAuthSession >	mApi;

	protected PreferenceHelper			prefs;
	private MenuItem					menuItemUpdate, menuItemLogin, menuItemLogout;
	private Backup[]					backupsCloud;
	private ListView					listView;
	private Activity					context;



	/**
	 * 
	 */
	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	@SuppressLint( "NewApi" )
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Se aplica el theme que corresponda:
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		// Se llama al padre y se establece el layout para el activity:
		super.onCreate(savedInstanceState);

		//Permite que el icono de la barra de name se comporte como el boton atras, y su evento sea tratado desde onOptionsItemSelected()
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);


		// We create a new AuthSession so that we can use the Dropbox API.
		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI< AndroidAuthSession >(session);

		// Basic Android widgets
		setContentView(R.layout.settings_dropbox);

		checkAppKeySetup();

		listView = (ListView)findViewById(R.id.dropbox_list_items);

		context = this;

		// Display the proper UI state if logged in or not
		//setLoggedIn(mApi.getSession().isLinked());
	}



	/**
	 * 
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}



	/**
	 * 
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		AndroidAuthSession session = mApi.getSession();

		// The next part must be inserted in the onResume() method of the
		// activity from which session.startAuthentication() was called, so
		// that Dropbox authentication completes properly.
		if( session.authenticationSuccessful() )
		{
			try
			{
				// Mandatory call to complete the auth
				session.finishAuthentication();

				// Store it locally in our app for later use
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				setLoggedIn(true);
			}
			catch( IllegalStateException e )
			{
				showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
				Log.i("Dropbox", "Error authenticating", e);
			}
		}
	}



	/**
	 * Cierra la sesion de Dropbox
	 */
	private void logOut()
	{
		// Remove credentials from the session
		mApi.getSession().unlink();
		// Clear our stored keys
		clearKeys();
		// Change UI state to display logged out version
		setLoggedIn(false);
	}



	/**
	 * Desde el metodo setLoggedIn se puede cambiar la interfaz del activity segun si
	 * hemos iniciado sesion o no en la cuenta de dropbox.
	 */
	private void setLoggedIn(boolean loggedIn)
	{
		// Si estamos autenticados se muestra el boton de actualizacion y cierre de sesion:
		if( loggedIn )
		{
			menuItemUpdate.setVisible(true);
			menuItemLogin.setVisible(false);
			menuItemLogout.setVisible(true);
			loadListView();
		}
		// En caso contrario solo se muestra el boton de inicio de sesion:
		else
		{
			menuItemUpdate.setVisible(false);
			menuItemLogin.setVisible(true);
			menuItemLogout.setVisible(false);
			BackupArrayAdapter adapter = new BackupArrayAdapter(this, new Backup[0]);
			listView.setAdapter(adapter);
		}
	}



	/**
	 * 
	 */
	private void checkAppKeySetup()
	{
		// Check to make sure that we have a valid app key
		if( APP_KEY.startsWith("CHANGE") || APP_SECRET.startsWith("CHANGE") )
		{
			showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
			finish();
			return;
		}

		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + APP_KEY;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = getPackageManager();
		if( 0 == pm.queryIntentActivities(testIntent, 0).size() )
		{
			showToast("URL scheme in your app's " + "manifest is not set up correctly. You should have a " + "com.dropbox.client2.android.AuthActivity with the " + "scheme: " + scheme);
			finish();
		}
	}



	/**
	 * @param msg
	 */
	private void showToast(String msg)
	{
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}



	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a local
	 * store, rather than storing user name & password, and re-authenticating each
	 * time (which is not to be done, ever).
	 * 
	 * @return Array of [access_key, access_secret], or null if none stored
	 */
	private String[] getKeys()
	{
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if( key != null && secret != null )
		{
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		}
		else
		{
			return null;
		}
	}



	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a local
	 * store, rather than storing user name & password, and re-authenticating each
	 * time (which is not to be done, ever).
	 */
	private void storeKeys(String key, String secret)
	{
		// Save the access key for later
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}



	/**
	 * 
	 */
	private void clearKeys()
	{
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}



	/**
	 * @return
	 */
	private AndroidAuthSession buildSession()
	{
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if( stored != null )
		{
			AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
		}
		else
		{
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}



	/**
	 * Se infla el menu del ActionBar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings_cloud_actionbar, menu);
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

			case R.id.cloud_actionbar_update:
				uploadBackups();
				break;

			case R.id.cloud_actionbar_login:
				mApi.getSession().startAuthentication(SettingsDropbox.this);
				break;

			case R.id.cloud_actionbar_logout:
				logOut();
				break;

			default:
				break;
		}
		return true;
	}



	/**
	 * Desde este metodo pueden obtenerse las referencias a los botones del Actionbar, en principio
	 * no se puede desde otro metodo ya que se obtionen las referencias a NULL.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		menuItemUpdate = menu.findItem(R.id.cloud_actionbar_update);
		menuItemLogin = menu.findItem(R.id.cloud_actionbar_login);
		menuItemLogout = menu.findItem(R.id.cloud_actionbar_logout);

		setLoggedIn(mApi.getSession().isLinked());
		return true;
	}



	/**
	 * 
	 */
	private void loadListView()
	{
		// http://stackoverflow.com/questions/10928816/dropbox-and-android-get-files-names-via-api-call
		// https://www.dropbox.com/developers/start/files#android
		// https://www.dropbox.com/static/developers/dropbox-android-sdk-1.5.3-docs/

		try
		{
			// Se recuperan los datos de Dropbox:
			Entry entries = mApi.metadata("/", 100, null, true, null);

			// Se recorren los ficheros para contarlos:
			int numFiles = 0;
			for( Entry e : entries.contents )
				if( !e.isDeleted && !e.isDir )
					numFiles++;

			backupsCloud = new Backup[numFiles];

			// Se recuperan todos los ficheros y se guardan en el array:
			int i = 0;
			for( Entry e : entries.contents )
			{
				if( !e.isDeleted && !e.isDir )
				{
					backupsCloud[i] = new Backup(new File(e.path), e.modified, e.size);
					i++;
				}
			}

			// Una vez con toda la información en el array se traspasa al ListView:
			BackupArrayAdapter adapter = new BackupArrayAdapter(this, backupsCloud);
			listView.setAdapter(adapter);
		}
		catch( DropboxException e )
		{
			e.printStackTrace();
		}
	}



	/**
	 * 
	 */
	private void uploadBackups()
	{
		// Se comparan los ficheros locales y los de dropbox
		// Despues se suben los que no esten en dropbox

		Backup[] backupsSD = new BackupHelper(this).getArrayBackupsInMemorySD();


		for( Backup bSD : backupsSD )
		{
			boolean copiar = true;

			for( Backup bClud : backupsCloud )
			{
				String temp1 = bSD.getFile().getName();
				String temp2 = bClud.getFile().getName();

				if( temp1.equals(temp2) )
					copiar = false;
			}

			if( copiar )
			{
				// Se sube el fichero a dropbox 
				////////////////////////////////////////////////////
				// Uploading content.
				FileInputStream inputStream = null;
				try
				{
					File file = bSD.getFile();
					inputStream = new FileInputStream(bSD.getFile());
					Entry newEntry = mApi.putFile(file.getName(), inputStream, file.length(), null, null);
					Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
				}
				catch( DropboxUnlinkedException e )
				{
					// User has unlinked, ask them to link again here.
					Log.e("DbExampleLog", "User has unlinked.");
				}
				catch( DropboxException e )
				{
					Log.e("DbExampleLog", "Something went wrong while uploading.");
				}
				catch( FileNotFoundException e )
				{
					Log.e("DbExampleLog", "File not found.");
				}
				finally
				{
					if( inputStream != null )
					{
						try
						{
							inputStream.close();
						}
						catch( IOException e )
						{}
					}
				}
				////////////////////////////////////////////////////
			}
		}


		loadListView();
	}
}
