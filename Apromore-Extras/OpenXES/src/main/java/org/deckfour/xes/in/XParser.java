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
package org.deckfour.xes.in;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.deckfour.xes.model.XLog;

/**
 * This abstract class describes a parser for reading
 * XES models from a given input stream.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public abstract class XParser {
	
	/**
	 * Returns the name of this parser or, more specifically,
	 * the name of the format it can process.
	 */
	public abstract String name();
	
	/**
	 * Returns a brief description of this parser.
	 */
	public abstract String description();
	
	/**
	 * Returns the name of the author of this parser.
	 */
	public abstract String author();
	
	/**
	 * Checks whether this parser can handle the given file.
	 * 
	 * @param file File to check against parser.
	 * @return Whether this parser can handle the given file.
	 */
	public abstract boolean canParse(File file);
	
	/**
	 * Parses the given input stream, and returns the
	 * XLog instances extracted.
	 * 
	 * @param is Stream to read XLog instances from.
	 * @return A list of XLog instances read from
	 * 	the given input stream. The number of read XLogs
	 * 	is at least one. If no XLog instance could be
	 * 	parsed, the parser is expected to throw an exception.
	 */
	public abstract List<XLog> parse(InputStream is) throws Exception;
	
	/**
	 * Parses the given file, and returns the XLog instances
	 * extracted. The file is first checked against this parser,
	 * to check whether it can be handled. If the parser cannot
	 * handle the given file, or the extraction itself fails,
	 * the parser should raise an <code>IOException</code>.
	 * 
	 * @param file The file to be parsed.
	 * @return List of XLog instances parsed from the given
	 * 	file.
	 * @throws Exception Raised in case the parser fails, or the
	 * 	given file cannot be processed.
	 */
	public List<XLog> parse(File file) throws Exception {
		if(canParse(file)) {
			InputStream is = new FileInputStream(file);
			return parse(is);
		} else {
			throw new IllegalArgumentException("Parser cannot handle this file!");
		}
	}
	
	/**
	 * toString() defaults to name().
	 */
	public String toString() {
		return name();
	}

	/**
	 * Returns whether the given file name ends (ignoring the case) with the given suffix.
	 * @param name The given file name.
	 * @param suffix The given suffix.
	 * @return Whether the given file name ends (ignoring the case) with the given suffix.
	 */
	protected boolean endsWithIgnoreCase(String name, String suffix) {
		if (name == null || suffix == null) {
			/*
			 * No name or no suffix. Return false.
			 */
			return false;
		}
		int i = name.length() - suffix.length();
		if (i < 0) {
			/*
			 * Suffix is longer than name, hence name cannot end with suffix.
			 */
			return false;
		}
		return name.substring(i).equalsIgnoreCase(suffix);
	}
}
