
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
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


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
 *         &lt;element name="attribute" type="{http://www.apromore.org/CPF}typeAttribute" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uri" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="author" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="modificationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
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
    "object",
    "attribute"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class CanonicalProcessType {

    @XmlElement(name = "Net", required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<NetType> net;
    @XmlElement(name = "ResourceType")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<ResourceTypeType> resourceType;
    @XmlElement(name = "Object")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<ObjectType> object;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	protected List<TypeAttribute> attribute;
    @XmlAttribute(name = "uri", required = true)
    @XmlSchemaType(name = "anyURI")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String uri;
    @XmlAttribute(name = "version", required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigDecimal version;
    @XmlAttribute(name = "name", required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String name;
    @XmlAttribute(name = "author")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String author;
    @XmlAttribute(name = "creationDate")
    @XmlSchemaType(name = "dateTime")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected XMLGregorianCalendar creationDate;
    @XmlAttribute(name = "modificationDate")
    @XmlSchemaType(name = "dateTime")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected XMLGregorianCalendar modificationDate;
    @XmlAttribute(name = "rootId", required = true)
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public List<ObjectType> getObject() {
        if (object == null) {
            object = new ArrayList<ObjectType>();
        }
        return this.object;
    }

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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public List<TypeAttribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<TypeAttribute>();
        }
        return this.attribute;
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the author property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public String getAuthor() {
        return author;
    }

    /**
     * Sets the value of the author property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setAuthor(String value) {
        this.author = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the modificationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public XMLGregorianCalendar getModificationDate() {
        return modificationDate;
    }

    /**
     * Sets the value of the modificationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setModificationDate(XMLGregorianCalendar value) {
        this.modificationDate = value;
    }

    /**
     * Gets the value of the rootId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setRootId(BigInteger value) {
        this.rootId = value;
    }

	/**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
