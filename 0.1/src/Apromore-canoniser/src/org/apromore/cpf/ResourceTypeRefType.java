
package org.apromore.cpf;

import java.math.BigInteger;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resourceTypeRefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resourceTypeRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="resourceTypeId" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="qualifier" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourceTypeRefType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-26T02:43:35+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ResourceTypeRefType {

    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-26T02:43:35+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger resourceTypeId;
    @XmlAttribute
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-26T02:43:35+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean optional;
    @XmlAttribute
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-26T02:43:35+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String qualifier;

    /**
     * Gets the value of the resourceTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-26T02:43:35+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public BigInteger getResourceTypeId() {
        return resourceTypeId;
    }

    /**
     * Sets the value of the resourceTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-26T02:43:35+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setResourceTypeId(BigInteger value) {
        this.resourceTypeId = value;
    }

    /**
     * Gets the value of the optional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-26T02:43:35+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-26T02:43:35+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setOptional(Boolean value) {
        this.optional = value;
    }

    /**
     * Gets the value of the qualifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-26T02:43:35+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public String getQualifier() {
        return qualifier;
    }

    /**
     * Sets the value of the qualifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-26T02:43:35+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setQualifier(String value) {
        this.qualifier = value;
    }

}
