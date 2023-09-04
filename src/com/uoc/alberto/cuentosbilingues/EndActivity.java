package com.uoc.alberto.cuentosbilingues;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uoc.alberto.cuentosbilingues.database.DAOStories;

/**
 * 
 * @author Alberto
 * @version 1 (15/04/2013)
 * Clase de tipo Activity que muestra el libro cerrado y los botones de "elegir otro cuento" y "Salir".
 */
public class EndActivity extends Activity implements View.OnClickListener, SoundPool.OnLoadCompleteListener{

	// Objeto para acceder a los datos.
	private DAOStories daoStories;

	// Variable de preferencias para recoger los idiomas de la configuraci�n del proyecto.
	private SharedPreferences prefs;

	// Variable para el tipo de letra "Edelfontmed" (tipo letra ligada de ni�os).
	private Typeface font;

	// Objetos del Layout, se asignar�n los valores correspondientes en tiempo de ejecuci�n.
	private TextView tvEnd;
	private Button bChooseStory;
	private Button bExit;

	// Variable para reproducir el efecto FX de cerrar el libro.
	private SoundPool closeBookFX;

	/**
	 * M�todo sobrecargado que se ejecuta al inicializar la Actividad.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.end_activity);

		// Inicializaci�n de las variables.
		initialize();
	}

	/**
	 * M�todo general que inicializa todas las variables declaradas al principio de la clase.
	 */
	private void initialize() {
		// Recojo el nombre de la imagen del cuento N cerrado, que env�a la actividad anterior (PageActivity)
		Bundle data = getIntent().getExtras();
		String backgroundBook = data.getString("backgroundBook");	

		String[] params;
		String end;
		String chooseStory;
		String exit;	

		daoStories = new DAOStories(this);

		// Guardo la imagen del cuento cerrado para obtener las dimensiones de la imagen.
		Bitmap closed_book = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(backgroundBook, "drawable", "com.uoc.alberto.cuentosbilingues"));

		// Obtengo los valores de las preferencias y creo el tipo de fuente.
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		font = Typeface.createFromAsset(getBaseContext().getAssets(), "Edelfontmed.ttf");

		// Componentes del Layout

		// TextView que contiene la imagen del cuento cerrado y el t�tulo fin en los idiomas definidos.
		tvEnd = (TextView) findViewById(R.id.tvEnd);
		tvEnd.setTypeface(font);
		// Asigno el valor de la imagen de fondo del libro cerrado y el tama�o del TextView.
		tvEnd.setBackgroundResource(getResources().getIdentifier(backgroundBook, "drawable", "com.uoc.alberto.cuentosbilingues"));
		tvEnd.setWidth(closed_book.getWidth());
		tvEnd.setHeight(closed_book.getHeight());

		// Button que contendr� la acci�n de "elegir otro cuento".
		bChooseStory = (Button) findViewById(R.id.bChooseStory);
		bChooseStory.setTypeface(font);
		bChooseStory.setOnClickListener(this);

		// Button que contendr� la acci�n de "salir".
		bExit = (Button) findViewById(R.id.bExit);
		bExit.setTypeface(font);
		bExit.setOnClickListener(this);

		// A partir de aqui obtengo los valores de los diferentes textos (Fin, elegir otro cuento y salir) en los dos idiomas definidos.
		params = new String[]{"End",prefs.getString("prefLang1","es"),prefs.getString("prefLang2","en")};

		daoStories.open();
		end = daoStories.getValue(params, "\n\n");

		params[0] = "ChooseAnotherStory";
		chooseStory = daoStories.getValue(params, " - ");

		params[0] = "Exit";
		exit = daoStories.getValue(params, " - ");

		daoStories.close();

		// Asigno el texto a cada �tem.
		tvEnd.setText(end);
		bChooseStory.setText(chooseStory);
		bExit.setText(exit);

		// Sonido FX de cerrar el libro.
		closeBookFX = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
		closeBookFX.load(this, R.raw.close_book,1);
		closeBookFX.setOnLoadCompleteListener((OnLoadCompleteListener)this);
	}

	/**
	 * M�todo sobrecargado que captura cuando se pulsa sobre el bot�n de "elegir otro cuento" o bien "salir". 
	 */
	@Override
	public void onClick(View v) {

		// S�lo si se ha pulsado uno de los dos botones
		if(v.getId() == R.id.bChooseStory || v.getId() == R.id.bExit){
			// Con este dato impide cerrar la aplicaci�n si se 
			// ha escogido la opci�n de "elegir otro cuento".
			boolean exit = false;

			// Si el bot�n pulsado ha sido el de "salir" se pone a true 
			// para que la StartActivity se encargue de cerrar la aplicaci�n.
			if(v.getId() == R.id.bExit)
				exit = true;

			// Finalmente pongo en primer plano la StartActivity y con los extras se cerrar� la aplicaci�n o no.
			back(exit);
		}
	}

	/**
	 * M�todo que carga la pantalla principal de la aplicaci�n o bien la cierra.
	 */
	private void back(boolean exitOrNot) {
		// Creo un nuevo intent hacia la StartActivity
		Intent i = new Intent(EndActivity.this,StartActivity.class);
		Bundle data = new Bundle();
		// Se pone a true o false para cerrar o no la aplicaci�n.
		data.putBoolean("exitOrNot", exitOrNot);
		// Con estas l�neas 'levanto' la primera activity a la parte frontal sin tener que instanciar una de nuevo.
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setAction("android.intent.action.MAIN");
		i.addCategory("android.intent.category.LAUNCHER");
		i.putExtras(data);
		startActivity(i);
		this.finish();
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
			Intent lp = new Intent("com.uoc.alberto.cuentosbilingues.LangPrefs");
			startActivity(lp);
			break;
		case R.id.action_about:
			Intent a = new Intent("com.uoc.alberto.cuentosbilingues.About");
			startActivity(a);
			break;
		case R.id.action_exit:
			back(true);
			break;
		}

		return true;
	}

	/**
	 * M�todo sobrecargado que reproduce el sonido de cerrarse el libro cuando �ste se carga completamente.
	 */
	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		if(status == 0)
			// Reporducir sonido FX de cerrar el libro.
			soundPool.play(sampleId, 1, 1, 0, 0, 1);		
	}

	/**
	 * M�todo sobrecargado que libera de la memoria el objeto de sonido FX y 
	 * el DAO de los cuentos para liberar memoria cuando la aplicaci�n se cierra.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeBookFX.release();
		if (daoStories != null) {
			daoStories.close();
		}
	}
}
