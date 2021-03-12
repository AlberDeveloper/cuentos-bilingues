package com.uoc.alberto.cuentosbilingues;

import android.app.Activity;
import android.os.Bundle;

/**
 * 
 * @author Alberto
 * @version 1 (18/05/2013)
 * 
 * Clase que muestra el mensaje de "Acerca de" del proyecto. 
 * En el archivo AndroidManifest esta configurado con el estilo "Theme.Dialog" para que salga en modo "popup".
 */
public class About  extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}

}
