package strings;

/**
 * The Strings class represents all character strings. A quick and easy
 * accessible library of String messages used in the project. <br>
 * Cannot be instantiated.
 * 
 * @author Patryk Bojar
 * @version 1.0.4, 23 Mar 2022
 *
 */

@SuppressWarnings("javadoc")
public final class Strings {

	/**
	 * Main private Constructor.
	 */
	private Strings() {
		// empty.
	}

	// START OF SYSTEM MESSAGES //
	public static final String FOLDER_PATH_LIST = "Listado de rutas de carpetas para la ruta:";
	public static final String ACCES_DENIED = "Acceso denegado";
	// END OF SYSTEM MESSAGES //

	// START OF MOVE FILE //
	public static final String FILE_MOVED = "\t1 archivo(s) movido(s).";
	public static final String FILE_NOT_MOVED = "\t0 archivo(s) movido(s).";
	// END OF MOVE FILE //

	// START COPY FILE //
	public static final String FILE_COPIED = "\t1 archivo(s) copiado(s).";
	public static final String FILE_NOT_COPIED = "\t0 archivo(s) copiado(s).";
	public static final String FILE_COPIED_SAME_PLC = "No se puede copiar el archivo sobre sí mismo.";
	// END OF COPY FILE //

	// START OF DELETE FILE //
	public static final String FILE_DELETED = "\tArchivo(s) eliminado(s).";
	// END OF DELETE FILE //

	// START OF XML and TXT FILE //
	public static final String XML_HANDLE_ONLY = "Este comando solo permite manipular archivos de tipo XML, con extensión .xml.";
	public static final String TXT_HANDLE_ONLY = "Este comando solo permite manipular archivos de texto, con extensión .txt.";
	public static final String FILES_ARE_EQUAL = "Los archivos son iguales";
	public static final String MISSING_PATH_OR_MODIFIER = "El comando espera una ruta o varios modificadores.\n"
			+ "Utilice \'ayuda\' para obtener más información.";

	public static final String WRONG_SYNTAX_MODIFIER = "no se reconoce como un modificador válido.";
	public static final String SHOW_XML_WITH_TAGS = "/conEtiquetas    Muestra el contenido con las etiquetas del archivo XML.";
	public static final String SHOW_XML_WITHOUT_TAGS = "/sinEtiquetas    Muestra el contenido sin las etiquetas del archivo XML.";
	// END OF XML and TXT FILE //

	// START OF PART2+ CONSOLE MESSAGES //
	public static final String PERMISSIONS_APPLIED = "Los permisos han sido aplicados.";

	// END OF PART2+ CONSOLE MESSAHES //

	//// START OF THROWS MESSAGES ////
	public static final String PATH_NOT_FOUND = "A.E.T.R.Y.K. no puede encontrar la ruta especificada.";
	public static final String FILE_NOT_FOUND = "A.E.T.R.Y.K. no puede encontrar el archivo especificado.";
	public static final String NAME_DIR_NOT_FOUND = "El nombre del directorio no es válido.";
	public static final String SOMETHING_WENT_WRONG = "Algo ha ido mal... Compruebe si el fichero existe.\n";
	public static final String FILE_NOT_ACCESIBLE = "No se ha podido mostrar el archivo. Compruebe si existe,\n"
			+ "o si A.E.T.R.Y.K. tiene permisos de lectura.";

	public static final String ONE_OF_FILES_NOT_FOUND = "No se ha podido encontrar uno de los archivos.";
	public static final String PATH_NOT_SEPARATED = "A.E.T.R.Y.K. cree que la ruta de origen o destino debe contener al menos una\n"
			+ "separación entre directorios y archivos.";

	public static final String ONE_FILE_HAS_NO_LINES = "Uno de los archivos está vacío y no contiene ningún caracter.\n"
			+ "Solo se permite comparar archivos iguales (con o sin contenido) o que contengan al menos una línea.";

	public static final String PATH_GENERAL_THROW = "El nombre de archivo, el nombre de directorio o la sintaxis de la etiqueta\n"
			+ "del volumen no son correctos.";

	public static final String INVALID_FILE_NAME = "Los nombres de archivo no pueden contener ninguno de los siguientes caracteres:\n"
			+ "\t\\ / : * ? < > | " + "\"";

	public static final String SYS_RESERVED_NAME_THROW = "Este nombre de archivo o carpeta está reservado por el sistema y no es válido.";
	public static final String CANT_CPY_MVE_DIR = "A.E.T.R.Y.K. no permite copiar ni mover carpetas.";
	public static final String CANT_SHOW_DIR_CONTENTS = "Este comando solo permite ver el contenido de un directorio.\n"
			+ "Ruta de acceso no válida.";

	public static final String ILLEGAL_ARGUMENT = "Uno de los argumentos es incorrecto.";
	public static final String WRONG_FORMAT_XML_FILE = "El formato, etiquetas, encapsulamiento o la sintaxis del documento XML es incorrecta.";
	//// END OF THROWS MESSAGES ////

	//// START OF CONSOLE COMMANDS ////
	public static final String WRONG_SYNTAX = "La sintaxis del comando no es correcta.";
	public static final String UNKNOWN_COMMAND = "no se reconoce como un comando interno o externo,\n"
			+ "programa o archivo por lotes ejecutable.\nSi cree que la orden ejecutada es correcta, limpie el búfer de entrada escribiendo nuevamente la orden.";
	public static final String SHOW_HELP = "Para obtener más información acerca de los comandos, escriba \'ayuda\'.";
	public static final String MISSING_DEST_PATH = "El comando espera una ruta de destino. Compruebe si ha proporcionado\n"
			+ "todos los parámetros.";
	//// END OF CONSOLE COMMANDS ////

}