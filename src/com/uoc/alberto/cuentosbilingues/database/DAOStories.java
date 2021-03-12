package com.uoc.alberto.cuentosbilingues.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uoc.alberto.cuentosbilingues.classes.Game;
import com.uoc.alberto.cuentosbilingues.classes.Page;
import com.uoc.alberto.cuentosbilingues.classes.Story;
/**
 * 
 * @author Alberto Pereira Gaviño
 * @version 1 (18/04/2013)
 * 
 * 
 * Con la clase DAOStories lo que se crea es un DAO (Data Access Object) a la base de datos sqlite de los cuentos.
 * Esta clase se encarga de crear una conexión hacia la base de datos.
 * Con los métodos open() y close() lo que se hace es abrir y/o cerrar la conexión con la base de datos. 
 * El resto de métodos son para obtener los datos de la BBDD.
 */

public class DAOStories {

	/**
	 * @uml.property  name="dbHelper"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private StoriesDB dbHelper;
	/**
	 * @uml.property  name="database"
	 * @uml.associationEnd  
	 */
	private SQLiteDatabase database;

	/**
	 * Constructor de la clase.
	 * @param context es el contexto de la clase que lo crea.
	 */
	public DAOStories(Context context){
		dbHelper = new StoriesDB(context);

		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Abre la comunicación con la BBDD.
	 * @return objeto del tipo DAOStories.
	 */
	public DAOStories open(){
		database = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Cierra la comunicación con la BBDD.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * @param params Array de parámetros para realizar el where de la consulta [idStory, prefLanguage1, prefLanguage2]
	 * @return Story story: Objeto de la case Story, null si no existe.
	 */
	public Story getStory(String[] params) {
		Story story = null;

		String query =	"SELECT s._id, sl1.title as firstLngName, sl2.title as secondLngName, s.imgbook, s.bgPage, s.imgClosedBook " + 
				"FROM story s " +
				"inner join storyLanguage sl1 on s._id = sl1.idStory " +
				"inner join storyLanguage sl2 on s._id = sl2.idStory " +
				"inner join language l1 on l1._id = sl1.idLanguage " +
				"inner join language l2 on l2._id = sl2.idLanguage " +
				"where s._id = ? and l1.isocode = ? and l2.isocode = ? ";

		Cursor cursor = database.rawQuery(query, params);

		if (cursor.moveToFirst()) {
			story = new Story(
					cursor.getInt(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getString(4),
					cursor.getString(5)
					);
		}

		cursor.close();

		return story;
	}

	/**
	 * 
	 * @param params Array de parámetros para realizar el where de la consulta en el métdo getStory [idStory (null), prefLanguage1, prefLanguage2]
	 * Se hace una consulta a todos los cuentos disponibles en la BBDD, se guarda en params[0] el id del cuento leído en la BBDD y 
	 * luego para cada uno de los registros se llama al método getStory con los mismos params y añadir el cuento a la ArrayList.
	 * Es decir, en este caso idStory viene vacío y se asigna el valor dentro del método. 
	 * Para cada cuento de la base de datos crea el objeto de la clase Story y lo añade en la lista.
	 * 
	 * @return List<Story> stories: Lista de objetos de la case Story.
	 */
	public List<Story> getStories(String[] params) {
		ArrayList<Story> stories = new ArrayList<Story>();

		String query =	"SELECT _id FROM story";

		Cursor cursor = database.rawQuery(query, null);

		if (cursor.moveToFirst()){
			do {
				params[0]=cursor.getString(0);
				stories.add(getStory(params));
			} while(cursor.moveToNext());

		}
		
		cursor.close();

		return stories;
	}

	/**
	 * 
	 * @param params Array de parámetros para realizar el where de la consulta [keyString, prefLanguage1, prefLanguage2]
	 * @param separador String que hace de separador entre los dos idiomas definidos, 
	 * que puede ser un guión (-), un salto de línia (\n) o cualquier otro tipo de carácter.
	 * 
	 * @return String result: El valor del String en los dos idiomas definidos por el usuario con el símbolo <b>separador</b> entre los dos idiomas.
	 */
	public String getValue(String[] params, String separador){
		String result = "";

		String query =	"select vL1.value as firstLngVal, vL2.value as secondLngVal from value v " + 
				"inner join valueLanguage vL1 on v.key = vL1.key " + 
				"inner join valueLanguage vL2 on v.key = vL2.key " + 
				"inner join language L1 on L1._id = vL1.idLanguage " + 
				"inner join language L2 on L2._id = vL2.idLanguage " +  
				"where v.key = ? and L1.isocode = ? and L2.isocode = ? ";		

		Cursor cursor = database.rawQuery(query, params);

		if (cursor.moveToFirst()) {
			result = cursor.getString(0) + separador + cursor.getString(1);
		}
		cursor.close();

		return result;
	}

	/**
	 * 
	 * @param params Array de parámetros para realizar el where de la consulta [idStory, prefLanguage1, prefLanguage2]
	 * @return List<Page> pages: un ArrayList con las páginas del cuento (objetos del tipo Page) identificadas por idStory y
	 * ordenadas por el número de página almacenado en la base de datos.
	 */
	public List<Page> getPages(String[] params) {
		ArrayList<Page> pages = new ArrayList<Page>();

		String query =	"select p.pageNo, p.imgPage, pL1.text as firstLngText, pL2.text as secondLngText " +
				"from page p " +
				"inner join pageLanguage pL1 on p.idStory = pL1.idStory and p.pageNo = pL1.pageNo " + 
				"inner join pageLanguage pL2 on p.idStory = pL2.idStory and  p.pageNo = pL2.pageNo " +
				"inner join language L1 on L1._id = pL1.idLanguage " +
				"inner join language L2 on L2._id = pL2.idLanguage " +
				"where p.idStory = ? and L1.isocode = ? and L2.isocode = ? " +
				"order by p.pageNo";

		Cursor cursor = database.rawQuery(query, params);

		if (cursor.moveToFirst()){
			do {

				pages.add(
						new Page(cursor.getInt(0),
								cursor.getString(1),
								cursor.getString(2),
								cursor.getString(3))
						);

			} while(cursor.moveToNext());

		}

		cursor.close();

		return pages;
	}

	/**
	 * Para este método la ordenación es aleatoria, para que cada vez que se abra un cuento y se llegue a la página del juego interactivo (al final de cada cuento)
	 * el orden siempre sea diferente.
	 * @param params Array de parámetros para realizar el where de la consulta [idStory, prefLanguage2 (Idioma a aprender)]
	 * @return List<Game> games: un ArrayList con los juegos del cuento (objetos del tipo Game) identificadas por idStory y el idioma que quiere aprender el usuario.
	 */
	public List<Game> getGames(String[] params){
		ArrayList<Game> games = new ArrayList<Game>();

		String query =	"select imgGame, value " +
				"from game g " +
				"inner join gameLanguage gl on g._id = gl.idGame " + 
				"inner join language L on gl.idLanguage = L._id " +
				"where g.idStory = ? and L.isocode = ? " +
				"order by random()";

		Cursor cursor = database.rawQuery(query, params);

		if (cursor.moveToFirst()){
			do {
				games.add( new Game(cursor.getString(0),
						cursor.getString(1)));	
			}while(cursor.moveToNext());
		}

		cursor.close();

		return games;
	}

	/**
	 * Para este método la ordenación es aleatoria, de manera que cada vez que se abra un cuento y se llegue a la página del juego interactivo (al final de cada cuento)
	 * el orden de las posibles opciones siempre sea diferente.
	 * @param params Array de parámetros para realizar el where de la consulta [idStory, prefLanguage2 (Idioma a aprender)]
	 * @return CharSequence[] values: Matriz con los valores para las opciones a elegir en el juego interactivo (al final de cada cuento).
	 */
	public CharSequence[] getGameValues(String[] params){
		ArrayList<String> listOfValues = new ArrayList<String>();
		CharSequence[] values;

		String query =	"select value " +
				"from game g " +
				"inner join gameLanguage gl on g._id = gl.idGame " + 
				"inner join language L on gl.idLanguage = L._id " +
				"where g.idStory = ? and L.isocode = ? " +
				"order by random()";

		Cursor cursor = database.rawQuery(query, params);

		if (cursor.moveToFirst()){
			do {
				listOfValues.add(cursor.getString(0));
			}while(cursor.moveToNext());
		}

		cursor.close();

		values = listOfValues.toArray(new CharSequence[listOfValues.size()]);

		return values;
	}
}
