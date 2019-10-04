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
 * Parser for the compressed XES XML serialization.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class XesXmlGZIPParser extends XesXmlParser {

	/**
	 * Creates a new parser instance.
	 * 
	 * @param factory The XES model factory
	 * instance used to build the model from
	 * the serialization.
	 */
	public XesXmlGZIPParser(XFactory factory) {
		super(factory);
	}

	/**
	 * Creates a new parser instance, using the
	 * currently-set standard factory for building
	 * the model.
	 */
	public XesXmlGZIPParser() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XesXmlParser#author()
	 */
	@Override
	public String author() {
		return "Christian W. Günther";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XesXmlParser#canParse(java.io.File)
	 */
	@Override
	public boolean canParse(File file) {
		String filename = file.getName();
		return endsWithIgnoreCase(filename, ".xez") || endsWithIgnoreCase(filename, ".xes.gz");
//		String suffix = filename.substring(filename.length() - 3);
//		if(suffix.equalsIgnoreCase("xez")) {
//			return true;
//		} else {
//			suffix = filename.substring(filename.length() - 6);
//			return suffix.equalsIgnoreCase("xes.gz");
//		}
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XesXmlParser#description()
	 */
	@Override
	public String description() {
		return "Reads XES models from compressed XML serializations";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XesXmlParser#name()
	 */
	@Override
	public String name() {
		return "XES XML Compressed";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XesXmlParser#parse(java.io.InputStream)
	 */
	@Override
	public List<XLog> parse(InputStream is)
			throws Exception {
		is = new GZIPInputStream(new BufferedInputStream(is));
		return super.parse(is);
	}
	
	

}
