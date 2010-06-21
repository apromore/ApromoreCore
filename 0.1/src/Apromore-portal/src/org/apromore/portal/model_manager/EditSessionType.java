
package org.apromore.portal.model_manager;

import java.sql.Date;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EditSessionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EditSessionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nativeType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="processId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="processName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="versionName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="domain" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="last_update" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="creation_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="documentation" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EditSessionType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class EditSessionType {

    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String username;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String nativeType;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer processId;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String processName;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String versionName;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String domain;
    @XmlAttribute(name = "last_update")
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Date lastUpdate;
    @XmlAttribute(name = "creation_date")
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Date creationDate;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String documentation;

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getNativeType() {
        return nativeType;
    }

    /**
     * Sets the value of the nativeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setNativeType(String value) {
        this.nativeType = value;
    }

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Integer getProcessId() {
        return processId;
    }

    /**
     * Sets the value of the processId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessId(Integer value) {
        this.processId = value;
    }

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getProcessName() {
        return processName;
    }

    /**
     * Sets the value of the processName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessName(String value) {
        this.processName = value;
    }

    /**
     * Gets the value of the versionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getVersionName() {
        return versionName;
    }

    /**
     * Sets the value of the versionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the lastUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the value of the lastUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setLastUpdate(Date value) {
        this.lastUpdate = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link Date }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Date }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setCreationDate(Date value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the documentation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Sets the value of the documentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:48:10+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDocumentation(String value) {
        this.documentation = value;
    }

}
