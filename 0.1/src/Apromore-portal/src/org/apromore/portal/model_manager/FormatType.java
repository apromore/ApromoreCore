
package org.apromore.portal.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for FormatType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FormatType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="extension" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FormatType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class FormatType {

    @XmlAttribute(name = "format")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String format;
    @XmlAttribute(name = "extension")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String extension;

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-18T01:22:04+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
