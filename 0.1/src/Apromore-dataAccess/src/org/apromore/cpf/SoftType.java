
package org.apromore.cpf;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SoftType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SoftType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.apromore.org/CPF}ObjectType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SoftType", propOrder = {
    "type"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-14T05:25:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class SoftType
    extends ObjectType
{

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-14T05:25:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String type;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-14T05:25:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-14T05:25:11+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setType(String value) {
        this.type = value;
    }

}
