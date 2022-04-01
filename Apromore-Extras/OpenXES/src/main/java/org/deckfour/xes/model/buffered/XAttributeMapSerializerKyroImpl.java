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
package org.deckfour.xes.model.buffered;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.XExtensionManager;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeMapImpl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

public class XAttributeMapSerializerKyroImpl extends Serializer<XAttributeMapImpl> {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.deckfour.xes.model.buffered.XAttributeMapSerializer#serialize(org
     * .deckfour.xes.model.XAttributeMap, java.io.DataOutput)
     */
    @Override
    public void write(Kryo kryo, Output output, XAttributeMapImpl map) {
        System.out.println("Kyro Serializer write the number of attributes = ");
        write(kryo, output, map.values());
    }

    /**
     * @param kryo
     * @param output
     * @param attributes
     */
    public void write(Kryo kryo, Output output, Collection<XAttribute> attributes) {

        output.writeInt(attributes.size());
        System.out.println("Kyro Serializer write the number of attributes = " + attributes.size());
        for (XAttribute attribute : attributes) {
            // encode attribute key
            output.writeString(attribute.getKey());
            // encode attribute extension
            XExtension extension = attribute.getExtension();
            if (extension == null) {
                output.writeInt(-1);
            } else {
                output.writeInt(XExtensionManager.instance().getIndex(extension));
            }
            // encode attribute type and value
            /*
             * List and Container need to precede Literal, as they both extend Literal
             * (for reasons of backwards compatibility).
             */
            if (attribute instanceof XAttributeList) {
                output.writeByte(6);
            } else if (attribute instanceof XAttributeContainer) {
                output.writeByte(7);
            } else if (attribute instanceof XAttributeBoolean) {
                output.writeByte(0);
                output.writeBoolean(((XAttributeBoolean) attribute).getValue());
            } else if (attribute instanceof XAttributeContinuous) {
                output.writeByte(1);
                output.writeDouble(((XAttributeContinuous) attribute).getValue());
            } else if (attribute instanceof XAttributeDiscrete) {
                output.writeByte(2);
                output.writeLong(((XAttributeDiscrete) attribute).getValue());
            } else if (attribute instanceof XAttributeLiteral) {
                output.writeByte(3);
                output.writeString(((XAttributeLiteral) attribute).getValue());
            } else if (attribute instanceof XAttributeTimestamp) {
                output.writeByte(4);
                output.writeLong(((XAttributeTimestamp) attribute)
                        .getValueMillis());
            } else if (attribute instanceof XAttributeID) {
                output.writeByte(5);
                try {
                    XID.write(((XAttributeID) attribute).getValue(), (DataOutput) output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new AssertionError(
                        "Unknown attribute type, cannot serialize!");
            }
            // recursive serialization of attribute map
            if (attribute instanceof XAttributeCollection) {
                Collection<XAttribute> childAttributes = ((XAttributeCollection) attribute).getCollection();
                write(kryo, output, childAttributes);
            } else {
                write(kryo, output, (XAttributeMapImpl)attribute.getAttributes());
            }
        }
    }

    @Override
    public XAttributeMapImpl read(Kryo kryo, Input input, Class<XAttributeMapImpl> type) {
        return read(kryo, input, type, null);
    }

    private XAttributeMapImpl read(Kryo kryo, Input in, Class<XAttributeMapImpl> ObjectType, XAttribute parent) {
        XFactory factory = XFactoryRegistry.instance().currentDefault();
        int size = in.readInt();
        System.out.println("Kyro Serializer read the number of attributes = " + size);
        XAttributeMapImpl map = new XAttributeMapImpl(size * 2);
        for (int i = 0; i < size; i++) {
            // read attribute key
            String key = in.readString();
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
                String value = in.readString();
                attribute = factory.createAttributeLiteral(key, value,
                        extension);
            } else if (type == 4) {
                long value = in.readLong();
                attribute = factory.createAttributeTimestamp(key, value,
                        extension);
            } else if (type == 5) {
                XID value = null;
                try {
                    value = XID.read((DataInput)in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            XAttributeMap metamap = read(kryo, in, ObjectType, attribute);
            attribute.setAttributes(metamap);
            // add to map
            map.put(key, attribute);
        }
        return map;
    }
}
