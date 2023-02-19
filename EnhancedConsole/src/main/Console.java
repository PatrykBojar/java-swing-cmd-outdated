package main;

//// START OF IMPORTS ////

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

import org.w3c.dom.NodeList;

import database.DatabaseConnector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;

import sax.SAXHandler;
import sax.SAXManagement;
import strings.Strings;

//// END OF IMPORTS ////

/**
 * Simulation of a command console to perform actions with customised
 * instructions. Contains all the code for a proper console functioning. <br>
 * Cannot be instantiated.
 * 
 * @author Patryk Bojar
 * @version 1.0.6, 22 Mar 2022
 */
public final class Console extends JFrame {

	// App Logo
	Image icon = Toolkit.getDefaultToolkit()
			.getImage("src" + File.separator + "img" + File.separator + "edib_logo.png");
	private static Scanner sc = new Scanner(System.in);

	private static String pathOri = "";
	private static String pathDes = "";
	public static String dbName = "";
	public static boolean createDB;

	/**
	 * Globally used variable, checks if XML tags have been activated or not. True
	 * by default.
	 */
	private static boolean showXMLTags = true;
	/**
	 * Locally used variable, checks if some methods need to write the file. True by
	 * default.
	 */
	private static boolean writeCopiedFile = true;

	// Swing used variables
	public static JTextArea txtArea;
	public static JPanel panel;
	public static JScrollPane areaScroll;
	private static String initialMessage = "A.E.T.R.Y.K [Versión 1.0.5]\n(c) 2022 Aetryk Corp. Todos los derechos reservados.\n\n"
			+ "Los comandos 'copia', 'mueve' y 'eliminaCascada', o comandos que necesiten doble confirmación '(s/n)',\nestán implementados al 90% en SWING.\nEl programa necesitará confirmación por consola de su IDE para terminar el proceso.\n\n";
	public static String pathMessage = System.getProperty("user.dir") + ">";
	private static int startingCommandPosition = 0; // gets the position of the caret (after pathMessage)

	/**
	 * Main private Constructor.
	 */
	private Console() {
		// Colours
		final Color SOFTER_BLACK = new Color(12, 12, 12);

		//////////////////////////
		///// START UI SWING /////
		//////////////////////////

		setSize(1000, 525);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(icon);
		setTitle("A.E.T.R.Y.K");
		setLocationRelativeTo(null);

		txtArea = new JTextArea();
		txtArea.setText(initialMessage + pathMessage);

		txtArea.setForeground(Color.GREEN);
		txtArea.setBackground(SOFTER_BLACK);
		txtArea.setFont(new Font("Consolas", Font.PLAIN, 15));
		txtArea.setLineWrap(true);
		txtArea.setCaret(new CustomCaret());
		txtArea.setCaretColor(Color.WHITE);
		txtArea.setFocusable(true);

		txtArea.setCaretPosition(initialMessage.length() + pathMessage.length());

		startingCommandPosition = txtArea.getCaretPosition();

		areaScroll = new JScrollPane(txtArea);

		// Interior Panel
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(areaScroll);

		add(panel);

		EventAwaken key = new EventAwaken();
		txtArea.addKeyListener(key);

		////////////////////////
		///// END UI SWING /////
		////////////////////////
	}

	/**
	 * Custom caret for the console.
	 *
	 * @author Patryk Bojar
	 * @source http://www.java2s.com/Code/Java/Swing-JFC/Acustomcaretclass.htm
	 */
	class CustomCaret extends DefaultCaret {
		private static final long serialVersionUID = 1463698199831092424L;
		private String mark = "▐";

		public CustomCaret() {
			setBlinkRate(500);
		}

		@Override
		protected synchronized void damage(Rectangle r) {
			if (r == null) {
				return;
			}

			JTextComponent comp = getComponent();
			FontMetrics fm = comp.getFontMetrics(comp.getFont());
			int textWidth = fm.stringWidth(">");
			int textHeight = fm.getHeight();
			x = r.x;
			y = r.y;
			width = textWidth;
			height = textHeight;
			repaint(); // calls getComponent().repaint(x, y, width, height)
		}

		@Override
		public void paint(Graphics g) {
			JTextComponent comp = getComponent();
			if (comp == null) {
				return;
			}

			int dot = getDot();
			Rectangle r = null;
			try {
				r = comp.modelToView(dot);
			} catch (BadLocationException e) {
				return;
			}
			if (r == null) {
				return;
			}

			if ((x != r.x) || (y != r.y)) {
				repaint(); // erase previous location of caret
				damage(r);
			}

			if (isVisible()) {
				FontMetrics fm = comp.getFontMetrics(comp.getFont());

				g.setColor(comp.getCaretColor());
				g.drawString(mark, x, y + fm.getAscent());
			}
		}
	}

	/**
	 * This is the main method. Starts the console.
	 * 
	 * @param args unused.
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		new Console().setVisible(true);
	}

	class EventAwaken implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			// empty

		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				try {
					askForCommand();
					e.consume();
				} catch (InterruptedException e1) {
					txtArea.append("\n\n" + e1.getMessage());
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if (txtArea.getCaretPosition() == startingCommandPosition) {
					e.consume();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (txtArea.getCaretPosition() == startingCommandPosition) {
					e.consume();
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				e.consume();
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				e.consume();
			}
			if (e.getKeyCode() == KeyEvent.VK_HOME) {
				e.consume();
				txtArea.setCaretPosition(startingCommandPosition);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// NOTHING.
		}
	}

	/**
	 * Moves the file from source/origin to destination path. If needed deletes the
	 * file from source path and saves the new file.
	 * 
	 * @param source the origin path of the file.
	 * @param dest   the destination or new path of the file.
	 */
	private static void moveFile(File source, File dest) {
		copyFile(source, dest, true);
		// Deletes the file only if the source and destination path exists, source
		// path is different from destination path and writes file is true.
		if ((source.exists() && dest.exists()) && (!source.equals(dest)) && writeCopiedFile) {
			deleteFile(source, false);
		}
	}

	/**
	 * Gets the name of the file and copies it at the specified location. It checks
	 * all the possibilities when copying a file. Tries to simulate Windows CMD
	 * copy/move command.
	 * 
	 * @param source               the source path file.
	 * @param dest                 the destination path file.
	 * @param isCalledFromMoveFile true if the method is called from moveFile
	 *                             method; false otherwise.
	 */
	@SuppressWarnings("resource")
	private static void copyFile(File source, File dest, boolean isCalledFromMoveFile) {
		try {
			if (isPathCorrect(source, dest)) {
				if (source.exists()) {
					// Creates a new path which consists of destination path and the source file
					// name.
					File copiedFile = new File(dest + File.separator + source.getName());

					// If both paths are the same, shows a message, sets write file to false and
					// ends the execution of the method.
					if (source.equals(copiedFile) || source.equals(dest)) {
						writeCopiedFile = false;
						if (isCalledFromMoveFile) {
							txtArea.append("\n\n" + Strings.FILE_MOVED);
							txtArea.append("\n\n" + pathMessage);
							return;
						}
						txtArea.append("\n\n" + Strings.FILE_COPIED_SAME_PLC);
						txtArea.append("\n\n" + pathMessage);
						return;
					}

					if (isNewFileNameCorrect(dest)) {
						// If destination path exists, start the main chain of tests.
						// If not, creates the file with the name of destination path with no file
						// specified.
						if (dest.exists()) {

							String choice = ""; // user's string choice value.

							// If the name of the file is not specified, then it will be the
							// name of source path.
							// If this file exists, then asks for overwrite it.
							if (copiedFile.exists()) {
								do {
									System.out.print("¿Sobreescribir " + source + "? [s/n]: ");
									choice = sc.nextLine().toLowerCase();
								} while ((!choice.equals("s")) && (!choice.equals("n")));
								writeCopiedFile = choice.equals("s"); // if choice is "s", then set it to true,
								// false
								// otherwise.

								// If the destination path it's a directory, then it exists so it will copy the
								// file with the source name.
								// If it not exists, then the destination path has his own and unique name for
								// the file. It will copy it with than name.
								if (dest.isDirectory()) {
									// Checks if write is enabled and calls for write method.
									// If false, shows a information message.
									if (writeCopiedFile) {
										writeFile(new FileInputStream(source), new FileOutputStream(copiedFile),
												isCalledFromMoveFile);
									} else {
										if (isCalledFromMoveFile) {
											txtArea.append("\n\n" + Strings.FILE_NOT_MOVED);
											txtArea.append("\n\n" + pathMessage);
											return;
										}
										txtArea.append("\n\n" + Strings.FILE_NOT_COPIED);
										txtArea.append("\n\n" + pathMessage);
									}

								} else {
									writeFile(new FileInputStream(source), new FileOutputStream(dest),
											isCalledFromMoveFile);
									return;
								}

							} else {
								// If the destination path it's a file and exists, then it overwrites it.
								if (dest.isFile()) {
									do {
										System.out.print("¿Sobreescribir " + source + "? [s/n]: ");
										choice = sc.nextLine().toLowerCase();
									} while ((!choice.equals("s")) && (!choice.equals("n")));

									writeCopiedFile = choice.equals("s");

									if (writeCopiedFile) {
										writeFile(new FileInputStream(source), new FileOutputStream(dest),
												isCalledFromMoveFile);
										return;
									} else {

										if (isCalledFromMoveFile) {
											txtArea.append("\n\n" + Strings.FILE_NOT_MOVED);
											txtArea.append("\n\n" + pathMessage);
											return;
										}
										txtArea.append("\n\n" + Strings.FILE_NOT_COPIED);
										txtArea.append("\n\n" + pathMessage);
										return;
									}
								}
								// Destination file name is not specified, so it'll be the name of the source
								// path.
								writeFile(new FileInputStream(source), new FileOutputStream(copiedFile),
										isCalledFromMoveFile);
							}
						} else {
							// Creates the file with source name.
							// This call occurs only once if the file doesn't already exists.
							writeCopiedFile = true;
							writeFile(new FileInputStream(source), new FileOutputStream(dest), isCalledFromMoveFile);
						}
					} // isFileNameCorrect ends here.

				} else { // if file doesn't exists.
					throw new FileNotFoundException();
				}
			} // isPathCorrect ends here.
		} catch (FileNotFoundException e) {
			txtArea.append("\n\n" + Strings.FILE_NOT_FOUND);
			txtArea.append("\n\n" + pathMessage);
		}
	}

	/**
	 * Checks if the name contains any invalid Windows OS characters or it uses
	 * reserved names by the system.
	 * 
	 * @param file the file to be checked.
	 * @return true if the file name has a correct syntax; false otherwise.
	 */
	private static boolean isNewFileNameCorrect(File file) {
		String[] invalidChars = { "\\", ":", "*", "<", ">", "\"" };
		String fileName = file.getName();

		for (String ch : invalidChars) {
			if (fileName.contains(ch)) {
				txtArea.append("\n\n" + Strings.INVALID_FILE_NAME);
				txtArea.append("\n\n" + pathMessage);
				return false;
			}
		}
		if (removeFileExtension(fileName, true).equalsIgnoreCase("aux")) {
			txtArea.append("\n\n" + Strings.SYS_RESERVED_NAME_THROW);
			txtArea.append("\n\n" + pathMessage);
			return false;
		}
		return true;
	}

	/**
	 * Checks if all path syntax requirements are met.
	 * 
	 * @param pathOne the source and first path to be checked.
	 * @param pathTwo the destination and second path.
	 * @return true if the path has a correct syntax; false otherwise.
	 */
	private static boolean isPathCorrect(File pathOne, File pathTwo) {
		// Last string from both paths.
		String lastOriString = pathOri.substring(pathOri.length() - 1, pathOri.length());
		String lastDesString = pathDes.substring(pathDes.length() - 1, pathDes.length());

		// If the destination path ends with a separator and it's not a directory, throw
		// an error.
		if (lastDesString.equals(File.separator) && !pathTwo.isDirectory()) {
			txtArea.append("\n\n" + Strings.PATH_NOT_FOUND);
			txtArea.append("\n\n" + pathMessage);
			return false;
		}
		// If the origin path ends with a separator, throw an error. Can't copy all
		// content from a directory or file can't end with a separator.
		if (lastOriString.equals(File.separator)) {
			txtArea.append("\n\n" + pathOne + File.separator + "*");
			txtArea.append("\n" + Strings.NAME_DIR_NOT_FOUND);
			txtArea.append("\n\n" + pathMessage);
			return false;
		}
		// If origin path or destination path hasn't a separator, throw an error.
		// Requires at least one separator in each path.
		if (!pathOri.contains(File.separator) || !pathDes.contains(File.separator)) {
			txtArea.append("\n\n" + Strings.PATH_NOT_SEPARATED);
			txtArea.append("\n" + Strings.PATH_GENERAL_THROW);
			txtArea.append("\n\n" + pathMessage);

			return false;
		}

		// If origin path is a directory, then can't execute copy or move.
		if (pathOne.isDirectory()) {
			txtArea.append("\n" + Strings.CANT_CPY_MVE_DIR);
			txtArea.append("\n\n" + pathMessage);
			return false;
		}

		// If a path gets here, it has the desired format to proceed with copy or move
		// file.

		return true;
	}

	/**
	 * Writes the file and shows a confirmation message when finished.
	 * 
	 * @param fis                  the bytes from a file in a file system. File of
	 *                             which the content is going to be copied.
	 * @param fos                  the output stream for writing data. File where
	 *                             the new content is going to be written on.
	 * @param isCalledFromMoveFile if true, shows confirmation messages for move
	 *                             command; if false, shows messages for copy
	 *                             command.
	 */
	private static void writeFile(FileInputStream fis, FileOutputStream fos, boolean isCalledFromMoveFile) {
		try {
			int b; // bytes
			while ((b = fis.read()) != -1) {
				fos.write(b);
			}
			// Closing streams
			fos.flush();
			fis.close();
			fos.close();

			// Controlling different messages, depending on if it's called
			// or not from Mover.
			if (isCalledFromMoveFile) {
				txtArea.append("\n\n" + Strings.FILE_MOVED);
				txtArea.append("\n\n" + pathMessage);
				return;
			}
			txtArea.append(Strings.FILE_COPIED);
			txtArea.append("\n\n" + pathMessage);

		} catch (IOException e) {
			txtArea.append(Strings.SOMETHING_WENT_WRONG);
			txtArea.append("\n\n" + pathMessage);
		}
	}

	/**
	 * Removes a file from disk and then displays an informational message on the
	 * console.
	 * 
	 * @param file                the file to be deleted.
	 * @param showConfirmationMsg true if the operation returns a confirmation
	 *                            message; false otherwise.
	 */
	private static boolean deleteFile(File file, boolean showConfirmationMsg) {
		boolean isDeleted = false;

		if (file.exists() && file.isFile()) {
			file.delete();
			if (showConfirmationMsg) {
				txtArea.append("\n\n" + Strings.FILE_DELETED);
				txtArea.append("\n\n" + pathMessage);
			}
			isDeleted = true;
		} else {
			if (showConfirmationMsg) {
				txtArea.append("\n\n" + Strings.FILE_NOT_FOUND);
				txtArea.append("\n\n" + pathMessage);
			}
			isDeleted = false;
		}
		return isDeleted;
	}

	/**
	 * List the content of a directory with additional information such as type,
	 * size and last modification.
	 * 
	 * @param path the path where the directories and files are listed.
	 */
	private static void listDirContent(File path) {
		File[] files = path.listFiles();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
		int totalFileNum = 0;
		int totalDirNum = 0;
		long totalFileB = 0;

		if (path.isFile()) {
			txtArea.append("\n\n" + Strings.CANT_SHOW_DIR_CONTENTS);
			txtArea.append("\n\n" + pathMessage);
			return;
		}

		if (path.exists()) {
			txtArea.append("\n\n" + " El volumen de la unidad es: " + (path.getTotalSpace() / 1073741824) + " GB");
			txtArea.append("\n" + " Espacio usable: " + (path.getUsableSpace() / 1073741824) + " GB");
			txtArea.append("\n\n" + " Directorio de " + path + "\n\n");

			for (int i = 0; i < files.length; i++) {
				String lastModified = sdf.format(files[i].lastModified());

				if (files[i].isFile()) {
					txtArea.append(
							lastModified + String.format("%21s", customFormat("###,###.###", files[i].length())));
					txtArea.append("\t" + files[i].getName() + "\n");

					totalFileNum++;
					totalFileB += files[i].length();
				} else {
					txtArea.append(lastModified + "    <DIR>\t\t" + files[i].getName() + "\n");
					totalDirNum++;
				}
			}
			// Files info
			txtArea.append(String.format("%17d", totalFileNum) + " arch.");
			txtArea.append(String.format("%15s", customFormat("###,###.###", totalFileB)));
			// Directories info
			txtArea.append(String.format("%5s%n", "-B-"));
			txtArea.append(String.format("%17d", totalDirNum) + " dirs.");

			txtArea.append("\n\n" + pathMessage);
		} else {
			txtArea.append("\n\n" + Strings.PATH_NOT_FOUND);
			txtArea.append("\n\n" + pathMessage);
		}
	}

	/**
	 * Gets the directories and files from the system folder path, saves all the
	 * data into a StringBuilder Object and then shows them on the screen. The more
	 * 
	 * @param folder the folder path from which all content will be listed.
	 */
	private static void showDirTree(File folder) {
		if (folder.isDirectory()) {
			int space = 0;
			StringBuilder strBld = new StringBuilder();

			txtArea.append("\n\n" + Strings.FOLDER_PATH_LIST);
			txtArea.append("\n" + folder.getAbsolutePath().toUpperCase() + "\n");

			getDirTree(folder, space, strBld);

			txtArea.append(strBld.toString());
		} else {
			txtArea.append("\n\n" + Strings.CANT_SHOW_DIR_CONTENTS);
		}

	}

	/**
	 * 
	 * @param folder the path of the folder from which the content will be listed.
	 * @param space  the space or indentation that increases with child folders.
	 * @param strBld the string builder that saves all the data tree.
	 */
	private static void getDirTree(File folder, int space, StringBuilder strBld) {
		File[] files = folder.listFiles();

		strBld.append(getSpaceString(space));
		// strBld.append("├─ 📁 "); // indicates the folder.
		strBld.append("   ");
		strBld.append(folder.getName());
		strBld.append("\n");

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					getDirTree(file, space + 1, strBld);
				} else {
					getFileOnly(file, space + 1, strBld);
				}
			}
		}
	}

	/**
	 * 
	 * @param file   the path of the file from which the content will be listed.
	 * @param space  the space or indentation that increases with child folders and
	 *               files.
	 * @param strBld the string builder that saves all the data tree for files.
	 */
	private static void getFileOnly(File file, int space, StringBuilder strBld) {
		// String fileExt = getFileExtension(file.getName());

		strBld.append(getSpaceString(space));
		strBld.append("   ");

		// Simple checks for setting the right symbol for every file, depending on its
		// extension.

		/*
		 * if (fileExt.equalsIgnoreCase(".zip") || fileExt.equalsIgnoreCase(".rar")) {
		 * strBld.append("├─ 📚 "); // indicates a compressed file. } else if
		 * (fileExt.equalsIgnoreCase(".gif") || fileExt.equalsIgnoreCase(".png") ||
		 * fileExt.equalsIgnoreCase(".jpg")) { strBld.append("├─ 📷 "); // indicates a
		 * graphic file, like images. } else if (fileExt.equalsIgnoreCase(".pdf")) {
		 * strBld.append("├─ 📑 "); } else { strBld.append("├─ 📄 "); // other generic
		 * files, like .txt. }
		 */

		strBld.append(file.getName());
		strBld.append("\n");
	}

	/**
	 * 
	 * @param space the left and vertical branch that increases with child folders
	 *              or files.
	 * @return The vertical branches of strings / indentations for data tree.
	 */
	private static String getSpaceString(int space) {
		StringBuilder strBld = new StringBuilder();
		for (int i = 0; i < space; i++) {
			// strBld.append("│ ");
			strBld.append("   ");
		}
		return strBld.toString();
	}

	/**
	 * Reads and prints on the screen the content of a TXT file.
	 * 
	 * @param file         the file to read.
	 * @param printContent if true, prints the content on the screen; if false,
	 *                     doesn't show the content.
	 * @return The content of the TXT file.
	 */
	private static List<String> showTXTContent(File file, boolean printContent) {
		ArrayList<String> contentInArray = null;
		try (BufferedReader fileToRead = new BufferedReader(new FileReader(file))) {
			String line = "";
			contentInArray = new ArrayList<>();

			if (getFileExtension(file.getName()).equalsIgnoreCase(".txt")) {
				/*
				 * if (printContent) { System.out.println(""); }
				 */
				while ((line = fileToRead.readLine()) != null) {
					if (printContent) {
						txtArea.append("\n" + line);
					}
					contentInArray.add(line);
				}
				/*
				 * if (printContent) { System.out.println(); }
				 */
				fileToRead.close();
			} else {
				txtArea.append("\n\n" + Strings.TXT_HANDLE_ONLY);
			}
		} catch (EOFException e) {
			// end of file
		} catch (Exception e) {
			txtArea.append("\n\n" + Strings.FILE_NOT_ACCESIBLE);
		}
		return contentInArray;
	}

	/**
	 * Checks if the XML file exists; if true it reads the file, taking into account
	 * the input of tags modifiers.
	 * 
	 * @param fileXML     the XML file.
	 * @param showXMLTags the tag modifiers. If true, shows the XML tags; if false,
	 *                    doesn't show the XML tags.
	 */
	private static void showXMLContent(File fileXML) {
		createDB = false;
		if (fileXML.exists()) {
			SAXManagement manSAX = new SAXManagement(fileXML);

			manSAX.openXML();
			txtArea.append("\n" + manSAX.roamSAXshowContentOrLevel(false));
		} else if (!getFileExtension(pathOri).equalsIgnoreCase(".xml")) {
			txtArea.append("\n\n" + Strings.XML_HANDLE_ONLY);
		} else {
			txtArea.append("\n\n" + Strings.FILE_NOT_FOUND);
		}
	}

	/**
	 * Checks if the content of both files it's the same, if so, shows a message; if
	 * it doesn't match, shows each different line and compares it to the other
	 * file.
	 * 
	 * @param fileOne the first file to compare.
	 * @param fileTwo the second file to compare with the first file.
	 */
	private static void compareTXT(File fileOne, File fileTwo) {

		// If one of the files doesn't have a txt extension, throws an error message and
		// returns.
		if (!getFileExtension(fileOne.getName()).equalsIgnoreCase(".txt")
				|| !getFileExtension(fileTwo.getName()).equalsIgnoreCase(".txt")) {
			txtArea.append("\n\n" + Strings.TXT_HANDLE_ONLY);
			return;
		}

		// Compare only and only if two files exist.
		if (fileOne.exists() && fileTwo.exists()) {
			// Original file content Array.
			ArrayList<String> fileOneList = new ArrayList<>();
			fileOneList.addAll(showTXTContent(fileOne, false));
			// Second file content Array to compare with the Original file.
			ArrayList<String> fileTwoList = new ArrayList<>();
			fileTwoList.addAll(showTXTContent(fileTwo, false));

			// Checks if both files are not equal.
			if (!(fileTwoList.equals(fileOneList))) {

				// If one of the files is empty (no lines, content or characters), then throw an
				// error message and return.
				if (fileOneList.isEmpty() || fileTwoList.isEmpty()) {
					txtArea.append("\n\n" + Strings.ONE_FILE_HAS_NO_LINES);
					return;
				}
				txtArea.append("\n");

				int aux = 0;
				while (aux < fileTwoList.size() || aux < fileOneList.size()) {
					// System.out.println(fileOne.getName() + ": " + fileOneList.get(aux));
					// Prints the n file line if the content doesn't match.
					if (!fileOneList.get(aux).equals(fileTwoList.get(aux))) {
						txtArea.append("\n" + fileOne.getName() + ": " + fileOneList.get(aux));
						txtArea.append("\n" + fileTwo.getName() + ": " + fileTwoList.get(aux));
						txtArea.append("\n");
					}

					// If one of the files has less lines, then adds empty string to compare.
					if (fileOneList.size() > fileTwoList.size()) {
						fileTwoList.add(" ");
					} else if ((fileTwoList.size() > fileOneList.size())) {
						fileOneList.add(" ");
					}
					aux++;
				}
			} else {
				// files are equal.
				txtArea.append("\n\n" + Strings.FILES_ARE_EQUAL);
			}
		} else {
			// 1 or more files doesn't exist.
			txtArea.append("\n\n" + Strings.ONE_OF_FILES_NOT_FOUND);
		}
	}

	private static void showHelp() {
		txtArea.append("\n\n ([parámetro]) Indica que el parámetro es opcional.\n\n");
		txtArea.append(
				"mueve         [ruta/archivo] [ruta]            Mueve un archivo de un directorio a otro, eliminándolo del origen.\n");
		txtArea.append("copia         [ruta/archivo] [ruta]            Copia un archivo a otra ubicación."
				+ " en la misma ruta.\n");
		txtArea.append("elimina       [ruta/archivo] -                 Elimina un archivo.\n");
		txtArea.append(
				"lista         ([ruta])       -                 Muestra una lista de archivos y subdirectorios en un directorio.\n");
		txtArea.append(
				"listaArbol    ([ruta])       -                 Muestra de forma gráfica la estructura de carpetas de una unidad o ruta.\n");
		txtArea.append(
				"muestraTXT    [ruta/archivo] -                 Lee y muestra el contenido de un archivo de texto con la extensión .txt.\n");
		txtArea.append(
				"muestraXML    [ruta/archivo] [/modificador]    Lee y muestra el contenido de un archivo XML con la extensión .xml.\n");
		txtArea.append(
				"comparaTXT    [ruta/archivo] [ruta/archivo]    Compara y muestra si dos ficheros de texto son iguales línea a línea.\n");
		txtArea.append(
				"ayuda         -              -                 Proporciona información de ayuda para los comandos"
						+ " de A.E.T.R.Y.K.\n");
		txtArea.append("salir         -              -                 Termia la ejecución y cierra la consola.\n");

		txtArea.append("\n\n" + pathMessage);
	}

	/**
	 * Sets the permissions given to a certain file.
	 * 
	 * @param file       the file to work with permissions.
	 * @param isReadOnly if true sets the file to read-only; if false sets the file
	 *                   to writable and readable.
	 */
	private static void readOnly(File file, boolean isReadOnly) {
		if (isReadOnly) {
			// Sets the file to readable only.
			file.setWritable(false);
		} else {
			// Sets the file to readable and writable.
			file.setWritable(true);
		}
		txtArea.append("\n\n" + Strings.PERMISSIONS_APPLIED);
	}

	/**
	 * Deletes all the files from a given path.
	 * 
	 * @param path      the path of which the files are being deleted.
	 * @param extension the extension type of the files to be removed.
	 */
	private static void deleteOnCascade(File path, String extension) {

		extension = "." + extension; // adding the dot to the extension.

		File[] files = path.listFiles();
		String choice = ""; // user's string choice value.

		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					// Checks if the file has the same extension as user's input.
					if (getFileExtension(String.valueOf(file)).equalsIgnoreCase(extension)) {
						// If the file has read-only permissions.
						if (!file.canWrite()) {
							do {
								System.out.print(file + " tiene permisos de solo lectura.");
								System.out.print("\nAún así, ¿desea eliminar " + file + "? [s/n] ");
								choice = sc.nextLine().toLowerCase();
							} while ((!choice.equals("s")) && (!choice.equals("n")));

							// Delete the read-only file.
							if (choice.equals("s")) {
								// Sets the permissions of the file to writable.
								readOnly(file, false);

								// If the SO can't delete the file, show informative message and keep going with
								// other files.
								if (!deleteFile(file, false)) {
									System.out.print(file + " no ha podido ser eliminado.");
								}
							}
						}
						// The file has already writable permissions. Gets deleted.
						else {
							// Repeat until answer if affirmative.
							do {
								System.out.print("¿Desea eliminar " + file + "? [s/n]: ");
								choice = sc.nextLine().toLowerCase();
							} while ((!choice.equals("s")) && (!choice.equals("n")));
							// Delete the file.
							if (choice.equals("s")) {
								deleteFile(file, false);
							}
						}
					}

				}
			}
		}
	}

	/**
	 * Counts all the nodes provided by the user input. Shows an informative
	 * messages with the total occurrences of that node.
	 * 
	 * @param file     the xml file.
	 * @param nodeName the name of the xml file node.
	 */
	private static void nodeQuantity(File file, String nodeName) {
		createDB = false;
		DocumentBuilderFactory docBldFactory = DocumentBuilderFactory.newInstance();

		try (InputStream is = new FileInputStream(file)) {

			// If the file has XML extension.
			if (getFileExtension(pathOri).equalsIgnoreCase(".xml")) {
				DocumentBuilder docBuilder = docBldFactory.newDocumentBuilder();
				org.w3c.dom.Document doc = docBuilder.parse(is);

				// Gets all the elements (nodes) by the user name.
				NodeList list = doc.getElementsByTagName(nodeName);

				// Shows a message depending of the list result. If 0, then no match was found.
				if (list.getLength() != 0) {
					txtArea.append("\n\n" + "Ocurrencia del nodo (" + nodeName + "): " + list.getLength());
				} else {
					txtArea.append("\n\n" + "No se ha podido encontrar el nodo (" + nodeName + ").");
				}

				is.close(); // Closing the stream.

			} else if (!getFileExtension(pathOri).equalsIgnoreCase(".xml")) {
				txtArea.append("\n\n" + Strings.XML_HANDLE_ONLY);
			} else {
				txtArea.append("\n\n" + Strings.FILE_NOT_FOUND);
			}

		} catch (EOFException e) {
			// end of file.
		} catch (Exception e) {
			txtArea.append("\n\n" + Strings.FILE_NOT_ACCESIBLE);
		}
	}

	/**
	 * Executes the SQL QUERY and shows the results on the screen.
	 * 
	 * @param sqlQuery   the sql query.
	 * @param sqlCommand the sql command, as: SELECT or SHOW.
	 */
	private static void executeDBquery(String sqlQuery, String sqlCommand) {
		try {
			Statement st;
			ResultSet rs;
			st = DatabaseConnector.getConnection().createStatement();
			rs = st.executeQuery(sqlQuery);

			if (sqlCommand.equalsIgnoreCase("SELECT")) {
				txtArea.append("\n\n");
				// Paints the column names.
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					txtArea.append(String.format("%25s", "[" + rs.getMetaData().getColumnName(i) + "]"));
				}
				txtArea.append("\n");
				// Shows all the table data.
				while (rs.next()) {
					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						txtArea.append(String.format("%25s", rs.getString(i)));
					}
					txtArea.append("\n");
				}
			} else if (sqlCommand.equalsIgnoreCase("SHOW")) {
				txtArea.append("\n");
				// Stores all the data in the set.
				rs = st.getResultSet();
				txtArea.append("\nListado de todas las bases de datos:\n");
				while (rs.next()) {
					txtArea.append(rs.getString("Database") + "\n");
				}
			}

			rs.close();
			st.close();
		} catch (SQLException sqle) {
			txtArea.append(
					"\n\nError de sintaxis en la sentencia SQL. Por favor, revisa\nel comando introducido o que los datos coincidan con la base de datos.\n");
			txtArea.append("Otros posibles errores:\n");
			txtArea.append(sqle.getMessage() + "\n");
		}
	}

	/**
	 * Executes and selects the new database to work with.
	 * 
	 * @param sqlQuery the sql update query.
	 */
	private static void executeDBuse(String sqlQuery) {
		try {
			Statement st;
			st = DatabaseConnector.getConnection().createStatement();
			st.executeUpdate(sqlQuery);

			st.close();
		} catch (SQLException sqle) {
			txtArea.append(
					"\n\nError de sintaxis en la sentencia SQL. Por favor, revisa\nel comando introducido o que los datos coincidan con la base de datos.\n");
			txtArea.append("Otros posibles errores:\n");
			txtArea.append(sqle.getMessage() + "\n");
		}
	}

	/**
	 * Executes the SQL update query, as: insert, delete, update, alter, drop, etc.
	 * 
	 * @param sqlQuery the sql update query
	 */
	private static boolean executeDBupdate(String sqlQuery) {
		boolean updateExecuted = false;
		try {
			Statement st;
			st = DatabaseConnector.getConnection().createStatement();

			if (st.executeUpdate(sqlQuery) == 1) {
				updateExecuted = true;
			} else {
				updateExecuted = false;
			}

			st.close();
		} catch (SQLException sqle) {
			txtArea.append(
					"\n\nError de sintaxis en la sentencia SQL. Por favor, revisa\nel comando introducido o que los datos coincidan con la base de datos.\n");
			txtArea.append("Otros posibles errores:\n");
			txtArea.append(sqle.getMessage() + "\n");
		}
		return updateExecuted;
	}

	/**
	 * Reads the file and obtains its maximum level.
	 * 
	 * @param fileXML the XML file.
	 */
	private static void levelXML(File fileXML) {
		createDB = false;
		if (fileXML.exists()) {
			SAXManagement manSAX = new SAXManagement(fileXML);

			manSAX.openXML();
			txtArea.append("\nEl nivel del archivo XML es: " + manSAX.roamSAXshowContentOrLevel(true));
		} else if (!getFileExtension(pathOri).equalsIgnoreCase(".xml")) {
			txtArea.append("\n\n" + Strings.XML_HANDLE_ONLY);
		} else {
			txtArea.append("\n\n" + Strings.FILE_NOT_FOUND);
		}
	}

	/**
	 * Creates a database from scratch by reading a XML file.
	 * 
	 * The database will have the name given by the level 0.
	 * 
	 * The table will have the name given by the level 1.
	 * 
	 * The table fields will have the name given by level 2.
	 * 
	 * @param fileXML the XML file.
	 */
	private static void createDBfromXML(File fileXML) {
		if (fileXML.exists()) {
			createDB = true;
			dbName = "";
			SAXManagement manSAX = new SAXManagement(fileXML);

			manSAX.openXML();
			manSAX.roamSAXshowContentOrLevel(false);
		} else if (!getFileExtension(pathOri).equalsIgnoreCase(".xml")) {
			txtArea.append("\n\n" + Strings.XML_HANDLE_ONLY);
		} else {
			txtArea.append("\n\n" + Strings.FILE_NOT_FOUND);
		}
	}

	private static void createXMLfromDB(String dataBaseName) {
		try {
			String dbNameAux = dataBaseName; // saves the real dbName into an aux variable.

			Statement st;
			ResultSet rs;

			dbName = ""; // sets the dbName to empty for a "empty" connection.
			st = DatabaseConnector.getConnection().createStatement();
			rs = st.executeQuery("SHOW DATABASES;");
			rs = st.getResultSet();

			ArrayList<String> dbNamesList = new ArrayList<>();

			while (rs.next()) {
				dbNamesList.add(rs.getString("Database")); // stores all database names into a list.
			}

			rs.close();
			st.close();

			// If the database name exists, then restores the original database name and
			// executes the query. If not, shows an informative message.
			if (dbNamesList.contains(dbNameAux)) {
				dbName = dbNameAux;
				BufferedWriter bw = new BufferedWriter(new FileWriter(dbNameAux + ".xml"));
				executeDBuse("USE " + dbNameAux + ";");

				bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				bw.newLine();
				// start node element
				bw.write("<" + dbNameAux + ">");
				// put here all the data from db.
				// start db data.
				// end db data.
				// end node element.
				bw.newLine();
				bw.write("</" + dbNameAux + ">");
				bw.flush();
				bw.close();

				txtArea.append("\n\nArchivo XML creado.\nConsulte la siguiente ruta:\n");
				txtArea.append("--> " + System.getProperty("user.dir") + " <--");
			} else {
				txtArea.append("\nEsta base de datos no existe.\nNo se ha podido crear el XML.");
				txtArea.append("\n\n" + pathMessage);
			}
		} catch (Exception e) {
			txtArea.append("\n\n"+Strings.SOMETHING_WENT_WRONG);
		}
	}

	/**
	 * Asks for the command written in the console and then splits it into orders
	 * and useful data for further execution.
	 * 
	 * @throws InterruptedException
	 */
	public static void askForCommand() throws InterruptedException {
		try {
			String disk = System.getProperty("user.dir").substring(0, 3); // actual project's drive.

			// Asks for the command
			txtArea.append("\n\n" + pathMessage);
			String commnd = txtArea.getText().substring(startingCommandPosition,
					txtArea.getText().length() - pathMessage.length());

			String[] order = commnd.split("\\s");

			// When the command is a valid order, then checks the parameters and if they are
			// correct, execute the command.

			// ----------------------------------------
			// NORMAL MODE: ALL COMMANDS. *START*
			// ----------------------------------------
			if (pathMessage.equals(System.getProperty("user.dir") + ">")) {
				switch (order[0]) {
				// MOVE COMMAND.
				case "mueve": {
					if (order.length == 3) {
						pathOri = order[1]; // defines source path of the file to move.
						pathDes = order[2]; // defines destination path of the file to move.
						moveFile(new File(pathOri), new File(pathDes));
					} else if (order.length == 2) { // show a message when the command has only 1 path.
						txtArea.append("\n\n" + Strings.MISSING_DEST_PATH + "\n");
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");
					}
					break;
				}
				// COPY COMMAND.
				case "copia": {
					if (order.length == 3) {
						pathOri = order[1]; // defines source path of the file to copy.
						pathDes = order[2]; // defines destination path of the file to copy.
						copyFile(new File(pathOri), new File(pathDes), false);
					} else if (order.length == 2) { // show a message when the command has only 1 path.
						txtArea.append("\n\n" + Strings.MISSING_DEST_PATH + "\n");
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");
						txtArea.append("\n\n" + pathMessage);
					}
					break;
				}
				// DELETE COMMAND.
				case "elimina": {
					if (order.length == 2) {
						pathOri = order[1]; // defines the path of file to delete.
						deleteFile(new File(pathOri), true);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");

					}
					break;
				}
				// LIST DIR COMMAND.
				case "lista": {
					if (order.length == 1) {
						listDirContent(new File(disk));
					} else if (order.length == 2) {
						pathOri = order[1];
						listDirContent(new File(pathOri));
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");

					}
					break;
				}
				// LIST TREE COMMAND.
				case "listaArbol": {
					if (order.length == 1) {
						showDirTree(new File(disk));
					} else if (order.length == 2) {
						pathOri = order[1];
						showDirTree(new File(pathOri));
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");
					}
					break;
				}
				// COMPARE TXTs COMMAND.
				case "comparaTXT": {
					if (order.length == 3) {
						pathOri = order[1]; // defines source path for further uses.
						pathDes = order[2]; // defines destination path for further uses.
						compareTXT(new File(pathOri), new File(pathDes));
						txtArea.append("\n\n" + pathMessage);
					} else if (order.length == 2) { // show a message when the command has only 1 path.
						txtArea.append("\n\n" + Strings.MISSING_DEST_PATH + "\n");

					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");

					}
					break;
				}
				// SHOW TXT COMMAND.
				case "muestraTXT": {
					if (order.length == 2) {
						pathOri = order[1]; // defines the path of TXT file to show.
						showTXTContent(new File(pathOri), true);
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");

					}
					break;
				}
				// SHOW XML COMMAND.
				case "muestraXML": {
					if (order.length == 3) {
						pathOri = order[1]; // defines the path of XML file to show.
						if (order[2].equals("/conEtiquetas")) {
							setShowXMLTags(true); // will show XML tags.
						} else if (order[2].equals("/sinEtiquetas")) {
							setShowXMLTags(false); // won't show XML tags.
						} else { // show error message when tags aren't correct.
							txtArea.append("\n\n" + "\"" + order[2] + "\" " + Strings.WRONG_SYNTAX_MODIFIER + "\n");
							txtArea.append(Strings.SHOW_XML_WITH_TAGS + "\n");
							txtArea.append(Strings.SHOW_XML_WITHOUT_TAGS + "\n");
							txtArea.append("\n\n" + pathMessage);
							return;
						}
						showXMLContent(new File(pathOri));
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");
						txtArea.append("\n\n" + pathMessage);
					}
					break;
				}
				// READ ONLY PERMISSION FILE.
				case "soloLectura": {
					if (order.length == 3) {
						boolean isReadOnly;
						pathOri = order[1]; // defines the path of the file to be modified with only lecture permission.
						if (order[2].equals("/s")) {
							isReadOnly = true; // read only.
						} else if (order[2].equals("/n")) {
							isReadOnly = false; // not read only.
						} else { // show error message when modifiers aren't correct.
							txtArea.append("\n\n" + "\"" + order[2] + "\" " + Strings.WRONG_SYNTAX_MODIFIER + "\n");
							txtArea.append("\n\n" + pathMessage);
							return;
						}
						readOnly(new File(pathOri), isReadOnly);
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX);
						txtArea.append("\n\n" + pathMessage);
					}
					break;
				}
				// DELETE ON CASCADE ALL FILES FROM PATH.
				case "eliminaCascada": {
					if (order.length == 3) {
						pathOri = order[1]; // defines the path.
						String extension = order[2]; // defines the extension of files to be deleted.

						deleteOnCascade(new File(pathOri), extension);
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX);
						txtArea.append("\n\n" + pathMessage);
					}
					break;
				}
				// COUNT THE XML NODES.
				case "cantidadNodo": {
					if (order.length == 3) {
						pathOri = order[1]; // defines the path.
						String node = order[2]; // defines the name of the xml node to be counted.

						nodeQuantity(new File(pathOri), node);
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX);
						txtArea.append("\n\n" + pathMessage);
					}
					break;
				}
				// GET THE XML FILE LEVEL.
				case "nivelXML": {
					if (order.length == 2) {
						pathOri = order[1]; // defines the path of the file.

						levelXML(new File(pathOri));
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX);
						txtArea.append("\n\n" + pathMessage);
					}
					break;
				}
				// CREATE DATABASE FROM XML FILE.
				case "creaBDdeXML": {
					if (order.length == 2) {
						pathOri = order[1]; // defines the path of the file.
						createDBfromXML(new File(pathOri));
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX);
						txtArea.append("\n\n" + pathMessage);
					}
					break;
				}
				// CREATE XML FROM DATABASE.
				case "creaXMLdeBD": {
					if (order.length == 2) {
						String dataBaseName = order[1]; // name of the database
						createXMLfromDB(dataBaseName);
						// txtArea.append("\nEste comando está bajo desarrollo.");
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX);
						txtArea.append("\n\n" + pathMessage);
					}
					break;
				}
				// DATABASE MODE.
				case "modoBD": {
					if (order.length == 1) {
						// Connects to a "empty" database, used for UPDATE queries.
						dbName = "";
						DatabaseConnector.getConnection();

						pathMessage = "MySQL>";
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");
					}
					break;
				}
				// HELP COMMAND.
				case "ayuda": {
					if (order.length == 1) {
						showHelp();
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");
					}
					break;
				}
				// CLEAN SCREEN COMMAND.
				case "limpiar": {
					if (order.length == 1) {
						txtArea.setText("");
						txtArea.append("\n\n" + pathMessage);

					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");
					}
					break;
				}
				// EXIT COMMAND.
				case "salir": {
					if (order.length == 1) {
						System.exit(0);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");
					}
					break;
				}
				// IF WRONG COMMAND IS WRITTEN.
				default:
					txtArea.append("\n\n" + "\"" + order[0] + "\" " + Strings.UNKNOWN_COMMAND);
					txtArea.append("\n" + Strings.SHOW_HELP);
					txtArea.append("\n\n" + pathMessage);
					break;
				}
				// ----------------------------------------
				// NORMAL MODE: ALL COMMANDS. *END*
				// ----------------------------------------
			} else {
				// ----------------------------------------
				// DATABASE MODE: ALL COMMANDS. *START*
				// ----------------------------------------
				// RETURN TO NORMAL MODE.
				if (order[0].equals("modoConsola")) {
					if (order.length == 1) {
						pathMessage = System.getProperty("user.dir") + ">";
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX);
						txtArea.append("\n\n" + pathMessage);
					}
				}
				// CONNECT AND SELECT DATABASE.
				// Sets the new name of the database, so all the queries can work properly.
				else if (order[0].equalsIgnoreCase("USE")) {

					// Removes ';' from the sentence, so the database name is correct
					if (order[1].contains(";")) {
						dbName = order[1].substring(0, order[1].length() - 1); // new name without ';'
					} else {
						dbName = order[1]; // name already without ';'
					}
					String dbNameAux = dbName; // saves the real dbName into an aux variable.

					Statement st;
					ResultSet rs;
					dbName = ""; // sets the dbName to empty for a "empty" connection.
					st = DatabaseConnector.getConnection().createStatement();
					rs = st.executeQuery("SHOW DATABASES;");
					rs = st.getResultSet();

					ArrayList<String> dbNamesList = new ArrayList<>();

					while (rs.next()) {
						dbNamesList.add(rs.getString("Database")); // stores all database names into a list.
					}

					rs.close();
					st.close();

					// If the database name exists, then restores the original database name and
					// executes the query. If not, shows an informative message.
					if (dbNamesList.contains(dbNameAux)) {
						dbName = dbNameAux;
						executeDBuse(commnd);
					} else {
						txtArea.append("\nEsta base de datos no existe.");
						txtArea.append("\n\n" + pathMessage);
					}

					txtArea.append("\n\n" + pathMessage);
				}
				// SHOW SELECTED DATABASE.
				else if (order[0].equals("BD")) {
					if (!dbName.equals("")) {
						txtArea.append("\n\n" + "La base de datos seleccionada actualmente es: --->" + dbName + "<---");
						txtArea.append("\n\n" + pathMessage);
					} else {
						txtArea.append("\n\n" + "Ninguna base de datos seleccionada.");
						txtArea.append("\n\n" + pathMessage);
					}
				}
				// EXECUTE SQL QUERY.
				else if (order[0].equalsIgnoreCase("SELECT") || order[0].equalsIgnoreCase("SHOW")) {
					String sqlQuery = commnd; // the entire sql command;
					String sqlCommand = order[0];

					executeDBquery(sqlQuery, sqlCommand);
					txtArea.append("\n" + pathMessage);
				}
				// EXECUTE SQL UPDATE.
				else if (order[0].equalsIgnoreCase("UPDATE") || order[0].equalsIgnoreCase("DELETE")
						|| order[0].equalsIgnoreCase("INSERT") || order[0].equalsIgnoreCase("ALTER")
						|| order[0].equalsIgnoreCase("DROP") || order[0].equalsIgnoreCase("CREATE")
						|| order[0].equalsIgnoreCase("RENAME")) {
					String sqlQuery = commnd;
					if (executeDBupdate(sqlQuery)) {
						txtArea.append("\nSentencia ejecutada exitosamente.");
					}

					txtArea.append("\n\n" + pathMessage);
				}
				// EXIT COMMAND.
				else if (order[0].equals("salir")) {
					if (order.length == 1) {
						System.exit(0);
					} else {
						txtArea.append("\n\n" + Strings.WRONG_SYNTAX + "\n");
					}
				}
				// IF WRONG COMMAND IS WRITTEN
				else {
					txtArea.append("\n\n" + "\"" + order[0] + "\" " + Strings.UNKNOWN_COMMAND);
					txtArea.append("\n\n" + pathMessage);
				}
			}
			// ----------------------------------------
			// DATABASE MODE: ALL COMMANDS. *END*
			// ----------------------------------------

			startingCommandPosition = txtArea.getCaretPosition();

		} catch (Exception e) {

		}
	}

	/**
	 * Formats a type long number to the desired format.
	 * 
	 * @param pattern the pattern to which the number will be formatted.
	 * @param value   the value to be formatted.
	 * @return The formatted number in String type.
	 */
	public static String customFormat(String pattern, long value) {
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		return myFormatter.format(value);
	}

	/**
	 * Obtains the current date and time and displays it in the following format:
	 * DD.MM.YYYY - HH:MM:SS.MS.
	 * 
	 * @return the the String array composed of date and time messages to be shown
	 *         in the console.
	 */
	public static String[] getCurrentTime() {
		Calendar cal = Calendar.getInstance();
		return new String[] {
				cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + "."
						+ cal.get(Calendar.MILLISECOND),
				" - ", cal.get(Calendar.DATE) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR) };
	}

	/**
	 * Obtains the file extension through a file name with it's extension. <br>
	 * Example: gets file.txt; returns .txt
	 * 
	 * @param fileWithExtension the name of the file with extension.
	 * @return The extension of the file, dot included.
	 */
	public static String getFileExtension(String fileWithExtension) {
		String extension = "";

		int dotPos = fileWithExtension.lastIndexOf('.');
		if (dotPos > 0) {
			extension = fileWithExtension.substring(dotPos);
		}
		return extension;
	}

	/**
	 * Checks if the file has an extension or a .file leading dot extension, then
	 * replaces the extension and removes it from the string. <br>
	 * <br>
	 * Source: https://www.baeldung.com/java-filename-without-extension
	 * 
	 * @param fileName            the name of the file, with extension.
	 * @param removeAllExtensions if true removes all the extensions from the file;
	 *                            if false removes only the last extension from a
	 *                            file name.
	 * @return The file without extension.
	 * 
	 */
	public static String removeFileExtension(String fileName, boolean removeAllExtensions) {
		if (fileName == null || fileName.isEmpty()) {
			return fileName;
		}
		String extPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");
		return fileName.replaceAll(extPattern, "");
	}

	//////////////////////////
	// GETTERS AND SETTERS //
	//////////////////////////

	/**
	 * @return The showXMLTags.
	 */
	public static boolean isShowXMLTags() {
		return showXMLTags;
	}

	/**
	 * @param showXMLTags the showXMLTags to set.
	 */
	public static void setShowXMLTags(boolean showXMLTags) {
		Console.showXMLTags = showXMLTags;
	}
}