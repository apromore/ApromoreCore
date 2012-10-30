package org.apromore.graph.canonical;

/**
 * Default implementation of an CPF attribute.
 * 
 * @author Felix Mannhardt
 *
 */
public class Attribute implements IAttribute {
    
    private String value;
    private Object any;

    public Attribute(String value, Object any) {
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
    public java.lang.Object getAny() {
        return this.any;
    }

}
