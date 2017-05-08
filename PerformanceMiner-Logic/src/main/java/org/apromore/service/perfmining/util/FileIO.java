package org.apromore.service.perfmining.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author R.P. Jagadeesh Chandra 'JC' Bose
 * @date 14 July 2010
 * @since 01 July 2010
 * @version 1.0
 * @email j.c.b.rantham.prabhakara@tue.nl
 * @copyright R.P. Jagadeesh Chandra 'JC' Bose Architecture of Information
 *            Systems Group (AIS) Department of Mathematics and Computer Science
 *            University of Technology, Eindhoven, The Netherlands
 */

public class FileIO {

	public Object write;

	public FileIO() {

	}

	public void copy(String fromFileName, String toFileName) throws IOException {
		File fromFile = new File(fromFileName);
		File toFile = new File(toFileName);

		if (!fromFile.exists()) {
			throw new IOException("FileCopy: " + "no such source file: " + fromFileName);
		}
		if (!fromFile.isFile()) {
			throw new IOException("FileCopy: " + "can't copy directory: " + fromFileName);
		}
		if (!fromFile.canRead()) {
			throw new IOException("FileCopy: " + "source file is unreadable: " + fromFileName);
		}

		if (toFile.isDirectory()) {
			toFile = new File(toFile, fromFile.getName());
		}

		if (toFile.exists()) {
			if (!toFile.canWrite()) {
				throw new IOException("FileCopy: " + "destination file is unwriteable: " + toFileName);
				// System.out.print("Overwrite existing file " + toFile.getName()
				// + "? (Y/N): ");
				// System.out.flush();
				// BufferedReader in = new BufferedReader(new InputStreamReader(
				// System.in));
				// String response = in.readLine();
				// if (!response.equals("Y") && !response.equals("y"))
				// throw new IOException("FileCopy: "
				// + "existing file was not overwritten.");
			}
		} else {
			String parent = toFile.getParent();
			// if (parent == null)
			// { parent = System.getProperty("user.dir");
			// System.out.println(parent);}
			File dir = new File(parent);
			if (!dir.exists())

			{
				dir.mkdirs();

			}
			// throw new IOException("FileCopy: "
			// + "destination directory doesn't exist: " + parent);
			if (dir.isFile()) {
				throw new IOException("FileCopy: " + "destination is not a directory: " + parent);
			}
			if (!dir.canWrite()) {
				throw new IOException("FileCopy: " + "destination directory is unwriteable: " + parent);
			}
		}

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytesRead);
			}
		} finally {
			if (from != null) {
				try {
					from.close();
				} catch (IOException e) {
					;
				}
			}
			if (to != null) {
				try {
					to.close();
				} catch (IOException e) {
					;
				}
			}
		}
	}

	// * I created it.
	public String readStringStream(String dir, String filename) {
		String path = dir + "\\" + filename;

		BufferedReader reader;
		String charStream = "";
		String currentLine;

		int numOfLine = 0;

		try {
			reader = new BufferedReader(new FileReader(path));
			while ((currentLine = reader.readLine()) != null) {
				if (numOfLine == 0) {
					charStream = charStream + currentLine.trim();
				} else {
					charStream = charStream + currentLine.trim() + "\n";
				}
				numOfLine++;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + path);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + path);
			e.printStackTrace();
		}
		System.out.println(charStream);
		return charStream;
	}

	public HashSet<String> readFileAsSet(String inputDir, String fileName) {
		HashSet<String> stringSet = new HashSet<String>();
		BufferedReader reader;

		String currentLine;
		try {
			reader = new BufferedReader(new FileReader(inputDir + "\\" + fileName));
			while ((currentLine = reader.readLine()) != null) {
				stringSet.add(currentLine.trim());

			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		}

		return stringSet;
	}

	public ArrayList<String> readFile(String inputDir, String fileName) {
		ArrayList<String> stringList = new ArrayList<String>();
		BufferedReader reader;

		String currentLine;
		try {
			reader = new BufferedReader(new FileReader(inputDir + "\\" + fileName));
			while ((currentLine = reader.readLine()) != null) {
				stringList.add(currentLine.trim());
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		}

		return stringList;
	}

	public ArrayList<TreeSet<String>> readSetFromFile(String inputDir, String fileName) {
		ArrayList<TreeSet<String>> listSets = new ArrayList<TreeSet<String>>();
		BufferedReader reader;

		String currentLine;
		String[] currentLineSplit;
		TreeSet<String> stringSet;
		try {
			reader = new BufferedReader(new FileReader(inputDir + "\\" + fileName));
			while ((currentLine = reader.readLine()) != null) {
				currentLine = currentLine.replaceAll("\\[", "");
				currentLine = currentLine.replaceAll("\\]", "");
				currentLineSplit = currentLine.split(",");
				stringSet = new TreeSet<String>();
				for (String str : currentLineSplit) {
					stringSet.add(str.trim());
				}

				listSets.add(stringSet);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		}

		return listSets;
	}

	public HashMap<String, String> readMapFromFile(String inputDir, String fileName, String delim) {
		HashMap<String, String> map = new HashMap<String, String>();

		BufferedReader reader;

		String currentLine;
		String[] currentLineSplit;
		try {
			reader = new BufferedReader(new FileReader(inputDir + "\\" + fileName));
			while ((currentLine = reader.readLine()) != null) {
				currentLineSplit = currentLine.split(delim);
				if (currentLineSplit.length != 2) {

					System.out.println("Something wrong in the format of the map file");
					System.exit(0);
				}

				map.put(currentLineSplit[0].trim(), currentLineSplit[1].trim());
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		}

		return map;
	}

	public HashMap<String, Integer> readMapStringIntegerFromFile(String inputDir, String fileName, String delim) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		BufferedReader reader;

		String currentLine;
		String[] currentLineSplit;
		try {
			reader = new BufferedReader(new FileReader(inputDir + "\\" + fileName));
			while ((currentLine = reader.readLine()) != null) {
				// System.out.println(currentLine);
				currentLineSplit = currentLine.split(delim);
				if (currentLineSplit.length != 2) {
					System.out.println("Something wrong in the format of the map file");
					System.exit(0);
				}

				map.put(currentLineSplit[0].trim(), new Integer(currentLineSplit[1].trim()));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		}

		return map;
	}

	// /**
	// * @param inputDir
	// * @param fileName
	// * @param delim1 ^
	// * @param delim2 ,
	// * @return
	// *
	// * It is used to read the feature sets binary-combination rules from txt
	// * file. The rules are kept in the format:
	// *
	// * individual event^tandem repeat,maximal repeat
	// *
	// * which has the meaning individual event can be mixed with either one of
	// * the feature sets out of {tandem repeat,maximal repeat}, i.e. the
	// possible
	// * combinations are {individual event,tandem repeat}, and {individual
	// event,
	// * maximal repeat}.
	// */
	// public Set<Set<String>> readMapStringSetFromFile(String inputDir,
	// String fileName, String delim1, String delim2) {
	// Set<Set<String>> set = new HashSet<Set<String>>();
	// BufferedReader reader;
	//
	// String currentLine;
	// String[] currentLineSplit;
	// try {
	// reader = new BufferedReader(new FileReader(inputDir + "\\"
	// + fileName));
	// while ((currentLine = reader.readLine()) != null) {
	// // System.out.println(currentLine);
	// currentLineSplit = currentLine.split(delim1);
	// if (currentLineSplit.length != 2) {
	// System.out
	// .println("Something wrong in the format of the map file");
	// System.exit(0);
	// }
	//
	// String currentLineSplit2[] = currentLineSplit[1].split(",");
	// for (int i = 0; i < currentLineSplit2.length; i++) {
	// Set<String> oneSet = new HashSet<String>();
	// oneSet.add(currentLineSplit[0].trim());
	// oneSet.add(currentLineSplit2[i].trim());
	// set.add(oneSet);
	// }
	//
	// }
	// reader.close();
	// } catch (FileNotFoundException e) {
	// System.err.println("File Not Found: " + inputDir + "\\" + fileName);
	// e.printStackTrace();
	// } catch (IOException e) {
	// System.err.println("IO Exception while Reading: " + inputDir + "\\"
	// + fileName);
	// e.printStackTrace();
	// }
	//
	// return set;
	// }

	/**
	 * @param inputDir
	 * @param fileName
	 * @param delim1
	 *            ^
	 * @param delim2
	 *            ,
	 * @param delim3
	 *            #
	 * @return It is used to read the feature sets non-binary-combination rules
	 *         from txt file. The rules are kept in the format:
	 * 
	 *         Individual Event^Maximal Repeat, Tandem Repeat#Super Maximal
	 *         Repeat, Tandem Repeat#Super Maximal Repeat, Tandem Repeat
	 * 
	 *         which has the meaning individual event can be mixed with {Maximal
	 *         Repeat, Tandem Repeat},{Super Maximal Repeat, Tandem Repeat}, and
	 *         { Maximal Repeat, Tandem Repeat}, i.e. the possible combinations
	 *         are {Individual Event,Maximal Repeat, Tandem Repeat},{Individual
	 *         Event, Super Maximal Repeat, Tandem Repeat}, and {Individual
	 *         Event, Maximal Repeat, Tandem Repeat}
	 */
	public Set<Set<String>> readMapStringSetFromFile(String inputDir, String fileName, String delim1, String delim2,
			String delim3) {

		Set<Set<String>> set = new HashSet<Set<String>>();
		BufferedReader reader;

		String currentLine;
		String[] currentLineSplit;
		try {
			reader = new BufferedReader(new FileReader(inputDir + "\\" + fileName));
			while ((currentLine = reader.readLine()) != null) {
				// System.out.println(currentLine);
				currentLineSplit = currentLine.split(delim1);
				if (currentLineSplit.length != 2) {
					System.out.println("Something wrong in the format of the map file");
					System.exit(0);
				}

				String currentLineSplit2[] = currentLineSplit[1].split("#");
				for (int i = 0; i < currentLineSplit2.length; i++) {
					Set<String> oneSet = new HashSet<String>();
					oneSet.add(currentLineSplit[0].trim());

					String currentLineSplit3[] = currentLineSplit2[i].split(",");
					for (int j = 0; j < currentLineSplit3.length; j++) {
						oneSet.add(currentLineSplit3[j].trim());
					}
					set.add(oneSet);

				}

			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		}

		return set;
	}

	public HashMap<TreeSet<String>, Integer> readMapSetIntegerFromFile(String inputDir, String fileName, String delim) {
		HashMap<TreeSet<String>, Integer> map = new HashMap<TreeSet<String>, Integer>();

		BufferedReader reader;

		String currentLine, currentSetLine;
		String[] currentLineSplit, currentSetLineSplit;
		TreeSet<String> setStrings;
		try {
			reader = new BufferedReader(new FileReader(inputDir + "\\" + fileName));
			while ((currentLine = reader.readLine()) != null) {
				currentLineSplit = currentLine.split(delim);
				if (currentLineSplit.length != 2) {
					System.out.println("Something wrong in the format of the map file");
					System.exit(0);
				}
				setStrings = new TreeSet<String>();
				currentSetLine = currentLineSplit[0].trim();
				currentSetLine = currentSetLine.replaceAll("\\[", "");
				currentSetLine = currentSetLine.replaceAll("\\]", "");
				currentSetLineSplit = currentSetLine.split(",");
				setStrings = new TreeSet<String>();
				for (String str : currentSetLineSplit) {
					setStrings.add(str.trim());
				}

				map.put(setStrings, new Integer(currentLineSplit[1].trim()));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		}

		return map;
	}

	public HashMap<TreeSet<String>, TreeSet<String>> readEquivalenceClassMapFromFile(String inputDir, String fileName,
			String delim) {
		HashMap<TreeSet<String>, TreeSet<String>> setEquivalenceClassMap = new HashMap<TreeSet<String>, TreeSet<String>>();

		BufferedReader reader;

		String currentLine;
		String[] currentLineSplit, currentSetLineSplit;
		String currentSetLine;
		TreeSet<String> stringKeySet, stringEquivalenceClassSet;
		try {
			reader = new BufferedReader(new FileReader(inputDir + "\\" + fileName));
			while ((currentLine = reader.readLine()) != null) {
				currentLineSplit = currentLine.split(delim);
				if (currentLineSplit.length != 2) {
					System.out.println("Something wrong in the format of the map file");
					System.exit(0);
				}

				currentSetLine = currentLineSplit[0];
				currentSetLine = currentSetLine.replaceAll("\\[", "");
				currentSetLine = currentSetLine.replaceAll("\\]", "");

				currentSetLineSplit = currentSetLine.split(",");
				stringKeySet = new TreeSet<String>();
				for (String str : currentSetLineSplit) {
					stringKeySet.add(str.trim());
				}

				currentSetLine = currentLineSplit[1];
				currentSetLine = currentSetLine.replaceAll("\\[", "");
				currentSetLine = currentSetLine.replaceAll("\\]", "");

				currentSetLineSplit = currentSetLine.split(",");
				stringEquivalenceClassSet = new TreeSet<String>();
				for (String str : currentSetLineSplit) {
					stringEquivalenceClassSet.add(str.trim());
				}

				setEquivalenceClassMap.put(stringKeySet, stringEquivalenceClassSet);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception while Reading: " + inputDir + "\\" + fileName);
			e.printStackTrace();
		}

		return setEquivalenceClassMap;
	}

	public <T, E> void writeToFile(String dir, String fileName, Map<T, E> map, String delim) {
		FileOutputStream fos;
		PrintStream ps;

		if (isDirExists(dir)) {
			try {
				fos = new FileOutputStream(dir + "\\" + fileName);
				ps = new PrintStream(fos);

				if (map != null) {
					for (T t : map.keySet()) {
						if (map.containsKey(t)) {
							ps.println(t.toString() + " " + delim + " " + map.get(t).toString());
						}
					}
				}
				ps.close();
				fos.close();
			} catch (FileNotFoundException e) {
				System.err.println("File Not Found Exception while creating file: " + dir + "\\" + fileName);
				System.exit(0);
			} catch (IOException e) {
				System.err.println("IO Exception while writing file: " + dir + "\\" + fileName);
				System.exit(0);
			}
		} else {
			System.err.println("Can't create Directory: " + dir);
		}
	}

	public <T> void writeToFile(String dir, String fileName, Collection<T> collection) {
		FileOutputStream fos;
		PrintStream ps;

		if (isDirExists(dir)) {
			try {
				fos = new FileOutputStream(dir + "\\" + fileName);
				ps = new PrintStream(fos);

				for (T t : collection) {
					ps.println(t.toString());
				}

				ps.close();
				fos.close();
			} catch (FileNotFoundException e) {
				System.err.println("File Not Found Exception while creating file: " + dir + "\\" + fileName);
				System.exit(0);
			} catch (IOException e) {
				System.err.println("IO Exception while writing file: " + dir + "\\" + fileName);
				System.exit(0);
			}
		} else {
			System.err.println("Can't create Directory: " + dir);
		}
	}

	public <T> void writeToFile(String dir, String fileName, T[] arrayT) {
		FileOutputStream fos;
		PrintStream ps;

		if (isDirExists(dir)) {
			try {
				fos = new FileOutputStream(dir + "\\" + fileName);
				ps = new PrintStream(fos);

				for (T t : arrayT) {
					ps.println(t.toString());
				}

				ps.close();
				fos.close();
			} catch (FileNotFoundException e) {
				System.err.println("File Not Found Exception while creating file: " + dir + "\\" + fileName);
				System.exit(0);
			} catch (IOException e) {
				System.err.println("IO Exception while writing file: " + dir + "\\" + fileName);
				System.exit(0);
			}
		} else {
			System.err.println("Can't create Directory: " + dir);
		}
	}

	public <T> void writeToFile(String dir, String fileName, T t) {
		FileOutputStream fos;
		PrintStream ps;

		if (isDirExists(dir)) {
			try {
				fos = new FileOutputStream(dir + "\\" + fileName);
				ps = new PrintStream(fos);

				ps.println(t.toString());

				ps.close();
				fos.close();
			} catch (FileNotFoundException e) {
				System.err.println("File Not Found Exception while creating file: " + dir + "\\" + fileName);
				System.exit(0);
			} catch (IOException e) {
				System.err.println("IO Exception while writing file: " + dir + "\\" + fileName);
				System.exit(0);
			}
		} else {
			System.err.println("Can't create Directory: " + dir);
		}
	}

	public void createDir(String dir) {
		if (!(new File(dir)).exists()) {
			boolean success = new File(dir).mkdirs();
			if (!success) {
				System.out.println("Cannot create directory: " + dir);
				System.exit(0);
			}
		}
	}

	private boolean isDirExists(String dir) {
		if (!(new File(dir)).exists()) {
			return new File(dir).mkdirs();
		} else {
			return true;
		}
	}
}
