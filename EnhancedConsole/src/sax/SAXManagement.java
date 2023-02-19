package sax;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXParseException;

import main.Console;
import strings.Strings;

/**
 * Manages all SAX objects and instances for later management.
 * 
 * @author Patryk Bojar
 * @version 1.0.2, 23 Mar 2022
 */
public class SAXManagement {
	File fileXML = null;

	SAXParserFactory factory = null;
	SAXParser parser = null;
	SAXHandler manSax = null;

	/**
	 * Main empty Constructor.
	 */
	public SAXManagement() {
	}

	/**
	 * Main Constructor. Gets the XML file.
	 * 
	 * @param fileXML the XML file to be sent to the Parser.
	 */
	public SAXManagement(File fileXML) {
		setFile(fileXML);
	}

	/**
	 * Opens the XML file.
	 */
	public void openXML() {
		try {
			factory = SAXParserFactory.newInstance();
			parser = factory.newSAXParser();
			manSax = new SAXHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Starts the parser to begins handling the XML file; then return its content.
	 * 
	 * @param getLevel if true, shows the actual level of the XML file; if false,
	 *                 shows it's content.
	 * @return The XML file content.
	 */
	public String roamSAXshowContentOrLevel(boolean getLevel) {
		try {
			parser.parse(fileXML, manSax);
		} catch (FileNotFoundException e) {
			Console.txtArea.append("\n\n" + Strings.FILE_NOT_FOUND + ": " + fileXML.getName());
		} catch (IllegalArgumentException e) {
			Console.txtArea.append("\n\n" + Strings.ILLEGAL_ARGUMENT);
		} catch (SAXParseException e) {
			Console.txtArea.append("\n\n" + Strings.WRONG_FORMAT_XML_FILE);
		} catch (Exception e) {
			Console.txtArea.append("\n\n" + Strings.SOMETHING_WENT_WRONG);
		}
		if (getLevel) {
			return String.valueOf(SAXHandler.maxLevel);
		} else {
			return manSax.strBld.toString();
		}
	}

	//////////////////////
	// SETTERS AND GETTERS
	//////////////////////

	/**
	 * Sets the XML file.
	 * 
	 * @param fileXml the XML file.
	 */
	public void setFile(File fileXml) {
		this.fileXML = fileXml;
	}

}
