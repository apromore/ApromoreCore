
package org.apromore.anf;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for sizeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sizeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sizeType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class SizeType {

    @XmlAttribute(name = "width")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigDecimal width;
    @XmlAttribute(name = "height")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigDecimal height;

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigDecimal getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setWidth(BigDecimal value) {
        this.width = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigDecimal getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setHeight(BigDecimal value) {
        this.height = value;
    }

	/**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
