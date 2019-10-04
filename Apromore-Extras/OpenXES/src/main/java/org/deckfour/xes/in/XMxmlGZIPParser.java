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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XLog;

/**
 * Parser for the compressed MXML format for event logs (deprecated).
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XMxmlGZIPParser extends XMxmlParser {

	/**
	 * Creates a new MXML parser instance.
	 * 
	 * @param factory The factory to use for XES model
	 *  building.
	 */
	public XMxmlGZIPParser(XFactory factory) {
		super(factory);
	}

	/**
	 * Creates a new MXML parser instance, using the
	 * current standard factory for XES model building.
	 */
	public XMxmlGZIPParser() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XParser#author()
	 */
	@Override
	public String author() {
		return "Christian W. Günther";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XParser#canParse(java.io.File)
	 */
	@Override
	public boolean canParse(File file) {
		String filename = file.getName();
		return endsWithIgnoreCase(filename, ".mxml.gz");
//		String suffix = filename.substring(filename.length() - 7);
//		return suffix.equalsIgnoreCase("mxml.gz");
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XParser#description()
	 */
	@Override
	public String description() {
		return "Reads XES models from legacy compressed MXML serializations";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XParser#name()
	 */
	@Override
	public String name() {
		return "MXML Compressed";
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XMxmlParser#parse(java.io.InputStream)
	 */
	@Override
	public List<XLog> parse(InputStream is)
			throws Exception {
		is = new GZIPInputStream(new BufferedInputStream(is));
		return super.parse(is);
	}

}
