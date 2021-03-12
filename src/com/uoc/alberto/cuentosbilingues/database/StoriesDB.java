package com.uoc.alberto.cuentosbilingues.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.uoc.alberto.cuentosbilingues.MyAppContext;
/**
 * 
 * @author Alberto Pereira Gaviño
 * @version 1 (18/04/2013)
 * 
 * 
 * Con la clase StoriesDB lo que se crea es un conector de la aplicación a la base de datos sqlite de los cuentos.
 * Para realizar la conexión a la base de datos ésta debe existir en el directorio databases del sistema Android,
 * por lo tanto antes de realizar la conexión se comprueba la existencia de la base de datos y su versión, si no existe o bien
 * la original (en el directorio assets) tiene otra versión, la base de datos se actualizará.
 *
 */

public class StoriesDB extends SQLiteOpenHelper{

	// La dirección al directorio de las bases de datos del sistema.
	// Con la clase auxiliar "MyAppContext" obtengo el directorio del tipo "/data/data/com.uoc.alberto.cuentosbilingues/files"
	// con getParentFile subo un nivel en los directorios de sistema e incluyo el subdirectorio "/databases/".
	private final static String DB_PATH = MyAppContext.getAppContext().getFilesDir().getParentFile().getPath() + "/databases/";
	
	// El nombre del archivo de la base de datos que tengo en la carpeta assets (BBDD Local).
	private final static String DB_NAME = "bilingualstories.sqlite";	

	// Número de la versión actual de la base de datos.
	private final static int DB_VERSION = 1;

	// Número de la versión nueva de la base de datos.
	private final static int DB_NEW_VERSION = 1;

	//Contexto de la clase.
	/**
	 * @uml.property  name="myContext"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private final Context myContext;

	/**
	 * Constructor de la clase.
	 * @param context El contexto de la clase que lo crea.
	 */
	public StoriesDB(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Al estar la BD en un archivo (en el directorio "assets"), 
		// para crearla se llama al método "createDataBase()" desde el DAO (Data Access Object).
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		try {
    		// Copia la base de datos bilingualstories.sqlite en la nueva base de datos creada.
			copyDataBase();
			System.out.println("BD Actualizada!");
		} catch (IOException e) {
    		throw new Error("Error copiando la base de datos");
    	}
	}

	/**
     * Crea la base de datos comprobando previamente que no exista.
     * @throws IOException Excepción lanzada en caso de que haya algun error copiando la base de datos al sistema.
     */
    public void createDataBase() throws IOException{
 
    	// Comprueba si ya existe la base de datos
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		// Si existe la base de datos se compara la versión, si la versión (DB_NEW_VERSION)
    		// es mayor a la BD actual ésta se actualiza con el método onUpgrade. 
    		if(this.getReadableDatabase().getVersion() < DB_NEW_VERSION)
    			onUpgrade(this.getReadableDatabase(),DB_VERSION,DB_NEW_VERSION);
    	}else{
    		// Y si por el contrario no existe se llama a este método que crea una nueva BBDD en la ruta por defecto.
        	this.getReadableDatabase();
        	try {
        		// Copia la bilingualstories.sqlite en la nueva base de datos creada
    			copyDataBase();
    		} catch (IOException e) {
    			throw new Error("Error copiando la base de datos");
        	}
    	}
 
    }
 
    /**
     * Comprueba si ya existe la base de datos para evitar la reescritura de la misma cada vez que se abra la aplicación.
     * @return true si existe, false si no existe.
     */
    private boolean checkDataBase(){
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}catch(SQLiteException e){
    		// En este punto la BBDD no existe, por lo tanto el método devolverá false y la BBDD se creará. 
    	}
 
    	// Si la BD existía, se cierra.
    	if(checkDB != null) 
    		checkDB.close();
 
    	return checkDB != null ? true : false;
    }
 
 
	/**
	 * Copia la base de datos sqlite de la carpeta assets a la nueva base de datos.
	 * @throws IOException Excepción lanzada en caso de que haya algun error copiando la base de datos al sistema.
	 */
    private void copyDataBase() throws IOException{
 
    	// Abre la base de datos del fichero del directorio "assets".
    	InputStream inputDB = myContext.getAssets().open(DB_NAME);
 
    	// Directorio de la nueva base de datos.
    	String outDBname = DB_PATH + DB_NAME;
 
    	// Abre la nueva base de datos.
    	OutputStream outputDB = new FileOutputStream(outDBname);
 
    	// Transfiere bytes desde el archivo (inputDB) en el directorio assets a la nueva base de datos (outputDB).
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = inputDB.read(buffer))>0){
    		outputDB.write(buffer, 0, length);
    	}
 
    	// Se liberan los streams de la memoria.
    	outputDB.flush();
    	outputDB.close();
    	inputDB.close();
 
    }

}
