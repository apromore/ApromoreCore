
package org.apromore.cpf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
 *       &lt;sequence>
 *         &lt;element name="attribute" type="{http://www.apromore.org/CPF}typeAttribute" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
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
@XmlType(name = "resourceTypeRefType", propOrder = {
    "attribute"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ResourceTypeRefType {

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	protected List<TypeAttribute> attribute;
    @XmlAttribute(name = "id")
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger id;
    @XmlAttribute(name = "resourceTypeId")
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger resourceTypeId;
    @XmlAttribute(name = "optional")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Boolean optional;
    @XmlAttribute(name = "qualifier")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String qualifier;

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeAttribute }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public List<TypeAttribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<TypeAttribute>();
        }
        return this.attribute;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the resourceTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setQualifier(String value) {
        this.qualifier = value;
    }

}
