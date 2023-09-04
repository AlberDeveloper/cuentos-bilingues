package com.uoc.alberto.cuentosbilingues;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

/**
 * 
 * @author Alberto
 * @version 1 (18/04/2013)
 * 
 * Clase principal de configuraci�n. 
 * En la primera versi�n esta la selecci�n de los dos idiomas para visualizar los contenidos de los cuentos.
 */
public class LangPrefs extends PreferenceActivity{

	/**
	 * M�todo sobrecargado que se ejecuta al inicializar la Actividad.
	 * S�lo carga el layout de preferencias de idioma.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.langprefs);
	}

	/**
	 * M�todo sobrecargado que se ejecuta al cerrar la ventana de preferencias.
	 * Muestra el mensaje que debes reiniciar la aplicaci�n para que los cambios del idioma hagan efecto.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		String text =  getApplicationContext().getResources().getString(R.string.language);
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
		toast.show();
	}

	

}
