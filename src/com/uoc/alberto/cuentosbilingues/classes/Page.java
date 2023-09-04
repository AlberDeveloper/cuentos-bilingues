package com.uoc.alberto.cuentosbilingues.classes;

/**
 * @author Alberto
 * Clase que representa cada una de las p�ginas de un libro.
 * Esta clase esta compuesta por el n�mero de p�gina, la imagen y 
 * el texto en los idiomas escogidos por el usuario.
 */
public class Page {
	
	private int pageNo;
	private String image, firstLngText, secondLngText;

	/**
	 * Constructor de la clase.
	 * @param pageNo
	 * @param image
	 * @param firstLngText
	 * @param secondLngText
	 */
	public Page(int pageNo, String image, String firstLngText,
			String secondLngText) {
		this.pageNo = pageNo;
		this.image = image;
		this.firstLngText = firstLngText;
		this.secondLngText = secondLngText;
	}

	/**
	 * Devuelve el nombre de la imagen (del directorio "drawable") de la p�gina actual.
	 * @return image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Devuelve el texto de la p�gina actual en el idioma primario (nativo).
	 * @return firstLngText
	 */
	public String getFirstLngText() {
		return firstLngText;
	}

	/**
	 * Devuelve el texto de la p�gina actual en el idioma secundario (el que quiere aprender el usuario).
	 * @return secondLngText
	 */
	public String getSecondLngText() {
		return secondLngText;
	}

	/**
	 * Devuelve el n�mero de la p�gina actual.
	 * @return pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}
}
