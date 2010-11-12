
package org.apromore.anf;

import java.math.BigDecimal;
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
 * <p>Java class for AnnotationsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AnnotationsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Annotation" type="{http://www.apromore.org/ANF}AnnotationType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uri" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="author" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="modificationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="cpfUri" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="cpfVersion" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnnotationsType", propOrder = {
    "annotation"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class AnnotationsType {

    @XmlElement(name = "Annotation", required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<AnnotationType> annotation;
    @XmlAttribute(name = "uri", required = true)
    @XmlSchemaType(name = "anyURI")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String uri;
    @XmlAttribute(name = "version", required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigDecimal version;
    @XmlAttribute(name = "name")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String name;
    @XmlAttribute(name = "author")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String author;
    @XmlAttribute(name = "creationDate")
    @XmlSchemaType(name = "dateTime")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected XMLGregorianCalendar creationDate;
    @XmlAttribute(name = "modificationDate")
    @XmlSchemaType(name = "dateTime")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected XMLGregorianCalendar modificationDate;
    @XmlAttribute(name = "cpfUri", required = true)
    @XmlSchemaType(name = "anyURI")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String cpfUri;
    @XmlAttribute(name = "cpfVersion", required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigDecimal cpfVersion;

    /**
     * Gets the value of the annotation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annotation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnotation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnnotationType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public List<AnnotationType> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<AnnotationType>();
        }
        return this.annotation;
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setModificationDate(XMLGregorianCalendar value) {
        this.modificationDate = value;
    }

    /**
     * Gets the value of the cpfUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public String getCpfUri() {
        return cpfUri;
    }

    /**
     * Sets the value of the cpfUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setCpfUri(String value) {
        this.cpfUri = value;
    }

    /**
     * Gets the value of the cpfVersion property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigDecimal getCpfVersion() {
        return cpfVersion;
    }

    /**
     * Sets the value of the cpfVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setCpfVersion(BigDecimal value) {
        this.cpfVersion = value;
    }

	/**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:56:43+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
