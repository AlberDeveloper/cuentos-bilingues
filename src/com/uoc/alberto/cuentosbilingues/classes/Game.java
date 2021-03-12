package com.uoc.alberto.cuentosbilingues.classes;

/**
 * @author Alberto
 * Clase que se crea para el final de cada cuento. 
 * Esta clase contiene el nombre de una imagen y el valor 
 * que corresponde al nombre de la imagen en el idioma secundario (el que se quiere aprender)
 * relacionado con personajes u objetos de cada cuento.
 */
public class Game {

	private String image, value;
	

	/**
	 * Constructor de la clase.
	 * @param imgGame
	 * @param value
	 */
	public Game(String imgGame, String value) {
		this.image = imgGame;
		this.value = value;
	}

	/**
	 * Devuelve el nombre de la imagen en el directorio "drawable".
	 * @return image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Devuelve el valor del nombre de la imagen en el idioma secundario.
	 * @return value
	 */
	public String getValue() {
		return value;
	}
}
