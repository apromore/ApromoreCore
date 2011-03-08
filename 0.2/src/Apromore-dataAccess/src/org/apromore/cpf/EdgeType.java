
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
 * <p>Java class for EdgeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EdgeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attribute" type="{http://www.apromore.org/CPF}typeAttribute" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="condition" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="sourceId" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="targetId" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EdgeType", propOrder = {
    "attribute"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class EdgeType {

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	protected List<TypeAttribute> attribute;
    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger id;
    @XmlAttribute(name = "condition")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String condition;
    @XmlAttribute(name = "default")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Boolean _default;
    @XmlAttribute(name = "sourceId", required = true)
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger sourceId;
    @XmlAttribute(name = "targetId", required = true)
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger targetId;

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
     * Gets the value of the condition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public String getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setCondition(String value) {
        this.condition = value;
    }

    /**
     * Gets the value of the default property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public boolean isDefault() {
        if (_default == null) {
            return true;
        } else {
            return _default;
        }
    }

    /**
     * Sets the value of the default property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setDefault(Boolean value) {
        this._default = value;
    }

    /**
     * Gets the value of the sourceId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigInteger getSourceId() {
        return sourceId;
    }

    /**
     * Sets the value of the sourceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setSourceId(BigInteger value) {
        this.sourceId = value;
    }

    /**
     * Gets the value of the targetId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigInteger getTargetId() {
        return targetId;
    }

    /**
     * Sets the value of the targetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setTargetId(BigInteger value) {
        this.targetId = value;
    }

}
