package sax;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import database.DatabaseConnector;
import main.Console;

/**
 * Handles the XML file, reading and displaying it.
 * 
 * @author Patryk Bojar
 * @version 1.0.1, 23 Mar 2022
 */
public class SAXHandler extends DefaultHandler {
	public int actualLevel = -1; // the actual level of the xml file.
	public static int maxLevel = 0; // the maximum level of the xml file.

	String xmlDBname = "";
	String xmlDBtableName = "";
	String xmlDBpkName = "";
	String xmlDBpkValue = "";
	String xmlDBpk2Value = "";
	String xmlDBcolName = "";

	boolean read = true;
	StringBuilder strBld = new StringBuilder();

	Connection conn;
	Statement st;

	/**
	 * Main Constructor with read boolean variable, false by default.
	 */
	public SAXHandler() {
		read = false;
		if (Console.createDB) {
			conn = DatabaseConnector.getConnection();
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

		if (Console.isShowXMLTags()) {
			strBld.append(qName);
			strBld.append(": ");
			read = true;
		}
		if (atts.getQName(0) != null && atts.getValue(atts.getQName(0)) != null) {
			strBld.append(atts.getQName(0));
			strBld.append("=");
			strBld.append(atts.getValue(atts.getQName(0)));
			read = true;
		}

		// If the actual level of the tag is bigger than the maximum level, then the
		// maximum level equals the actual.
		// For each endElement, the actual level decreases. The maximum level stays
		// always the same.
		// This way, we can know the exact level.
		actualLevel++;
		if (actualLevel > maxLevel) {
			maxLevel = actualLevel; // 0: db, 1: table, 2: field.
		}

		if (Console.createDB) {
			// Stores all data into variables.
			try {

				// Creates and selects the database.
				if (maxLevel == 0) {
					st = conn.createStatement();

					xmlDBname = qName;

					st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + xmlDBname + ";");
					Console.txtArea.append("\n\n-->Esquema de base de datos creado.");

					conn = DriverManager
							.getConnection("jdbc:mysql://localhost:3306/" + xmlDBname + "?user=root&password=root");

					st.executeUpdate("USE " + xmlDBname + ";");
					Console.txtArea.append("\n-->Base de datos seleccionada.");

					st.close();
				}

				// Creates the tables and inserts ONLY the ID values.
				if (maxLevel == 1) {
					st = conn.createStatement();

					xmlDBtableName = qName;
					xmlDBpkName = atts.getQName(0); // primary key name.
					xmlDBpkValue = atts.getValue(0); // primary key value.
					st.executeUpdate(
							"CREATE TABLE IF NOT EXISTS " + xmlDBtableName + "(" + xmlDBpkName + " int PRIMARY KEY);");
					Console.txtArea.append("\n-->Tabla [" + xmlDBtableName + "] con la columna PK creada.");

					st.executeUpdate(
							"INSERT INTO " + xmlDBtableName + "(" + xmlDBpkName + ") VALUES ('" + xmlDBpkValue + "');");
					Console.txtArea.append("\n-->Nueva fila PK de nivel 1 insertada.");
					st.close();
				}

				if (maxLevel == 2) {
					st = conn.createStatement();
					// Adds only a new name if it's different from the previous one and it's
					// different from the database name and table name.
					if (!xmlDBcolName.contains(qName) && !qName.equals(xmlDBname) && !qName.equals(xmlDBtableName)) {
						// Adds only a tag if its length is 45 or less.
						if (qName.length() <= 45) {

							xmlDBcolName = qName;

							st.executeUpdate(
									"ALTER TABLE " + xmlDBtableName + " ADD " + xmlDBcolName + " varchar(45);");
							Console.txtArea.append("\n-->Añadida nueva columna [" + xmlDBcolName + "] a la tabla.");
						}
					}
					// Gets and inserts the data for level 2.
					xmlDBpk2Value = atts.getValue(0);
					st.executeUpdate("INSERT INTO " + xmlDBtableName + "(" + xmlDBpkName + ") VALUES ('" + xmlDBpk2Value
							+ "');");
					Console.txtArea.append("\n-->Nueva PK fila de nivel 2 insertada.");
					st.close();
				}
			} catch (SQLException e) {
				//empty
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		actualLevel--;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// stores all the inner tag values. Used for inserts into a table.
		if (Console.createDB) {
			String data = "";

			if (length > 1) {
				for (int i = start; i < length + start; i++) {
					if (ch[i] != ' ' || ch[i] != '\t' || ch[i] != '\n' || ch[i] != '\s') {
						data += ch[i];
						data = data.replace(" ", "");
						data = data.replace("  ", "");
						data = data.replace("   ", "");
						data = data.replace("\\n", "");
						data = data.replaceAll("\\r\\n", "");
						data = data.replaceAll("\\r|\\n", "");
					}
				}
				try {

					if (!data.isEmpty()) {
						if(xmlDBcolName != null) {
							st = conn.createStatement();
							
						st.executeUpdate("UPDATE " + xmlDBtableName + " SET " + xmlDBcolName + " = '" + data
								+ "' WHERE " + xmlDBpkName + " = " + xmlDBpkValue + ";");
						Console.txtArea.append("\n-->Valores en columna [" + xmlDBcolName + "] para PK (" + xmlDBpkValue
								+ ") insertados.");
						
						st.executeUpdate("UPDATE " + xmlDBtableName + " SET " + xmlDBcolName + " = '" + data
								+ "' WHERE " + xmlDBpkName + " = " + xmlDBpk2Value + ";");
						Console.txtArea.append("\n-->Valores en columna [" + xmlDBcolName + "] para PK (" + xmlDBpk2Value
								+ ") insertados.");
						
						st.close();
						}
					}
				} catch (SQLException e) {
					//empty
				}

			}
		}

		if (read) {
			for (int i = start; i < length + start; i++) {
				strBld.append(ch[i]);
			}
		}

	}
}