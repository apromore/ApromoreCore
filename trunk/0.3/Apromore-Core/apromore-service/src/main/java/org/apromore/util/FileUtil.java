package org.apromore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class FileUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
	
	public static void createFile(String fileName, String data) {
		File file = new File(fileName);
		if (file.exists()) {
			LOGGER.info("File: " + fileName + " already exists.");
			return;
		}
		
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public static void appendToFile(String fileName, String data) {
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter writer = new FileWriter(file, true);
			writer.append(data);
			writer.flush();
			writer.close();
		} catch (IOException e) {
            LOGGER.error("Failed to append to file: " + fileName, e);
		}
	}
	
	public static void createFileWithOverwrite(String fileName, String data) {
		File file = new File(fileName);
		
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.flush();
			writer.close();
		} catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
		}
	}

	public static void copyFile(String source, String target) {
		try {
			File f1 = new File(source);
			File f2 = new File(target);
			if (!f2.exists()) f2.createNewFile(); 
			InputStream in = new FileInputStream(f1);
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
		} catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
		}
	}

	public static void clearFilesInFolder(File d) {
		if (d.isDirectory()) {
			File[] fs = d.listFiles();
			for (File f: fs) {
				if (f.isDirectory()) clearFilesInFolder(f);
				else f.delete();
			}
		}
	}
	
//	public static void displayModelSizes(String d, String resultsFile) {
//		File rf = new File(resultsFile);
//		LOGGER.debug("Results file: " + rf.getAbsolutePath());
//		if (rf.exists()) {
//            LOGGER.error("Results file " + rf.getAbsolutePath() + " already exists. Provide a new file name.");
//			return;
//		}
//		ResultsWriter rw = new ResultsWriter(rf.getAbsolutePath());
//
//		File dir = new File(d);
//		if (!dir.isDirectory()) return;
//
//		File[] fs = dir.listFiles();
//		for (File f: fs) {
//			EPCDeserializer desrializer = new EPCDeserializer();
//			ProcessModelGraph g = desrializer.deserialize(f.getAbsolutePath());
//			String data = f.getName() + "," + g.getVertices().size() + "," + g.getEdges().size();
//			rw.report(data);
//			System.out.println(data);
// 		}
//	}
	
//	public static void displayModelSizes(String d) {
//		File dir = new File(d);
//		if (!dir.isDirectory()) return;
//
//		File[] fs = dir.listFiles();
//		for (File f: fs) {
//			EPCDeserializer desrializer = new EPCDeserializer();
//			ProcessModelGraph g = desrializer.deserialize(f.getAbsolutePath());
//			String data = f.getName() + "," + g.getVertices().size() + "," + g.getEdges().size();
//			System.out.println(data);
// 		}
//	}
}
