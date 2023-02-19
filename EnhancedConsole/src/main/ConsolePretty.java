package main;

/**
 * A simple pretty console utilities, like messages or prints. Pure
 * beautification purposes. <br>
 * Cannot be instantiated.
 * 
 * @author Patryk Bojar
 * @version 1.0.1 02 Dec 2021
 */
public final class ConsolePretty {
	/**
	 * Main private Constructor.
	 */
	private ConsolePretty() {

	}

	/**
	 * Writes a welcome message with the system information and time.
	 * 
	 * @return the String array composed of messages to be shown in the console.
	 */

	public static String[] showStartMessage() {

		return new String[] { "A.E.T.R.Y.K [Versión 1.0.5]", "\n(c) Cerberus Corp. Todos los derechos reservados.",
				"\n\nBienvenido, " + System.getProperty("user.name") + "." };
	}

	/**
	 * Shows all console commands and writes their information.
	 */
	public static void showHelpMessage() {
		System.out.println("");
		System.out.println("([parámetro]) Indica que el parámetro es opcional.\n");
		System.out.println(
				"mueve         [ruta/archivo] [ruta]            Mueve un archivo de un directorio a otro, eliminándolo del origen.");
		System.out.println("copia         [ruta/archivo] [ruta]            Copia un archivo a otra ubicación."
				+ " en la misma ruta.");
		System.out.println("elimina       [ruta/archivo] -                 Elimina un archivo.");
		System.out.println(
				"lista         ([ruta])       -                 Muestra una lista de archivos y subdirectorios en un directorio.");
		System.out.println(
				"listaArbol    ([ruta])       -                 Muestra de forma gráfica la estructura de carpetas de una unidad o ruta.");
		System.out.println(
				"muestraTXT    [ruta/archivo] -                 Lee y muestra el contenido de un archivo de texto con la extensión .txt.");
		System.out.println(
				"muestraXML    [ruta/archivo] [/modificador]    Lee y muestra el contenido de un archivo XML con la extensión .xml.");
		System.out.println(
				"comparaTXT    [ruta/archivo] [ruta/archivo]    Compara y muestra si dos ficheros de texto son iguales línea a línea.");
		System.out.println(
				"ayuda         -              -                 Proporciona información de ayuda para los comandos"
						+ " de I.A.C.C.");
		System.out.println("salir         -              -                 Termia la ejecución y cierra la consola.");
		System.out.println("");
	}
}
