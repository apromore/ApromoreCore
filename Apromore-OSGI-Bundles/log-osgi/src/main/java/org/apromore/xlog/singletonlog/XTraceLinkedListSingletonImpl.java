package org.apromore.xlog.singletonlog;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.*;
import org.deckfour.xes.util.XAttributeUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

public class XTraceLinkedListSingletonImpl extends LinkedList<XEvent> implements XTrace {
    private static final long serialVersionUID = 843122019760036963L;
    private XAttributeMap attributes;

    public XTraceLinkedListSingletonImpl(XAttributeMap attributeMap) {
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
        XTraceLinkedListSingletonImpl clone = (XTraceLinkedListSingletonImpl)super.clone();
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
