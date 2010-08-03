
package org.apromore.cpf;

import java.math.BigInteger;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for objectRefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="objectRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="objectId" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="type" type="{http://www.apromore.org/CPF}InputOutputType" />
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="consumed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "objectRefType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ObjectRefType {

    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger objectId;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected InputOutputType type;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean optional;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean consumed;

    /**
     * Gets the value of the objectId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public BigInteger getObjectId() {
        return objectId;
    }

    /**
     * Sets the value of the objectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setObjectId(BigInteger value) {
        this.objectId = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link InputOutputType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public InputOutputType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link InputOutputType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setType(InputOutputType value) {
        this.type = value;
    }

    /**
     * Gets the value of the optional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public boolean isOptional() {
        if (optional == null) {
            return false;
        } else {
            return optional;
        }
    }

    /**
     * Sets the value of the optional property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setOptional(Boolean value) {
        this.optional = value;
    }

    /**
     * Gets the value of the consumed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public boolean isConsumed() {
        if (consumed == null) {
            return false;
        } else {
            return consumed;
        }
    }

    /**
     * Sets the value of the consumed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setConsumed(Boolean value) {
        this.consumed = value;
    }

}
