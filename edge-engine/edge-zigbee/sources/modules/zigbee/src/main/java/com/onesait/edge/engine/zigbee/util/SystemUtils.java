/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.onesait.edge.engine.zigbee.util;

/**
 * Clase con métodos estáticos que ofrece información del sistema
 * @author fgminambre - 3ehouses project - Indra
 * @since 2011
 */
public class SystemUtils {
	
	private SystemUtils() {
	    throw new IllegalStateException("Utility class");
	  }

    /**
     * Carácter que separa partes de rutas de archivo.
     * @return  "/" en UNIX y "\" en Windows.
     */
    public static String getFileSeparator(){
        return java.lang.System.getProperty("file.separator");
    }
    /**
     * Ruta utilizada para encontrar directorios y archivos JAR que contienen
     * archivos de clase java. Los elementos están separados en función del
     * carácter utilizado por la plaraforma, que podemos conocer en el método
     * getPathSeparator
     * @return Ruta ubicación directorios java
     */
    public static String getClassPath(){
        return java.lang.System.getProperty("java.class.path");
    }
    /**
     * Directorio ubicación Java Runtime Environment (JRE)
     * @return Directorio ubicación Java Runtime Environment (JRE)
     */
    public static String getJavaHome(){
        return java.lang.System.getProperty("java.home");
    }

    /**
     * Devuelve el nombre del distribuidor JRE
     * @return JRE empresa
     */
    public static String getVendor(){
        return java.lang.System.getProperty("java.vendor");
    }
    /**
     * Devuelve la url del distribuidor JRE
     * @return JRE url
     */
    public static String getVendorUrl(){
        return java.lang.System.getProperty("java.vendor.url");
    }

    /**
     * Devuelve el número de versión JRE
     * @return verión JRE
     */
    public static String getVersion(){
        return java.lang.System.getProperty("java.version");
    }
    /**
     * Secuencia utilizada por el sistema operativo en líneas separadas en archivos de texto (End Of Line EOL).<br>
     * Gracias a este carácter, podemos conocer el fin de línea de un archivo.
     * @return EOL
     */
    public static String getLineSeparator(){
        return java.lang.System.getProperty("line.separator");
    }

    /**
     * Arquitectura del sistema Operativo
     * @return Arquitectura del sistema Operativo
     */
    public static String getOsArch(){
        return java.lang.System.getProperty("os.arch");
    }

    /**
     * Nombre del sistema Operativo
     * @return Nombre del sistema Operativo
     */
    public static String getOsName(){
        return java.lang.System.getProperty("os.name");
    }
    
    /**
     * Nombre del JDK
     * @return Nombre del sistema Operativo
     */
    public static String getEnv(){
    	return java.lang.System.getenv("PROCESSOR_IDENTIFIER");
    }    
    /**
     * Arquitectura del sistema Operativo
     * @return Arquitectura del sistema Operativo
     */
    public static String getOsVersion(){
        return java.lang.System.getProperty("os.version");
    }

    /**
     * carácter separador de rutas uirlizado en las clases de java
     * @return carácter separador de rutas uirlizado en las clases de java
     */
    public static String getPathSeparator(){
        return java.lang.System.getProperty("path.separator");
    }
    /**
     * Directorio de trabajo del usuario
     * @return Directorio de trabajo del usuario
     */
    public static String getUserDir(){
        return java.lang.System.getProperty("user.dir");
    }
    /**
     * Directorio home de usuario.
     * @return Directorio home de usuario.
     */
    public static String getHomeDir(){
        return java.lang.System.getProperty("path.separator");
    }
    /**
     * Nombre de la cuenta de usuario
     * @return Nombre de la cuenta de usuario
     */
    public static String getAccountName(){
        return java.lang.System.getProperty("user.name");
    }

    /**
     * Muestra el contenido completo de las llamadas a todos los métodos de la clase
     * @return Resumen de los métodos del sistema
     */
    public static String getResumen() {
        String str="";
        str+="\nFileSeparator:"+getFileSeparator();
        str+="\nClassPath:"+getClassPath();
        str+="\nJavaHome:"+getJavaHome();
        str+="\nVendor:"+getVendor();
        str+="\nVendorUrl:"+getVendorUrl();
        str+="\nVersion:"+getVersion();
        str+="\nLineSeparator:"+getLineSeparator();
        str+="\nOsArch:"+getOsArch();
        str+="\nName:"+getOsName();
        str+="\nOsVersion:"+getOsVersion();
        str+="\nPathSeparator:"+getPathSeparator();
        str+="\nUserDir:"+getUserDir();
        str+="\nHomeDir:"+getHomeDir();
        str+="\nAccountName:"+getAccountName();
        return str;
    }
    /**
     * Lanza un programa
     * @param url
     * @return verdadero si la operación se ha ejecutado.
     */
    public static boolean exec(String url){
        try{
            Runtime.getRuntime().exec(url);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    /**
     * Lanza un programa bajo el internet explorer
     * @param url
     * @return verdadero si la operación se ha ejecutado.
     */
    public static boolean execIExplorer(String url){
        try{
            Runtime.getRuntime().exec("cmd /c start explorer "+url);
            return true;
        }catch(Exception e){
            return false;
        }
    }


}
