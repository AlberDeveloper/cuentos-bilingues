package com.uoc.alberto.cuentosbilingues;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

/**
 * 
 * @author Alberto
 * @version 1 (18/04/2013)
 * 
 * Clase principal de configuración. 
 * En la primera versión esta la selección de los dos idiomas para visualizar los contenidos de los cuentos.
 */
public class LangPrefs extends PreferenceActivity{

	/**
	 * Método sobrecargado que se ejecuta al inicializar la Actividad.
	 * Sólo carga el layout de preferencias de idioma.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.langprefs);
	}

	/**
	 * Método sobrecargado que se ejecuta al cerrar la ventana de preferencias.
	 * Muestra el mensaje que debes reiniciar la aplicación para que los cambios del idioma hagan efecto.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		String text =  getApplicationContext().getResources().getString(R.string.language);
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
		toast.show();
	}

	

}
