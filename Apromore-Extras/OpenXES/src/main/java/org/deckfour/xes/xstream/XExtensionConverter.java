/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
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
package org.deckfour.xes.xstream;

import java.net.URI;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.XExtensionManager;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream converter for serializing extensions.
 * 
 * <p>For more information about XStream and its serialization
 * API, please see <a href="http://xstream.codehaus.org/">http://xstream.codehaus.org/</a>.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XExtensionConverter extends XConverter {

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	public void marshal(Object obj, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		XExtension extension = (XExtension)obj;
		writer.addAttribute("uri", extension.getUri().toString());
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		URI uri = URI.create(reader.getAttribute("uri"));
		return XExtensionManager.instance().getByUri(uri);
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class c) {
		return XExtension.class.isAssignableFrom(c);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.xstream.XConverter#registerAliases(com.thoughtworks.xstream.XStream)
	 */
	@Override
	public void registerAliases(XStream stream) {
		stream.aliasType("XExtension", XExtension.class);
	}
	
	

}
