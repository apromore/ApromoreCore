
package org.apromore.cpf;

import java.math.BigInteger;

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
@XmlType(name = "EdgeType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class EdgeType {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger id;
    @XmlAttribute
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String condition;
    @XmlAttribute(name = "default")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean _default;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger sourceId;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger targetId;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T11:49:24+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setTargetId(BigInteger value) {
        this.targetId = value;
    }

}
