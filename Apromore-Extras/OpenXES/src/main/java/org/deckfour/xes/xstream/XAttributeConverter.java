/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
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
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeCollection;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.util.XAttributeUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XStream converter for serializing attributes.
 * 
 * <p>
 * For more information about XStream and its serialization API, please see <a
 * href="http://xstream.codehaus.org/">http://xstream.codehaus.org/</a>.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XAttributeConverter extends XConverter {

	protected static final Integer PARENT = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 * com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	public void marshal(Object obj, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		XAttribute attribute = (XAttribute) obj;
		writer.addAttribute("key", attribute.getKey());
		writer.addAttribute("type", XAttributeUtils.getTypeString(attribute));
		String value = attribute.toString();
		if (value == null) {
			throw new AssertionError("Attribute value must not be null");
		}
		writer.addAttribute("value", value);
		if (attribute.getExtension() != null) {
			writer.addAttribute("extension", attribute.getExtension().getUri()
					.toString());
		}
		// marshal meta-attributes
		if (attribute.getAttributes().size() > 0) {
			writer.startNode("XAttributeMap");
			XAttribute oldParent = (XAttribute) context.get(PARENT);
			context.put(PARENT, attribute); 
			context.convertAnother(attribute.getAttributes(),
					XesXStreamPersistency.attributeMapConverter);
			context.put(PARENT, oldParent);
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
		String key = reader.getAttribute("key");
		String type = reader.getAttribute("type");
		String value = reader.getAttribute("value");
		XExtension extension = null;
		String extensionString = reader.getAttribute("extension");
		if (extensionString != null && extensionString.length() > 0) {
			URI uri = URI.create(extensionString);
			extension = XExtensionManager.instance().getByUri(uri);
		}
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XAttribute attribute = XAttributeUtils.composeAttribute(factory, key,
				value, type, extension);
		XAttribute parent = (XAttribute) context.get(PARENT);
		if (parent != null && parent instanceof XAttributeCollection) {
			((XAttributeCollection) parent).addToCollection(attribute);
		}
		if (reader.hasMoreChildren()) {
			reader.moveDown();
			Object oldParent = context.get(PARENT);
			context.put(PARENT, attribute);
			XAttributeMap metaAttributes = (XAttributeMap) context
					.convertAnother(attribute, XAttributeMap.class,
							XesXStreamPersistency.attributeMapConverter);
			context.put(PARENT, oldParent);
			reader.moveUp();
			attribute.setAttributes(metaAttributes);
		}
		return attribute;
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
		return XAttribute.class.isAssignableFrom(c);
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
		stream.aliasType("XAttribute", XAttribute.class);
	}
}
