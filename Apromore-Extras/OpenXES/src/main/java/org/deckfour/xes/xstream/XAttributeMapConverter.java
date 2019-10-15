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

import java.util.Collection;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeCollection;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.buffered.XAttributeMapBufferedImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream converter for serializing attribute maps.
 * 
 * <p>
 * For more information about XStream and its serialization API, please see <a
 * href="http://xstream.codehaus.org/">http://xstream.codehaus.org/</a>.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XAttributeMapConverter extends XConverter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 * com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@SuppressWarnings("unchecked")
	public void marshal(Object obj, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		XAttributeMap map = (XAttributeMap) obj;
		String lazy = "false";
		String buffered = "false";
		// register, whether lazy attribute map has been used,
		// and whether the implementation is buffered.
		if (map instanceof XAttributeMapLazyImpl) {
			lazy = "true";
			if (((XAttributeMapLazyImpl) map).getBackingStoreClass().equals(
					XAttributeMapBufferedImpl.class)) {
				buffered = "true";
			}
		} else if (map instanceof XAttributeMapBufferedImpl) {
			buffered = "true";
		}
		writer.addAttribute("lazy", lazy);
		writer.addAttribute("buffered", buffered);
		Collection<XAttribute> childAttributes = map.values();
		XAttribute parent = (XAttribute) context.get(XAttributeConverter.PARENT);
		if (parent instanceof XAttributeCollection) {
			childAttributes = ((XAttributeCollection) parent).getCollection();
		}
		for (XAttribute attribute : childAttributes) {
			writer.startNode("XAttribute");
			context.convertAnother(attribute,
					XesXStreamPersistency.attributeConverter);
			writer.endNode();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks
	 * .xstream.io.HierarchicalStreamReader,
	 * com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		XAttributeMap map = null;
		// restore correct type of attribute map, i.e., check for buffered
		// implementation, and correctly restore lazy implementation if
		// this had been used for serialization.
		boolean lazy = reader.getAttribute("lazy").equals("true");
		boolean buffered = reader.getAttribute("buffered").equals("true");
		if (lazy) {
			if (buffered) {
				map = new XAttributeMapLazyImpl<XAttributeMapBufferedImpl>(
						XAttributeMapBufferedImpl.class);
			} else {
				map = new XAttributeMapLazyImpl<XAttributeMapImpl>(
						XAttributeMapImpl.class);
			}
		} else if (buffered) {
			map = new XAttributeMapBufferedImpl();
		} else {
			map = new XAttributeMapImpl();
		}
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			XAttribute attribute = (XAttribute) context.convertAnother(map,
					XAttribute.class, XesXStreamPersistency.attributeConverter);
			map.put(attribute.getKey(), attribute);
			reader.moveUp();
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.
	 * lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class c) {
		return XAttributeMap.class.isAssignableFrom(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.xstream.XConverter#registerAliases(com.thoughtworks.
	 * xstream.XStream)
	 */
	@Override
	public void registerAliases(XStream stream) {
		stream.aliasType("XAttributeMap", XAttributeMap.class);
	}

}
