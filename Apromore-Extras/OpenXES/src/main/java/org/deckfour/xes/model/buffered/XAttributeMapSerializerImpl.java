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
package org.deckfour.xes.model.buffered;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.XExtensionManager;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeCollection;
import org.deckfour.xes.model.XAttributeContainer;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.impl.XAttributeMapImpl;

/**
 * This class provides binary serialization of XAttributeMap instances, based on
 * the DataInput and DataOutput interfaces.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XAttributeMapSerializerImpl implements XAttributeMapSerializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.model.buffered.XAttributeMapSerializer#serialize(org
	 * .deckfour.xes.model.XAttributeMap, java.io.DataOutput)
	 */
	public void serialize(XAttributeMap map, DataOutput out) throws IOException {
		serialize(map.values(), out);
	}
	
	private void serialize(Collection<XAttribute> attributes, DataOutput out) throws IOException {
		out.writeInt(attributes.size());
		for (XAttribute attribute : attributes) {
			// encode attribute key
			out.writeUTF(attribute.getKey());
			// encode attribute extension
			XExtension extension = attribute.getExtension();
			if (extension == null) {
				out.writeInt(-1);
			} else {
				out.writeInt(XExtensionManager.instance().getIndex(extension));
			}
			// encode attribute type and value
			/*
			 * List and Container need to precede Literal, as they both extend Literal
			 * (for reasons of backwards compatibility).
			 */
			if (attribute instanceof XAttributeList) {
				out.writeByte(6);
			} else if (attribute instanceof XAttributeContainer) {
				out.writeByte(7);
			} else if (attribute instanceof XAttributeBoolean) {
				out.writeByte(0);
				out.writeBoolean(((XAttributeBoolean) attribute).getValue());
			} else if (attribute instanceof XAttributeContinuous) {
				out.writeByte(1);
				out.writeDouble(((XAttributeContinuous) attribute).getValue());
			} else if (attribute instanceof XAttributeDiscrete) {
				out.writeByte(2);
				out.writeLong(((XAttributeDiscrete) attribute).getValue());
			} else if (attribute instanceof XAttributeLiteral) {
				out.writeByte(3);
				out.writeUTF(((XAttributeLiteral) attribute).getValue());
			} else if (attribute instanceof XAttributeTimestamp) {
				out.writeByte(4);
				out.writeLong(((XAttributeTimestamp) attribute)
						.getValueMillis());
			} else if (attribute instanceof XAttributeID) {
				out.writeByte(5);
				XID.write(((XAttributeID) attribute).getValue(), out);
			} else {
				throw new AssertionError(
						"Unknown attribute type, cannot serialize!");
			}
			// recursive serialization of attribute map
			if (attribute instanceof XAttributeCollection) {
				Collection<XAttribute> childAttributes = ((XAttributeCollection) attribute).getCollection();
				serialize(childAttributes, out);
			} else {
				serialize(attribute.getAttributes(), out);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.model.buffered.XAttributeMapSerializer#deserialize(java
	 * .io.DataInput)
	 */
	public XAttributeMap deserialize(DataInput in) throws IOException {
		return deserialize(in, null);
	}
	
	private XAttributeMap deserialize(DataInput in, XAttribute parent) throws IOException {
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		int size = in.readInt();
		XAttributeMapImpl map = new XAttributeMapImpl(size * 2);
		for (int i = 0; i < size; i++) {
			// read attribute key
			String key = in.readUTF();
			// decode attribute extension
			int ext = in.readInt();
			XExtension extension = null;
			if (ext >= 0) {
				extension = XExtensionManager.instance().getByIndex(ext);
			}
			// assemble according to type and read value
			XAttribute attribute;
			byte type = in.readByte();
			if (type == 0) {
				boolean value = in.readBoolean();
				attribute = factory.createAttributeBoolean(key, value,
						extension);
			} else if (type == 1) {
				double value = in.readDouble();
				attribute = factory.createAttributeContinuous(key, value,
						extension);
			} else if (type == 2) {
				long value = in.readLong();
				attribute = factory.createAttributeDiscrete(key, value,
						extension);
			} else if (type == 3) {
				String value = in.readUTF();
				attribute = factory.createAttributeLiteral(key, value,
						extension);
			} else if (type == 4) {
				long value = in.readLong();
				attribute = factory.createAttributeTimestamp(key, value,
						extension);
			} else if (type == 5) {
				XID value = XID.read(in);
				attribute = factory.createAttributeID(key, value, extension);
			} else if (type == 6) {
				attribute = factory.createAttributeList(key, extension);
			} else if (type == 7) {
				attribute = factory.createAttributeContainer(key, extension);
			} else {
				throw new AssertionError(
						"Unknown attribute type, cannot deserialize!");
			}
			if (parent != null && parent instanceof XAttributeCollection) {
				((XAttributeCollection) parent).addToCollection(attribute);
			}
			// read meta-attribute map
			XAttributeMap metamap = deserialize(in, attribute);
			attribute.setAttributes(metamap);
			// add to map
			map.put(key, attribute);
		}
		return map;
	}

}
