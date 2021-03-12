package com.uoc.alberto.cuentosbilingues.classes;

import java.util.ArrayList;

/**
 * 
 * @author Alberto
 * @version 1 (19/04/2013)
 * 
 * Clase principal de un cuento, contiene el nombre del cuento en el idioma principal y secundario, 
 * el nombre de la imagen de la portada y el número de páginas que contiene el cuento.
 *
 */
public class Story {
	
	private int _id;
	private String firstLngName, secondLngName, imgbook, bgPage, imgClosedBook;
	private ArrayList<Page> pages;

	

	/**
	 * Constructor de la clase.
	 * @param _id
	 * @param firstLngName
	 * @param secondLngName
	 * @param imgbook
	 * @param bgPage
	 * @param imgClosedBook
	 */
	public Story(int _id, String firstLngName, String secondLngName,
			String imgbook, String bgPage, String imgClosedBook) {
		this._id = _id;
		this.firstLngName = firstLngName;
		this.secondLngName = secondLngName;
		this.imgbook = imgbook;
		this.bgPage = bgPage;
		this.imgClosedBook = imgClosedBook;
		this.pages = null;
	}

	/**
	 * Devuelve el identificador del cuento.
	 * @return _id
	 */
	public int getId() {
		return _id;
	}

	/**
	 * Devuelve el número total de páginas que tiene el cuento.
	 * @return nopages
	 */
	public int getNopages() {
		int numOfPages = 0;
		if(!pages.isEmpty())
			numOfPages = pages.size();
		
		return numOfPages;
	}

	/**
	 * Devuelve el título del cuento en el idioma primario (nativo).
	 * @return firstLngName
	 */
	public String getFirstLngName() {
		return firstLngName;
	}

	/**
	 * Devuelve el título del cuento en el idioma secundario (el que quiere aprender el usuario).
	 * @return secondLngName
	 */
	public String getSecondLngName() {
		return secondLngName;
	}

	/**
	 * Devuelve el nombre de la imagen (del directorio "drawable") del cuento.
	 * @return imgbook
	 */
	public String getImgbook() {
		return imgbook;
	}

	/**
	 * Devuelve el nombre de la imagen del libro abierto (del directorio "drawable") del cuento.
	 * @return bgPage
	 */
	public String getBgPage() {
		return bgPage;
	}

	/**
	 * Devuelve el nombre de la imagen del libro cerrado (del directorio "drawable") del cuento.
	 * @return imgClosedBook
	 */
	public String getImgClosedBook() {
		return imgClosedBook;
	}

	/**
	 * Devuelve una lista con todas las páginas del cuento.
	 * @return pages
	 */
	public ArrayList<Page> getPages() {
		return pages;
	}

	/**
	 * Assigna la lista de las páginas con todas las páginas del cuento.
	 * @param ArrayList<Page> pages: ArrayList con las páginas del cuento.
	 */
	public void setPages(ArrayList<Page> pages) {
		this.pages = pages;
	}
	
	/**
	 * @param pageNo el índice de la página a obtener. 
	 * @return si existe, la página (objeto Page) en la posición <b>pageNo</b>, en caso contrario null.
	 */
	public Page getPageAt(int pageNo){
		Page page = null;
		
		if(!pages.isEmpty() && pageNo > 0){
			// Obtiene la página en la posición 'pageNo'-1 porque la lista se basa en 'index 0'.
			page = pages.get(pageNo-1);
		}
		
		return page;
	}
	
	
}