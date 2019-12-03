package org.apromore.cache.ehcache;

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
import org.deckfour.xes.model.impl.XLogImpl;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

public class XAttributeMapSerializer extends Serializer<XAttributeMap> {

    public void write(Kryo kryo, Output out, XAttributeMap xAttributeMap) {
        try {
            write(kryo, out, xAttributeMap.values());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write (Kryo kryo, Output out, Collection<XAttribute> attributes)  throws IOException {

//        Collection<XAttribute> attributes = xAttributeMap.values();

        out.writeInt(attributes.size());
        for (XAttribute attribute : attributes) {
            // encode attribute key
            out.writeString(attribute.getKey());
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
                out.writeString(((XAttributeLiteral) attribute).getValue());
            } else if (attribute instanceof XAttributeTimestamp) {
                out.writeByte(4);
                out.writeLong(((XAttributeTimestamp) attribute)
                        .getValueMillis());
            } else if (attribute instanceof XAttributeID) {
                out.writeByte(5);
                XID.write(((XAttributeID) attribute).getValue(), (DataOutput) out);
            } else {
                throw new AssertionError(
                        "Unknown attribute type, cannot serialize!");
            }
            // recursive serialization of attribute map
            if (attribute instanceof XAttributeCollection) {
                Collection<XAttribute> childAttributes = ((XAttributeCollection) attribute).getCollection();
                write(kryo, out, childAttributes);
            } else {
                write(kryo, out, (XAttributeMap) attribute.getAttributes());
            }
        }
    }

    public XAttributeMap read (Kryo kryo, Input in, Class<XAttributeMap> type) {
        try {
            return read(kryo, in, type, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public XAttributeMap read (Kryo kryo, Input in, Class<XAttributeMap> type, XAttribute parent) throws IOException {
        XFactory factory = XFactoryRegistry.instance().currentDefault();
        int size = in.readInt();
        XAttributeMapImpl map = new XAttributeMapImpl(size * 2);

        kryo.reference(map);

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
            byte attributeType = in.readByte();
            if (attributeType == 0) {
                boolean value = in.readBoolean();
                attribute = factory.createAttributeBoolean(key, value,
                        extension);
            } else if (attributeType == 1) {
                double value = in.readDouble();
                attribute = factory.createAttributeContinuous(key, value,
                        extension);
            } else if (attributeType == 2) {
                long value = in.readLong();
                attribute = factory.createAttributeDiscrete(key, value,
                        extension);
            } else if (attributeType == 3) {
                String value = in.readString();
                attribute = factory.createAttributeLiteral(key, value,
                        extension);
            } else if (attributeType == 4) {
                long value = in.readLong();
                attribute = factory.createAttributeTimestamp(key, value,
                        extension);
            } else if (attributeType == 5) {
                XID value = XID.read((DataInput)in);
                attribute = factory.createAttributeID(key, value, extension);
            } else if (attributeType == 6) {
                attribute = factory.createAttributeList(key, extension);
            } else if (attributeType == 7) {
                attribute = factory.createAttributeContainer(key, extension);
            } else {
                throw new AssertionError(
                        "Unknown attribute type, cannot deserialize!");
            }
            if (parent != null && parent instanceof XAttributeCollection) {
                ((XAttributeCollection) parent).addToCollection(attribute);
            }
            // read meta-attribute map
            XAttributeMap metamap = read(kryo, in, type, attribute);
            attribute.setAttributes(metamap);
            // add to map
            map.put(key, attribute);
        }
        return map;
    }
}
