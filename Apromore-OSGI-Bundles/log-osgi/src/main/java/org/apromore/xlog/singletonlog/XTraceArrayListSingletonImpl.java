/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2018, 2020 The University of Melbourne.
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
import org.deckfour.xes.model.*;
import org.deckfour.xes.util.XAttributeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class XTraceArrayListSingletonImpl extends ArrayList<XEvent> implements XTrace {
    private static final long serialVersionUID = 843122019760036963L;
    private XAttributeMap attributes;

    public XTraceArrayListSingletonImpl(XAttributeMap attributeMap) {
        this.attributes = attributeMap;
    }

    public XAttributeMap getAttributes() {
        return this.attributes;
    }

    public Set<XExtension> getExtensions() {
        return XAttributeUtils.extractExtensions(this.attributes);
    }

    public void setAttributes(XAttributeMap attributes) {
        this.attributes = attributes;
    }

    public boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }

    public Object clone() {
        XTraceArrayListSingletonImpl clone = (XTraceArrayListSingletonImpl)super.clone();
        clone.attributes = (XAttributeMap)this.attributes.clone();
        clone.clear();

        for(XEvent event : this) {
            clone.add((XEvent)event.clone());
        }

        return clone;
    }

    public synchronized int insertOrdered(XEvent event) {
        if (this.size() == 0) {
            this.add(event);
            return 0;
        } else {
            XAttribute insTsAttr = event.getAttributes().get("time:timestamp");
            if (insTsAttr == null) {
                this.add(event);
                return this.size() - 1;
            } else {
                Date insTs = ((XAttributeTimestamp)insTsAttr).getValue();

                for(int i = this.size() - 1; i >= 0; --i) {
                    XAttribute refTsAttr = this.get(i).getAttributes().get("time:timestamp");
                    if (refTsAttr == null) {
                        this.add(event);
                        return this.size() - 1;
                    }

                    Date refTs = ((XAttributeTimestamp)refTsAttr).getValue();
                    if (!insTs.before(refTs)) {
                        this.add(i + 1, event);
                        return i + 1;
                    }
                }

                this.add(0, event);
                return 0;
            }
        }
    }

    public void accept(XVisitor visitor, XLog log) {
        visitor.visitTracePre(this, log);

        for(XAttribute attribute : attributes.values()) {
            attribute.accept(visitor, this);
        }

        for(XEvent event : this) {
            event.accept(visitor, this);
        }

        visitor.visitTracePost(this, log);
    }
}
