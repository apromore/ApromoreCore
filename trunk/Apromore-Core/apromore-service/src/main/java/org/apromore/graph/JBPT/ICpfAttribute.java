package org.apromore.graph.JBPT;

/**
 * Interface class for {@link CpfAttribute}
 * 
 * @author Felix Mannhardt
 */
public interface ICpfAttribute {

    /**
     * Set the simple text value of this attribute.
     * 
     * @param value
     */
    void setValue(String value);

    /**
     * Get the simple text value of this attribute.
     * 
     * @return
     */
    String getValue();

    /**
     * Set the complex XML object of this attribute. Object should be of type {@link org.w3c.dom.Element} in case of a namespace less XML or
     * {@link javax.xml.bind.JAXBElement} in case of XML with a namespace. The caller should know how do deal with this element.
     * 
     * @param any
     *            {@link org.w3c.dom.Element} or {@link javax.xml.bind.JAXBElement}
     */
    void setAny(Object any);

    /**
     * Get the complex XML object of this attribute. Object should be of type {@link org.w3c.dom.Element} in case of a namespace less XML or
     * {@link javax.xml.bind.JAXBElement} in case of XML with a namespace. The caller should know how to deal with this element.
     * 
     * @return {@link org.w3c.dom.Element} or {@link javax.xml.bind.JAXBElement}
     */
    Object getAny();

}
