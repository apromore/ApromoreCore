package org.apromore.xlog.singletonlog;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.impl.XAttributeImpl;

public class XAttributeContinuousSingletonImpl extends XAttributeImpl implements XAttributeContinuous {
    private static final long serialVersionUID = -1789813595800348876L;
    private Double value;

    public XAttributeContinuousSingletonImpl(String key, Double value) {
        this(key, value, (XExtension)null);
    }

    public XAttributeContinuousSingletonImpl(String key, Double value, XExtension extension) {
        super(key, extension);
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String toString() {
        return Double.toString(this.value);
    }

    public Object clone() {
        return super.clone();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XAttributeContinuous)) {
            return false;
        } else {
            XAttributeContinuous other = (XAttributeContinuous)obj;
            return super.equals(other) && this.value == other.getValue();
        }
    }

    public int compareTo(XAttribute other) {
        if (!(other instanceof XAttributeContinuous)) {
            throw new ClassCastException();
        } else {
            int result = super.compareTo(other);
            return result != 0 ? result : Double.valueOf(this.value).compareTo(((XAttributeContinuous)other).getValue());
        }
    }
}
