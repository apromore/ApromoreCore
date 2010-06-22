
package org.apromore.anf;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


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
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class PositionType {

    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigDecimal x;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigDecimal y;

    /**
     * Gets the value of the x property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setY(BigDecimal value) {
        this.y = value;
    }

}
