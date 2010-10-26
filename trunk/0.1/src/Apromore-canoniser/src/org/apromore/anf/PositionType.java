
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
 * <p>Java class for positionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="positionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="x" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="y" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "positionType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T09:56:03+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class PositionType {

    @XmlAttribute(name = "x")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T09:56:03+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigDecimal x;
    @XmlAttribute(name = "y")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T09:56:03+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigDecimal y;

    /**
     * Gets the value of the x property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T09:56:03+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigDecimal getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T09:56:03+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setX(BigDecimal value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T09:56:03+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigDecimal getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T09:56:03+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setY(BigDecimal value) {
        this.y = value;
    }

	/**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-26T09:56:03+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
