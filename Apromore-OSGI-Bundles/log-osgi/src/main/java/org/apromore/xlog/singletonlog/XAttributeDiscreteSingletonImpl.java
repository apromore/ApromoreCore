package org.apromore.xlog.singletonlog;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.impl.XAttributeImpl;

public class XAttributeDiscreteSingletonImpl extends XAttributeImpl implements XAttributeDiscrete {
    private static final long serialVersionUID = -1789813595800348876L;
    private Long value;

    public XAttributeDiscreteSingletonImpl(String key, Long value) {
        this(key, value, (XExtension) null);
    }

    public XAttributeDiscreteSingletonImpl(String key, Long value, XExtension extension) {
        super(key, extension);
        this.value = value;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String toString() {
        return Long.toString(this.value);
    }

    public Object clone() {
        return super.clone();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XAttributeDiscrete)) {
            return false;
        } else {
            XAttributeDiscrete other = (XAttributeDiscrete)obj;
            return super.equals(other) && this.value == other.getValue();
        }
    }

    public int compareTo(XAttribute other) {
        if (!(other instanceof XAttributeDiscrete)) {
            throw new ClassCastException();
        } else {
            int result = super.compareTo(other);
            return result != 0 ? result : Long.valueOf(this.value).compareTo(((XAttributeDiscrete)other).getValue());
        }
    }
}
