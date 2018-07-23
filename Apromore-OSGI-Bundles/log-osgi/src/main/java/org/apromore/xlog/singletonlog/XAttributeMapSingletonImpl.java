package org.apromore.xlog.singletonlog;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Map;

public class XAttributeMapSingletonImpl extends UnifiedMap<String, XAttribute> implements XAttributeMap {
    private static final long serialVersionUID = 2701256420845748051L;

    public XAttributeMapSingletonImpl() {
        this(3);
    }

    public XAttributeMapSingletonImpl(int size) {
        super(size);
    }

    public XAttributeMapSingletonImpl(Map<String, XAttribute> template) {
        this.putAll(template);
    }

    public UnifiedMap<String, XAttribute> clone() {
        XAttributeMapSingletonImpl clone = new XAttributeMapSingletonImpl(this.size());

        for(String key : keySet()) {
            clone.put(key, (XAttribute)(this.get(key)).clone());
        }

        return clone;
    }
}
