package com.uoc.alberto.cuentosbilingues;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.uoc.alberto.cuentosbilingues.classes.Story;
import com.uoc.alberto.cuentosbilingues.database.DAOStories;

/**
 * 
 * @author Alberto
 * @version 1 (21/04/2013)
 * 
 * Clase inicial del proyecto.
 * 
 * Contiene una clase privada llamada <b>HAdapter</b> para realizar la funci�n de scroll horizontal con el listado de cuentos.
 * 
 */
public class StartActivity extends Activity{
	// Lista de cuentos que se rellanar� en el evento onCreate.
	private static List<Story> stories;

	// Objeto del tipo HorizontalListView para mostrar las cubiertas de cada cuento en una lista horizontal arrastable.
	private static HorizontalListView listview;

	// Objeto para acceder a los datos.
	private DAOStories daoStories;

	// Variable de preferencias para recoger los idiomas de la configuraci�n del proyecto.
	private SharedPreferences prefs;

	// Variable para el tipo de letra "Edelfontmed" (tipo letra ligada de ni�os).
	private Typeface font;

	/**
	 * M�todo sobrecargado que se ejecuta al inicializar la aplicaci�n.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);
		
		// Inicializaci�n de las variables y del listview
		initialize();
	}

	/**
	 * M�todo sobrecargado que se ejecuta al volver en cualquier momento del flujo de la aplicaci�n.
	 * Se mantiene en segundo plano y �nicamente se cierra cuando se pulsa el bot�n salir de la clase "EndActivity", 
	 * ya que en esa Activity se introduce el dato "exitOrNot" a true.
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// Recojo los par�metros extras de la actividad que ha llamado a �sta.
		Bundle myData = intent.getExtras();
		boolean exit = myData.getBoolean("exitOrNot");	
		
		// Si exit es true cierro la aplicaci�n.
		if(exit)
			this.finish();
	}

	/**
	 * 
	 * @author Alberto
	 *
	 * Clase privada que permite crear un scroll horizontal con cualquier tipo de layout, 
	 * en este caso el listado de cuentos disponibles en la BD.
	 */
	private class HAdapter extends BaseAdapter implements View.OnClickListener{

		public HAdapter(){
			super();
		}

		public int getCount() {
			return stories.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewstory, null);

			// Obtengo el cuento de la lista stories en la posici�n "position".
			Story s = stories.get(position);
			// Recojo la imagen de la portada almacenada en la clase "story".
			Bitmap cover = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(s.getImgbook(), "drawable", "com.uoc.alberto.cuentosbilingues"));

			// Recojo el bot�n del layout.  
			Button button = (Button) retval.findViewById(R.id.btStoryCover);
			// Le asigno el fondo, el t�tulo del cuento y el tipo de letra (Typeface).
			button.setBackgroundResource(getResources().getIdentifier(s.getImgbook(), "drawable", "com.uoc.alberto.cuentosbilingues"));
			button.setText(s.getFirstLngName() + "\n" + s.getSecondLngName());
			button.setTypeface(font);

			// Defino el tama�o del bot�n seg�n la imagen recogida de los resources.
			button.setWidth(cover.getWidth());
			button.setHeight(cover.getHeight());
			
			// Le asigno el identificador del cuento en el tag.
			button.setTag(s.getId());
			
			// Finalmente le asigno el evento "OnClickListener".
			button.setOnClickListener(this);

			// Y finalmente devuelvo la vista (View).
			return retval;
		}

		/**
		 * M�todo de sobrecarga que captura el evento onClick de la clase HAdapter.
		 */
		@Override
		public void onClick(View v) {
			// Recojo el bot�n que se ha pulsado en la lista horizontal de cuentos.
			Button bt = (Button) v;
			// Recojo el identificador guardado en el tag.
			int storyId = Integer.valueOf(bt.getTag().toString());
			// Creo un nuevo Bundle para guardar el identificador del cuento y pasarselo a la actividad siguiente: PageActivity.
			Bundle data = new Bundle();
			data.putInt("storyId", storyId);
			// Creo un nuevo intent
			Intent a = new Intent(StartActivity.this,PageActivity.class);
			// Le asigno el bundle para recojerlo en la otra actividad.
			a.putExtras(data);
			// Y finalmente abro la actividad.
			startActivity(a);
			
		}
	};

	/**
	 * M�todo general que inicializa todas las variables declaradas al principio de la clase.
	 */
	private void initialize(){		
		daoStories = new DAOStories(this);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		font = Typeface.createFromAsset(getBaseContext().getAssets(), "Edelfontmed.ttf");
		
		daoStories.open();
		setStories();
		setLayoutTitle();
		daoStories.close();

		listview = (HorizontalListView) findViewById(R.id.listview);
		listview.setAdapter(new HAdapter());
	}

	/**
	 * M�todo que lee todos los cuentos almacenados en la BD y da valor a la lista de cuentos "stories".
	 */
	private void setStories(){

		String[] params = new String[]{null,prefs.getString("prefLang1","es"),
				prefs.getString("prefLang2","en")};

		stories = daoStories.getStories(params);
	}

	/**
	 * M�todo que muestra el t�tulo en la pantalla principal seg�n los idiomas definidos en las preferencias.
	 */
	private void setLayoutTitle(){

		String[] params = new String[]{"ChooseStory",prefs.getString("prefLang1","es"),prefs.getString("prefLang2","en")};
		String title = daoStories.getValue(params, " - ");

		TextView tv = (TextView) findViewById(R.id.tvTitle);
		tv.setText(title);
		tv.setTypeface(font);
	}


	/**
	 * M�todo sobrecargado que muestra las opciones del men�.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	/**
	 * M�todo sobrecargado que muestra la opci�n seleccionada.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_language:
			Intent i = new Intent("com.uoc.alberto.cuentosbilingues.LangPrefs");
			startActivity(i);
			break;
		case R.id.action_about:
			Intent a = new Intent("com.uoc.alberto.cuentosbilingues.About");
			startActivity(a);
			break;
		case R.id.action_exit:
			this.finish();
			break;
		}

		return false;
	}
	
	/**
	 * M�todo sobrecargado que libera de la memoria el DAO de los cuentos para liberar memoria cuando la aplicaci�n se cierra.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (daoStories != null) {
			daoStories.close();
		}
	}
}
