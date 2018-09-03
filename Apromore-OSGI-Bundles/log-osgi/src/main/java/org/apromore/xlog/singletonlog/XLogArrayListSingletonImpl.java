package org.apromore.xlog.singletonlog;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.*;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class XLogArrayListSingletonImpl extends ArrayList<XTrace> implements XLog {
    private static final long serialVersionUID = -9192919845877466525L;
    private XAttributeMap attributes;
    private Set<XExtension> extensions;
    private List<XEventClassifier> classifiers;
    private List<XAttribute> globalTraceAttributes;
    private List<XAttribute> globalEventAttributes;
    private XEventClassifier cachedClassifier;
    private XLogInfo cachedInfo;

    public XLogArrayListSingletonImpl(XAttributeMap attributeMap) {
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
        XLogArrayListSingletonImpl clone = (XLogArrayListSingletonImpl)super.clone();
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
