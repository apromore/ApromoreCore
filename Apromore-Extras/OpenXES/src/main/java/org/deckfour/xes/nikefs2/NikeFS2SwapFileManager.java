/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2009 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.nikefs2;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * This class provides static facilities to acquire and manage temporary swap files.
 * It is ensured, that acquired swap files will be removed after they are no longer
 * used on a best-effort basis. On Unix systems, this is usually guaranteed after
 * JVM shutdown. Especially on the Win32 platform, this class implements a workaround
 * which will guarantee the deletion of swap files upon the next startup.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class NikeFS2SwapFileManager {
	
	/**
	 * System-wide temporary directory, used for storings swap files.
	 */
	private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));
	/**
	 * Swap directory used for this session.
	 */
	private static File SWAP_DIR = null;
	/**
	 * Prefix string for swap directory names.
	 */
	private static final String SWAP_DIR_PREFIX = "NIKEFS_SWAP_";
	/**
	 * Suffix string for swap directory names.
	 */
	private static final String SWAP_DIR_SUFFIX = "_SWAPDIR";
	/**
	 * Suffix string for swap directory lock file names.
	 */
	private static final String LOCK_FILE_SUFFIX = ".LOCK";
	/**
	 * Prefix string for swap file names.
	 */
	private static final String SWAP_FILE_PREFIX = "NIKEFS_SWAP_";
	/**
	 * Suffix string for swap file names.
	 */
	private static final String SWAP_FILE_SUFFIX = ".SWAP2";
	
	static {
		// initialization: clean leftover swap directories and files
		cleanup();
	}

	
	/**
	 * Creates a new, empty swap file, ready for use. It is guaranteed
	 * that this swap file will be removed from the system either on 
	 * JVM shutdown (Unix platforms) or on subsequent use of this class
	 * (Win32 platforms).
	 * @return A new swap file.
	 * @throws IOException
	 */
	public static synchronized File createSwapFile() throws IOException {
		File swapFile = createSwapFile(SWAP_FILE_PREFIX, SWAP_FILE_SUFFIX);
		// works reliably only on Unix platforms!
		swapFile.deleteOnExit();
		return swapFile;
	}
	
	/**
	 * Creates a new, empty swap file, ready for use. It is guaranteed
	 * that this swap file will be removed from the system either on 
	 * JVM shutdown (Unix platforms) or on subsequent use of this class
	 * (Win32 platforms).
	 * @param prefix Prefix to be used for this swap file.
	 * @param suffix Suffix to be used for this swap file.
	 * @return A new swap file.
	 * @throws IOException
	 */
	public static synchronized File createSwapFile(String prefix, String suffix) throws IOException {
		File swapDir = getSwapDir();
		File tmpFile = File.createTempFile(prefix, suffix, swapDir);
		return tmpFile;
	}
	
	/**
	 * Retrieves a file handle on the swap directory of this session.
	 * @return The swap directory of this session.
	 * @throws IOException
	 */
	private static synchronized File getSwapDir() throws IOException {
		if(SWAP_DIR == null) {
			// create swap directory for this instance
			File swapDir = File.createTempFile(SWAP_DIR_PREFIX, SWAP_DIR_SUFFIX, TMP_DIR);
			// delete if created
			swapDir.delete();
			// create lock file
			File lockFile = new File(TMP_DIR, swapDir.getName() + LOCK_FILE_SUFFIX);
			lockFile.createNewFile();
			// delete lock file on exit, to make swap directory 
			// eligible for cleanup.
			lockFile.deleteOnExit();
			// make swap directory
			swapDir.mkdirs();
			// works reliably only on Unix platforms!
			swapDir.deleteOnExit();
			SWAP_DIR = swapDir;
		}
		return SWAP_DIR;
	}
	
	/**
	 * Cleans up stale swap files and directories, left over from
	 * previous sessions. Only swap directories for which no lock
	 * file exists will be removed.
	 */
	private static synchronized void cleanup() {
		int cleanedDirs = 0;
		int cleanedFiles = 0;
		// retrieve leftover swap directories
		File[] swapDirs = TMP_DIR.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(SWAP_DIR_SUFFIX);
			}
		});
		for(File swapDir : swapDirs) {
			// check if swap directory is locked
			File lockFile = new File(TMP_DIR, swapDir.getName() + LOCK_FILE_SUFFIX);
			if(lockFile.exists() == false) {
				// no lock file, recursively delete leftover swap dir
				cleanedDirs++;
				cleanedFiles += deleteRecursively(swapDir);
			}
		}
		System.out.println("NikeFS2: cleaned up " + cleanedFiles + 
				" stale swap files (from " + cleanedDirs + " sessions).");
	}
	
	/**
	 * Deletes the given directory recursively, i.e., bottom-up.
	 * @param directory Directory to be deleted.
	 * @return The number of files deleted.
	 */
	private static synchronized int deleteRecursively(File directory) {
		int deleted = 0;
		if(directory.isDirectory()) {
			File[] contents = directory.listFiles();
			for(File file : contents) {
				deleted += deleteRecursively(file);
			}
		} else {
			deleted++;
		}
		directory.delete();
		return deleted;
	}

}
