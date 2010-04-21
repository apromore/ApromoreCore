
package org.wfmc._2002.xpdl1;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="STRING"/>
 *             &lt;enumeration value="FLOAT"/>
 *             &lt;enumeration value="INTEGER"/>
 *             &lt;enumeration value="REFERENCE"/>
 *             &lt;enumeration value="DATETIME"/>
 *             &lt;enumeration value="BOOLEAN"/>
 *             &lt;enumeration value="PERFORMER"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "BasicType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class BasicType {

    @XmlAttribute(name = "Type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String type;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setType(String value) {
        this.type = value;
    }

}
