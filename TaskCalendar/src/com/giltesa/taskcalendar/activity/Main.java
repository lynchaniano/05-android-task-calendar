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
*/


package com.giltesa.taskcalendar.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.giltesa.taskcalendar.R;
import com.giltesa.taskcalendar.helper.PreferenceHelper;


public class Main extends FragmentActivity implements SearchView.OnQueryTextListener
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
	 * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
	 * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter		mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager					mViewPager;


	private SearchView			mSearchView;

	protected PreferenceHelper	prefs;




	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		prefs = new PreferenceHelper(this);
		setTheme(prefs.getTheme());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Create the adapter that will return a fragment for each of the three primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
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
					Main.this.finish();
				}
			});
			alert.show();
			// Como se ha tratado el evento de la tecla atrás, se devuelve true.
			return true;
		}
		// Si aquí no se ha tratado el evento se le envía al padre para que lo trate él.
		return super.onKeyDown(keyCode, event);
	}



	@Override
	protected void onResume()
	{
		super.onResume();
	}



	/*
		private void restartApp()
		{
			Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//i.putExtra(FileExplorerApp.EXTRA_FOLDER, currentDir.getAbsolutePath());
			startActivity(i);
		}
	*/


	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		/*ActionBar actionBar = getActionBar();
		if( hasFocus ) actionBar.hide();
		else actionBar.show();*/
	}



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
	 * Se tratan tanto los eventos del menu del boton fisico como los producidos por el boton del ActionBar
	 */
	@Override
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
								startActivity(new Intent(Main.this, NewTask.class));
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
				//Se pulsa el boton de busqueda
				//Toast.makeText(this, "main_actionbar_search", Toast.LENGTH_SHORT).show();
				return true;

			case R.id.main_menu_newtask:
				startActivity(new Intent(Main.this, NewTask.class));
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



		@Override
		public Fragment getItem(int i)
		{
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
			fragment.setArguments(args);
			return fragment;
		}



		@Override
		public int getCount()
		{
			return 4;
		}



		@Override
		public CharSequence getPageTitle(int position)
		{
			switch( position )
			{
				case 0:
					return getString(R.string.title_section1).toUpperCase();
				case 1:
					return getString(R.string.title_section2).toUpperCase();
				case 2:
					return getString(R.string.title_section3).toUpperCase();
				case 3:
					return getString(R.string.title_section4).toUpperCase();
			}
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



		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			Bundle args = getArguments();
			textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)) + " la");
			return textView;
		}
	}



	private void setupSearchView(MenuItem searchItem)
	{
		// Todo esto hacia busquedas en tiempo real de las aplicaciones instaladas en el movil
		// Podria ser util para adatarlo a las busquedas de la base de datos.

		/*if( false )//isAlwaysExpanded()
		{
			mSearchView.setIconifiedByDefault(false);
		}
		else
		{
			SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
			if( searchManager != null )
			{
				List< SearchableInfo > searchables = searchManager.getSearchablesInGlobalSearch();

				SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
				for( SearchableInfo inf : searchables )
				{
					if( inf.getSuggestAuthority() != null && inf.getSuggestAuthority().startsWith("applications") )
					{
						info = inf;
					}
				}
				mSearchView.setSearchableInfo(info);
			}*/

		searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		//}

		mSearchView.setOnQueryTextListener(this);
	}



	/**
	 * Obtiene el texto del cuadro de busqueda en TIEMPO REAL
	 */
	public boolean onQueryTextChange(String newText)
	{
		// TODO Apéndice de método generado automáticamente
		//Toast.makeText(this, "Query = " + newText, Toast.LENGTH_SHORT).show();
		return false;
	}



	/**
	 * Obtiene el texto del cuadro de busqueda al pulsar ENVIAR
	 */
	public boolean onQueryTextSubmit(String query)
	{
		// TODO Apéndice de método generado automáticamente
		Toast.makeText(this, "Query = " + query + " : submitted", Toast.LENGTH_SHORT).show();
		return false;
	}

}
