package org.oryxeditor.server.diagram.test.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class for reading from and writing to file
 * @author philipp.maschke
 *
 */
public class FileIO {

	/**
	 * Reads the contents of the file and returns it as a string.
	 * @param file
	 * @return file's contents as a string or empty string if file not found/could not be read
	 */
	public static String readWholeFile(File file) {
		return readWholeFile(file.getAbsolutePath());
	}
	/**
	 * Reads the contents of the file and returns it as a string.
	 * @param fileName
	 * @return fileName's contents as a string or empty string if file not found/could not be read
	 */
	public static String readWholeFile(String fileName) {
		StringBuffer contents = new StringBuffer();

		try {
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return contents.toString();
	}

	
	/**
	 * Writes content into a file.
	 * Will create a new file or overwrite an existing one.
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	public static void writeToFile(File file, String content) throws IOException {
		writeToFile(file.getAbsolutePath(), content);
	}
	/**
	 * Writes content into a file defined by fileName.
	 * Will create a new file or overwrite an existing one.
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	public static void writeToFile(String fileName, String content) throws IOException {
		BufferedWriter out = null;
		
		try{
			out = new BufferedWriter(new FileWriter(fileName));
			out.write(content);
		} finally{
			if(out != null) {
				out.close();
			}
		}
	}
}
