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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.helper.PreferenceHelper;


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

	private boolean						mLoggedIn;

	// Android widgets
	private Button						mSubmit;
	private LinearLayout				mDisplay;
	private Button						mPhoto;
	//	private Button						mRoulette;


	private ImageView					mImage;

	private final String				PHOTO_DIR			= "/";

	final static private int			NEW_PICTURE			= 1;
	private String						mCameraFileName;

	protected PreferenceHelper			prefs;



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		super.onCreate(savedInstanceState);

		if( savedInstanceState != null )
		{
			mCameraFileName = savedInstanceState.getString("mCameraFileName");
		}

		// We create a new AuthSession so that we can use the Dropbox API.
		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI< AndroidAuthSession >(session);

		// Basic Android widgets
		setContentView(R.layout.settings_dropbox);

		//Permite que el icono de la barra de name se comporte como el boton atras, y su evento sea tratado desde onOptionsItemSelected()
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		//----------------------------------------

		checkAppKeySetup();

		mSubmit = (Button)findViewById(R.id.auth_button);

		mSubmit.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				// This logs you out if you're logged in, or vice versa
				if( mLoggedIn )
				{
					logOut();
				}
				else
				{
					// Start the remote authentication
					mApi.getSession().startAuthentication(SettingsDropbox.this);
				}
			}
		});

		mDisplay = (LinearLayout)findViewById(R.id.logged_in_display);

		// This is where a photo is displayed
		mImage = (ImageView)findViewById(R.id.image_view);

		// This is the button to take a photo
		mPhoto = (Button)findViewById(R.id.photo_button);

		mPhoto.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent();
				// Picture from camera
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

				// This is not the right way to do this, but for some reason, having
				// it store it in
				// MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

				Date date = new Date();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");

				String newPicFile = df.format(date) + ".jpg";
				String outPath = "/sdcard/" + newPicFile;
				File outFile = new File(outPath);

				mCameraFileName = outFile.toString();
				Uri outuri = Uri.fromFile(outFile);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
				Log.i("Dropbox", "Importing New Picture: " + mCameraFileName);
				try
				{
					startActivityForResult(intent, NEW_PICTURE);
				}
				catch( ActivityNotFoundException e )
				{
					showToast("There doesn't seem to be a camera.");
				}
			}
		});


		// This is the button to take a photo
		/*mRoulette = (Button)findViewById(R.id.roulette_button);
		mRoulette.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{

			}
		});*/

		// Display the proper UI state if logged in or not
		setLoggedIn(mApi.getSession().isLinked());

	}



	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putString("mCameraFileName", mCameraFileName);
		super.onSaveInstanceState(outState);
	}



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



	// This is what gets called on finishing a media piece to import
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if( requestCode == NEW_PICTURE )
		{
			// return from file upload
			if( resultCode == Activity.RESULT_OK )
			{
				Uri uri = null;
				if( data != null )
				{
					uri = data.getData();
				}
				if( uri == null && mCameraFileName != null )
				{
					uri = Uri.fromFile(new File(mCameraFileName));
				}
				File file = new File(mCameraFileName);

				if( uri != null )
				{
					UploadPicture upload = new UploadPicture(this, mApi, PHOTO_DIR, file);
					upload.execute();
				}
			}
			else
			{
				Log.w("Dropbox", "Unknown Activity Result from mediaImport: " + resultCode);
			}
		}
	}



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
	 * Convenience function to change UI state based on being logged in
	 */
	private void setLoggedIn(boolean loggedIn)
	{
		mLoggedIn = loggedIn;
		if( loggedIn )
		{
			mSubmit.setText("Unlink from Dropbox");
			mDisplay.setVisibility(View.VISIBLE);
		}
		else
		{
			mSubmit.setText("Link with Dropbox");
			mDisplay.setVisibility(View.GONE);
			mImage.setImageDrawable(null);
		}
	}



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



	private void clearKeys()
	{
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}



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

			default:
				break;
		}
		return true;
	}

}
