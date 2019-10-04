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

import com.thoughtworks.xstream.XStream;

/**
 * Global static class for registering all XES XStream converters in a
 * convenient method.
 * 
 * <p>
 * For more information about XStream and its serialization API, please see <a
 * href="http://xstream.codehaus.org/">http://xstream.codehaus.org/</a>.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XesXStreamPersistency {

	static final XConverter logConverter = new XLogConverter();
	static final XConverter traceConverter = new XTraceConverter();
	static final XConverter eventConverter = new XEventConverter();
	static final XConverter attributeMapConverter = new XAttributeMapConverter();
	static final XConverter attributeConverter = new XAttributeConverter();
	static final XConverter extensionConverter = new XExtensionConverter();

	/**
	 * All XES XStream converters available
	 */
	private static final XConverter converters[] = new XConverter[] {
			logConverter, traceConverter, eventConverter,
			attributeMapConverter, attributeConverter, extensionConverter };

	/**
	 * Registers all XES converters for XStream with the provided stream,
	 * registers corresponding aliases.
	 * 
	 * @param stream
	 *            The XStream instance to register with
	 */
	public static void register(XStream stream) {
		for (XConverter converter : converters) {
			stream.registerConverter(converter);
			converter.registerAliases(stream);
		}
	}

}
