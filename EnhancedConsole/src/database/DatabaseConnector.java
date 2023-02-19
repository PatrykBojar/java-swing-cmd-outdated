package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import main.Console;
import main.ConsoleApp;

/**
 * Creates a connection with the database
 * 
 * @author Patryk Bojar
 *
 */
public class DatabaseConnector {

	// Configuration variables
	private static String srvName = "localhost";
	private static int portNum = 3306;
	private static String userName = "root";
	private static String password = "";

	/**
	 * Basic Connector the get the desired database connection.
	 * 
	 * @return the connection with the database.
	 */
	public static Connection getConnection() {
		Connection conn = null;
		String dbName = Console.dbName; // sets the name of the database
		try {
			// Connection
			conn = DriverManager.getConnection("jdbc:mysql://" + srvName + ":" + portNum + "/" + dbName + "?user="
					+ userName + "&password=" + password);
		} catch (SQLException e) {
			Console.txtArea.append("\n\n" + "No se ha podido establecer la conexión con la base de datos.");
			Console.txtArea.append("\nOtros posibles errores:\n");
			Console.txtArea.append(e.getMessage() + "\n");
			Console.txtArea.append(
					"\nEL ERROR OCASIONADO ES GRAVE. Si el programa no funciona correctamente, se recomienda reiniciarlo.\nTodas las modificaciones en la base de datos se han guardado correctamente.");
			Console.txtArea.append("\nSi desea continuar con el programa, pulse INTRO");

		}
		return conn;
	}

}
