
package org.wfmc._2002.xpdl1;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *       &lt;sequence>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Description" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}FormalParameters"/>
 *           &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}ExternalReference" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}ExtendedAttributes" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "description",
    "formalParameters",
    "externalReference",
    "extendedAttributes"
})
@XmlRootElement(name = "Application")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class Application {

    @XmlElement(name = "Description")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String description;
    @XmlElement(name = "FormalParameters")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected FormalParameters formalParameters;
    @XmlElement(name = "ExternalReference")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ExternalReference externalReference;
    @XmlElement(name = "ExtendedAttributes")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ExtendedAttributes extendedAttributes;
    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String id;
    @XmlAttribute(name = "Name")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String name;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the formalParameters property.
     * 
     * @return
     *     possible object is
     *     {@link FormalParameters }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public FormalParameters getFormalParameters() {
        return formalParameters;
    }

    /**
     * Sets the value of the formalParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormalParameters }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setFormalParameters(FormalParameters value) {
        this.formalParameters = value;
    }

    /**
     * Gets the value of the externalReference property.
     * 
     * @return
     *     possible object is
     *     {@link ExternalReference }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public ExternalReference getExternalReference() {
        return externalReference;
    }

    /**
     * Sets the value of the externalReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExternalReference }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setExternalReference(ExternalReference value) {
        this.externalReference = value;
    }

    /**
     * Gets the value of the extendedAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link ExtendedAttributes }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public ExtendedAttributes getExtendedAttributes() {
        return extendedAttributes;
    }

    /**
     * Sets the value of the extendedAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendedAttributes }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setExtendedAttributes(ExtendedAttributes value) {
        this.extendedAttributes = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setName(String value) {
        this.name = value;
    }

}
