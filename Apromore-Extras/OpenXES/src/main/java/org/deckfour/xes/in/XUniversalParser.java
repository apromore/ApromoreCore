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
import java.util.Collection;

import org.deckfour.xes.model.XLog;

/**
 * This class implements a universal parser, using the parser
 * registry to find an appropriate parser for extracting an
 * XES model from any given file. May be used as a convenience
 * method for applications.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class XUniversalParser {

	/**
	 * Checks whether the given file can be parsed by any parser.
	 */
	public boolean canParse(File file) {
		for(XParser parser : XParserRegistry.instance().getAvailable()) {
			if(parser.canParse(file)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Attempts to parse a collection of XES models
	 * from the given file, using all available parsers.
	 */
	public Collection<XLog> parse(File file) throws Exception {
		Collection<XLog> result = null;
		for(XParser parser : XParserRegistry.instance().getAvailable()) {
			if(parser.canParse(file)) {
				try {
					result = parser.parse(file);
					return result;
				} catch(Exception e) {
					// ignore and move on
				}
			}
		}
		throw new Exception("No suitable parser could be found!");
	}

}
