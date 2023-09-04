package com.uoc.alberto.cuentosbilingues;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.uoc.alberto.cuentosbilingues.classes.Game;
import com.uoc.alberto.cuentosbilingues.classes.Page;
import com.uoc.alberto.cuentosbilingues.classes.Story;
import com.uoc.alberto.cuentosbilingues.database.DAOStories;

/**
 * 
 * @author Alberto
 * @version 1 (24/04/2013)
 *
 * Clase de tipo Activity que muestra las diferentes p�ginas del cuento deslizando el dedo 
 * hacia la izquierda (avanzar p�gina) o bien 
 * hacia la derecha (retroceder p�gina).
 * 
 * Se puede hacer lo mismo pulsando sobre las flechas situadas en las esquinas inferiores.
 */
public class PageActivity extends Activity implements OnTouchListener, View.OnClickListener, DialogInterface.OnClickListener, 
SoundPool.OnLoadCompleteListener, OnPreparedListener{

	// Variable que indica la posici�n del dedo en la pantalla, se utiliza para "pasar de p�gina".
	private float downXValue;
	
	// Variable de tipo Page, que se obtendr� cada p�gina del objeto Story. 
	private Page page;

	// Objeto para acceder a los datos.
	private DAOStories daoStories;

	// Objeto de tipo story (cuento) para el listado inicial de cuentos.
	private Story story;

	// Variable de preferencias para recoger los idiomas de la configuraci�n del proyecto.
	private SharedPreferences prefs;

	// Variable para el tipo de letra "Edelfontmed" (tipo letra ligada de ni�os).
	private Typeface font;

	// Objetos del Layout, se asignar�n los valores correspondientes en tiempo de ejecuci�n.
	private LinearLayout layMain;
	private ViewFlipper vf;
	private LinearLayout background;

	private TextView title1;
	private TextView title2;

	private TextView text1;
	private TextView text2;

	private ImageView pageImage;

	private Button bLeft;
	private Button bRight;

	// TextViews para la ultima p�gina (juego interactivo)
	private TextView gImage1;
	private TextView gImage2;
	private TextView gImage3;
	private TextView gImage4;
	private TextView gImage5;
	private TextView gImage6;

	// Lista con cada imagen del juego
	private ArrayList<Game> games;

	// Las siguientes variables se utilitzan en los m�todos "onClick(View v)" y "onClick(DialogInterface dialog, int which)".

	// Variable de tipo auxiliar que se utiliza para la interacci�n con el juego.
	private Game auxGame;
	// Variable que guardar� una �nica vez las diferentes opciones para cada imagen del juego.
	private CharSequence[] gameOptions;
	// Variable del tipo TextView que guardar� el objeto "TextView" escogido (variables gImageN).
	private TextView auxTextView;

	// Variable para reproducir la m�sica de fondo y efecto FX de pasar la p�gina.
	private MediaPlayer bgMusic;
	private SoundPool turnPageFX;
	private int _turnPage;
	private int turnPageFXLoaded = 0;

	// Variable que indica la p�gina actual. 
	// Seg�n se avance o retroceda por las p�ginas esta variable aumentar� su valor o bien disminuir�.
	private int currPage = 1;

	/**
	 * M�todo sobrecargado que se ejecuta al inicializar la Actividad.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pageviewer);

		// Inicializaci�n de variables y componentes del LinearLayout
		initialize();

		// Al principio siempre se carga la primera p�gina.
		loadPage(currPage);
	}

	/**
	 * M�todo general que inicializa todas las variables declaradas al principio de la clase.
	 */
	private void initialize() {
		int storyId;
		int bg;
		ArrayList<Page> pages = new ArrayList<Page>();

		games = new ArrayList<Game>();
		vf = (ViewFlipper) findViewById(R.id.vfBookPage);

		Bundle data = getIntent().getExtras();
		storyId = data.getInt("storyId");

		daoStories = new DAOStories(this);

		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		font = Typeface.createFromAsset(getBaseContext().getAssets(), "Edelfontmed.ttf");

		// Imagen para cada p�gina.
		pageImage = (ImageView) findViewById(R.id.ivPageImage);

		// Controles de la pantalla

		// Flecha izquierda
		bLeft = (Button) findViewById(R.id.bLeft);
		bLeft.setOnClickListener(this);

		// Flecha derecha
		bRight = (Button) findViewById(R.id.bRight);
		bRight.setOnClickListener(this);

		// TextViews de las im�genes del juego interactivo final.
		gImage1 = (TextView) findViewById(R.id.tvGameImage1);
		gImage1.setTypeface(font);
		gImage1.setOnClickListener(this);

		gImage2 = (TextView) findViewById(R.id.tvGameImage2);
		gImage2.setTypeface(font);
		gImage2.setOnClickListener(this);

		gImage3 = (TextView) findViewById(R.id.tvGameImage3);
		gImage3.setTypeface(font);
		gImage3.setOnClickListener(this);	

		gImage4 = (TextView) findViewById(R.id.tvGameImage4);
		gImage4.setTypeface(font);
		gImage4.setOnClickListener(this);

		gImage5 = (TextView) findViewById(R.id.tvGameImage5);
		gImage5.setTypeface(font);
		gImage5.setOnClickListener(this);

		gImage6 = (TextView) findViewById(R.id.tvGameImage6);
		gImage6.setTypeface(font);
		gImage6.setOnClickListener(this);

		// Layout principal que captura el evento del deslizamiento del dedo para avanzar o retroceder la p�gina.
		layMain = (LinearLayout) findViewById(R.id.pageviewerLayout);
		layMain.setOnTouchListener((OnTouchListener) this);

		// Imagen de fondo (libro abierto).
		background = (LinearLayout) findViewById(R.id.backgroundLayout);

		// TextViews de los t�tulos y los textos de cada p�gina del cuento
		title1 = (TextView) findViewById(R.id.tvTitle1);
		title1.setTypeface(font,Typeface.BOLD_ITALIC);

		title2 = (TextView) findViewById(R.id.tvTitle2);
		title2.setTypeface(font,Typeface.BOLD_ITALIC);

		text1 = (TextView) findViewById(R.id.tvTextLang1);
		text1.setTypeface(font);

		text2 = (TextView) findViewById(R.id.tvTextLang2);
		text2.setTypeface(font);


		// Cargar las p�ginas y el juego final desde la base de datos con el DAO daoStories.
		String[] params = new String[]{String.valueOf(storyId),prefs.getString("prefLang1","es"),
				prefs.getString("prefLang2","en")};

		daoStories.open();

		// Cargo los datos del cuento.
		story = daoStories.getStory(params);

		// Cargo las p�ginas del cuento y las asigno al cuento.
		pages = (ArrayList<Page>) daoStories.getPages(params);
		story.setPages(pages);

		// Para cargar las im�genes del juego y sus valores solo necesito un idioma.
		// Por lo tanto lo cargo con el identificador del cuento y el idioma a aprender.
		params = new String[]{String.valueOf(storyId),prefs.getString("prefLang2","en")};
		games = (ArrayList<Game>) daoStories.getGames(params);

		// Asigno las opciones obtenidas.
		gameOptions = daoStories.getGameValues(params);

		daoStories.close();

		// Una vez tengo los datos del cuento cargados obtengo el identificador de la imagen de fondo (el libro abierto).
		bg = getBaseContext().getResources().getIdentifier(story.getBgPage(), "drawable", "com.uoc.alberto.cuentosbilingues");
		// Y le asigno la imagen de fondo.
		background.setBackgroundResource(bg);

		// Sonido FX de pasar la p�gina.
		turnPageFX = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
		_turnPage = turnPageFX.load(this, R.raw.turn_page,1);
		// Cuando se cargue el sonido FX se reproducir� por primera vez.
		turnPageFX.setOnLoadCompleteListener((OnLoadCompleteListener)this);

		// Se crea el objeto de tipo MediaPlayer con la m�sica de fondo.
		bgMusic = MediaPlayer.create(PageActivity.this, R.raw.bg_music);
		// setLooping = true para que se reproduzca de nuevo la m�sica al acabar.
		bgMusic.setLooping(true);
		// Cuando se cargue la m�sica empezar� a sonar.
		bgMusic.setOnPreparedListener((OnPreparedListener)this);
	}

	/**
	 * M�todo que carga la p�gina N del cuento seleccionado previamente.
	 * @param pageNo el n�mero de p�gina a visualizar.
	 * @return true si existe la p�gina, false en caso contrario.
	 */
	private boolean loadPage(int pageNo){
		int _pageImage;
		boolean nextOrPrev = false;

		if( pageNo == story.getNopages() + 1){
			// La p�gina siguiente a la �ltima del cuento ser� el juego interactivo.
			// Cargo las variables del juego.
			loadEndGame();
			nextOrPrev = true;
		}else if (pageNo == story.getNopages() + 2){ 
			// Y la siguiente p�gina al juego ser� la imagen del libro cerrado con las opciones de "salir" o "leer otro cuento".
			Bundle data = new Bundle();
			data.putString("backgroundBook", story.getImgClosedBook());

			Intent i = new Intent(PageActivity.this,EndActivity.class);
			i.putExtras(data);
			startActivity(i);
			this.finish();
		}else{
			// S�lo si la p�gina seleccionada es la ultima limpio los componentes del juego interactivo.
			if( pageNo == story.getNopages())
				cleanEndGame();

			// Cargo la p�gina en la posici�n "pageNo".
			page = story.getPageAt(pageNo);

			// Si no ha devuelto null cargo la imagen de la p�gina, el t�tulo y los textos en los idiomas.
			if (page != null){

				_pageImage =  getBaseContext().getResources().getIdentifier(page.getImage(), "drawable", "com.uoc.alberto.cuentosbilingues");

				pageImage.setImageResource(_pageImage);

				title1.setText(story.getFirstLngName());
				title2.setText(story.getSecondLngName());

				text1.setText(Html.fromHtml(page.getFirstLngText()));
				text2.setText(Html.fromHtml(page.getSecondLngText()));
				
				// Si el sonido FX se ha cargado lo reproduzco al pasar la p�gina.
				if(turnPageFXLoaded == 1)
					// Reporducir sonido FX de pasar la p�gina.
					turnPageFX.play(_turnPage, 1, 1, 0, 0, 1);

				nextOrPrev = true;
			}else
				back(false); // Si devuelve null (normalmente al ir hacia atr�s en la primera p�gina) cargo la pantalla inicial (StartActivity).
		}

		return nextOrPrev;
	}

	/**
	 * M�todo que carga la pantalla principal de la aplicaci�n o bien la cierra.
	 */
	private void back(boolean exitOrNot) {
		Intent i = new Intent(PageActivity.this,StartActivity.class);
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
	 * M�todo que carga las imagenes en cada TextView del juego final interactivo.
	 */
	private void loadEndGame() {
		// Limpio la imagen de la p�gina y los textos, el t�tulo del cuento se mantiene.
		pageImage.setImageBitmap(null);
		text1.setText("");
		text2.setText("");

		// Reporducir sonido FX de pasar la p�gina.
		turnPageFX.play(_turnPage, 1, 1, 0, 0, 1);

		Game game = null;
		int idImage;
		int count = 0;
		Iterator<Game> i = games.iterator();

		TextView[] gImages = {gImage1, gImage2, gImage3, gImage4, gImage5, gImage6};
		TextView tvAux = null;

		// A cada TextView le asigno una imagen y como tag le asigno un objeto del tipo Game 
		// para recuperarlo a la hora de realizar la comprobaci�n en la opci�n seleccionada.
		while(i.hasNext()){
			game = (Game) i.next();

			idImage = getBaseContext().getResources().getIdentifier(game.getImage(), "drawable", "com.uoc.alberto.cuentosbilingues");

			tvAux = (TextView)gImages[count];
			tvAux.setCompoundDrawablesWithIntrinsicBounds(0, idImage, 0, 0);
			tvAux.setTag(game);

			count++;
		}
	}

	/**
	 * M�todo que limpia los TextView en la p�gina del juego interactivo.
	 */
	private void cleanEndGame(){
		gImage1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		gImage2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		gImage3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		gImage4.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		gImage5.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		gImage6.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

		gImage1.setText("");
		gImage2.setText("");
		gImage3.setText("");
		gImage4.setText("");
		gImage5.setText("");
		gImage6.setText("");
	}

	/**
	 * M�todo para avanzar de p�gina.
	 */
	private void nextPage(){
		// Se incrementa el contador de la p�gina actual.
		currPage++;

		// Se carga la p�gina en el �ndice 'currPage'.
		if(loadPage(currPage)){
			// Asigno la animaci�n tipo "slide_left".
			vf.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_left));
			// Y muestro la siguiente p�gina.
			vf.showNext();			
		}
	}

	/**
	 * M�todo para retroceder de p�gina.
	 */
	private void prevPage(){
		// Se decrementa el contador de la p�gina actual.
		currPage--;

		// Se carga la p�gina en el �ndice 'currPage'.
		if(loadPage(currPage)){
			// Asigno la animaci�n tipo "slide_right".
			vf.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_right));
			// Y muestro la p�gina anterior.
			vf.showPrevious();
		}
	}

	/**
	 * M�todo sobrecargado que captura el evento 'onTouch'. 
	 * Se usa para pasar de p�gina hacia adelante o bien hacia atr�s dependendiendo de la posici�n X del dedo.
	 */
	@Override
	public boolean onTouch(View v, MotionEvent me) {
		// Obtengo la acci�n realizada en el evento "onTouch".
		switch (me.getAction()){

		// Si s�lo ha presionando la pantalla.
		case MotionEvent.ACTION_DOWN:{
			// Guardo el valor de X cuando el usuario presiona la pantalla.
			downXValue = me.getX();
			break;
		}

		case MotionEvent.ACTION_UP:{
			// Obtengo el valor de X cuando el usuario ya ha soltado el dedo de la pantalla.
			float currentX = me.getX();

			// Voy hacia atr�s: El usuario a movido el dedo hacia la derecha.
			if (downXValue < currentX)
				prevPage();

			// Voy hacia adelante: El usuario a movido el dedo hacia la izquierda.
			if (downXValue > currentX)
				nextPage();

			break;
		}
		}

		return true;
	}

	/**
	 * M�todo sobrecargado que captura cuando se pulsa sobre una de las flechas en las esquinas inferiores, 
	 * o bien cualquier imagen (TextView) del juego interactivo.
	 */
	@Override
	public void onClick(View v) {

		// Flecha izquierda retrocede una p�gina.
		if(v.getId() == R.id.bLeft)
			prevPage();

		// Flecha derecha avanza una p�gina.
		if(v.getId() == R.id.bRight)
			nextPage();

		// Si es cualquier imagen (TextView) del juego interactivo.
		if(v.getId() == R.id.tvGameImage1 || v.getId() == R.id.tvGameImage2 || v.getId() == R.id.tvGameImage3 || 
				v.getId() == R.id.tvGameImage4 || v.getId() == R.id.tvGameImage5 || v.getId() == R.id.tvGameImage6){

			// Guardo el TextView en una auxiliar.
			auxTextView = (TextView) v;
			// Guardo el game obtenido del tag del TextView anterior.
			auxGame = (Game) auxTextView.getTag();

			// Creo y muestro el menu de opciones.
			Builder builder = new Builder(this);
			builder.setTitle(getBaseContext().getText(R.string.titleGameOptions));
			builder.setSingleChoiceItems(gameOptions, -1, this);
			AlertDialog alert = builder.create();
			alert.show();
		}

	}

	/**
	 * M�todo sobrecargado que captura el evento cuando se selecciona alguna opci�n 
	 * en el men� de opciones de cada imagen del juego interactivo.
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		//HACER UN CONTADOR: SI HACE 3 BIEN PONER: 'SIGUE ASI'
		// CUANDO TODAS OK PONER: 'MUY BIEN!!'

		// id de la imagen 'incorrecto'.
		int idImage = R.drawable.wrong;

		// Guardo la posici�n de las im�genes.
		Drawable[] draw;
		draw = auxTextView.getCompoundDrawables();

		// Si la opci�n es la correcta guardo el id de la imagen 'correcto'.
		if(auxGame.getValue().contains(gameOptions[which]))
			idImage = R.drawable.correct;

		// Imagen del lado izquierdo (tick o cross)
		draw[0] = getBaseContext().getResources().getDrawable(idImage);
		auxTextView.setCompoundDrawablesWithIntrinsicBounds(draw[0], draw[1], draw[2], draw[3]);

		// Muestro debajo de la imagen la opci�n seleccionada.
		auxTextView.setText(" " + gameOptions[which]);

		// Finalmente cierro el di�logo de opciones.
		dialog.cancel();
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

		return false;
	}

	/**
	 * M�todo sobrecargado que reproduce la m�sica de fondo cuando �sta se carga completamente en memoria.
	 */
	@Override
	public void onPrepared(MediaPlayer music) {
		// Reproduce la musica de fondo.
		music.start();
	}

	/**
	 * M�todo sobrecargado que reproduce el sonido "turn_page" al cargar la primera p�gina cuando �ste se carga completamente.
	 */
	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		if(status == 0){
			// Reporducir sonido FX de pasar p�gina.
			soundPool.play(sampleId, 1, 1, 0, 0, 1);
			// Sonido FX cargado.
			turnPageFXLoaded = 1;
		}		
	}

	/**
	 * M�todo sobrecargado que libera de la memoria los objetos de sonido (fondo y fx) y 
	 * el DAO de los cuentos para liberar memoria cuando la aplicaci�n se cierra.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		bgMusic.release();
		turnPageFX.release();
		if (daoStories != null) {
			daoStories.close();
		}
	}
}
