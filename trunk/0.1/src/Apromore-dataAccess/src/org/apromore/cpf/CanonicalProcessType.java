
package org.apromore.cpf;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CanonicalProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CanonicalProcessType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Net" type="{http://www.apromore.org/CPF}NetType" maxOccurs="unbounded"/>
 *         &lt;element name="ResourceType" type="{http://www.apromore.org/CPF}ResourceTypeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Object" type="{http://www.apromore.org/CPF}ObjectType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uri" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rootId" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CanonicalProcessType", propOrder = {
    "net",
    "resourceType",
    "object"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class CanonicalProcessType {

    @XmlElement(name = "Net", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<NetType> net;
    @XmlElement(name = "ResourceType")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<ResourceTypeType> resourceType;
    @XmlElement(name = "Object")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<ObjectType> object;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String uri;
    @XmlAttribute(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigDecimal version;
    @XmlAttribute(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String name;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "positiveInteger")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger rootId;

    /**
     * Gets the value of the net property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the net property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NetType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<NetType> getNet() {
        if (net == null) {
            net = new ArrayList<NetType>();
        }
        return this.net;
    }

    /**
     * Gets the value of the resourceType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceTypeType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<ResourceTypeType> getResourceType() {
        if (resourceType == null) {
            resourceType = new ArrayList<ResourceTypeType>();
        }
        return this.resourceType;
    }

    /**
     * Gets the value of the object property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the object property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObjectType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<ObjectType> getObject() {
        if (object == null) {
            object = new ArrayList<ObjectType>();
        }
        return this.object;
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getUri() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setUri(String value) {
        this.uri = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public BigDecimal getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setVersion(BigDecimal value) {
        this.version = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the rootId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public BigInteger getRootId() {
        return rootId;
    }

    /**
     * Sets the value of the rootId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-23T04:18:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setRootId(BigInteger value) {
        this.rootId = value;
    }

}
