package org.apromore.graph.JBPT;

/**
 * Default implementation of an CPF attribute.
 * 
 * @author Felix Mannhardt
 *
 */
public class CpfAttribute implements ICpfAttribute {
    
    private String value;
    private Object any;

    public CpfAttribute(String value, Object any) {
        this.value = value;
        this.any = any;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setAny(Object any) {
        this.any = any;
    }

    @Override
    public Object getAny() {
        return this.any;
    }

}
