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

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.*;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class XLogLinkedListSingletonImpl extends LinkedList<XTrace> implements XLog {
    private static final long serialVersionUID = -9192919845877466525L;
    private XAttributeMap attributes;
    private Set<XExtension> extensions;
    private List<XEventClassifier> classifiers;
    private List<XAttribute> globalTraceAttributes;
    private List<XAttribute> globalEventAttributes;
    private XEventClassifier cachedClassifier;
    private XLogInfo cachedInfo;

    public XLogLinkedListSingletonImpl(XAttributeMap attributeMap) {
        this.attributes = attributeMap;
        this.extensions = new UnifiedSet<>();
        this.classifiers = new ArrayList();
        this.globalTraceAttributes = new ArrayList();
        this.globalEventAttributes = new ArrayList();
        this.cachedClassifier = null;
        this.cachedInfo = null;
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
        return this.extensions;
    }

    public Object clone() {
        XLogLinkedListSingletonImpl clone = (XLogLinkedListSingletonImpl)super.clone();
        clone.attributes = (XAttributeMap)this.attributes.clone();
        clone.extensions = new UnifiedSet(this.extensions);
        clone.classifiers = new ArrayList(this.classifiers);
        clone.globalTraceAttributes = new ArrayList(this.globalTraceAttributes);
        clone.globalEventAttributes = new ArrayList(this.globalEventAttributes);
        clone.cachedClassifier = null;
        clone.cachedInfo = null;
        clone.clear();

        for(XTrace trace : this) {
            clone.add((XTrace)trace.clone());
        }

        return clone;
    }

    public List<XEventClassifier> getClassifiers() {
        return this.classifiers;
    }

    public List<XAttribute> getGlobalEventAttributes() {
        return this.globalEventAttributes;
    }

    public List<XAttribute> getGlobalTraceAttributes() {
        return this.globalTraceAttributes;
    }

    public boolean accept(XVisitor visitor) {
        if (!visitor.precondition()) {
            return false;
        } else {
            visitor.init(this);
            visitor.visitLogPre(this);

            for(XExtension extension : extensions) {
                extension.accept(visitor, this);
            }

            for(XEventClassifier classifier : classifiers) {
                classifier.accept(visitor, this);
            }

            for(XAttribute attribute : attributes.values()) {
                attribute.accept(visitor, this);
            }

            for(XTrace trace : this) {
                trace.accept(visitor, this);
            }

            visitor.visitLogPost(this);
            return true;
        }
    }

    public XLogInfo getInfo(XEventClassifier classifier) {
        return classifier.equals(this.cachedClassifier) ? this.cachedInfo : null;
    }

    public void setInfo(XEventClassifier classifier, XLogInfo info) {
        this.cachedClassifier = classifier;
        this.cachedInfo = info;
    }
}
