package org.apromore.xlog.singletonlog;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.model.*;
import org.deckfour.xes.util.XAttributeUtils;

import java.util.Set;

public class XEventSingletonImpl implements XEvent {
    private XID id;
    private XAttributeMap attributes;

    public XEventSingletonImpl() {
        this(XIDFactory.instance().createId(), new XAttributeMapSingletonImpl());
    }

    public XEventSingletonImpl(XID id) {
        this(id, new XAttributeMapSingletonImpl());
    }

    public XEventSingletonImpl(XAttributeMap attributes) {
        this(XIDFactory.instance().createId(), attributes);
    }

    public XEventSingletonImpl(XID id, XAttributeMap attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    public XAttributeMap getAttributes() {
        return this.attributes;
    }

    public void setAttributes(XAttributeMap attributes) {
        this.attributes = attributes;
    }

    public boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }

    public Set<XExtension> getExtensions() {
        return XAttributeUtils.extractExtensions(this.attributes);
    }

    public Object clone() {
        XEventSingletonImpl clone;
        try {
            clone = (XEventSingletonImpl)super.clone();
        } catch (CloneNotSupportedException var3) {
            var3.printStackTrace();
            return null;
        }

        clone.id = XIDFactory.instance().createId();
        clone.attributes = (XAttributeMap)this.attributes.clone();
        return clone;
    }

    public boolean equals(Object o) {
        return o instanceof XEventSingletonImpl ? ((XEventSingletonImpl)o).id.equals(this.id) : false;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public XID getID() {
        return this.id;
    }

    public void setID(XID id) {
        this.id = id;
    }

    public void accept(XVisitor visitor, XTrace trace) {
        visitor.visitEventPre(this, trace);
        for(XAttribute attribute : attributes.values()) {
            attribute.accept(visitor, this);
        }

        visitor.visitEventPost(this, trace);
    }
}
