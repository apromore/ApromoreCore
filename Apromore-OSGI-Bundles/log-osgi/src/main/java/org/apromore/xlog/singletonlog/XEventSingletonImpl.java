/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
