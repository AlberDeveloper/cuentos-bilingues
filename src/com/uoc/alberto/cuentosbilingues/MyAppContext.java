package com.uoc.alberto.cuentosbilingues;

import android.app.Application;
import android.content.Context;
/**
 * 
 * @author Alberto
 * @version 1 (16/04/2013)
 * Clase auxiliar para obtener el directorio hacia mi paquete dentro del sistema Android.
 * Se utiliza únicamente en la clase StoriesDB.
 */
public class MyAppContext extends Application{

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyAppContext.context = getApplicationContext();
    }

    /**
     * Método estático que devuelve el directorio del paquete en el sistema.
     * @return ruta del tipo "/data/data/nombreDelPaquete/files"
     */
    public static Context getAppContext() {
        return MyAppContext.context;
    }
}
