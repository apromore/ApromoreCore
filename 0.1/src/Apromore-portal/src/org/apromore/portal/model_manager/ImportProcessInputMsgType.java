
package org.apromore.portal.model_manager;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImportProcessInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImportProcessInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessDescription" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ProcessName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Domain" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Last_update" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Creation_date" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Documentation" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImportProcessInputMsgType", propOrder = {
    "processDescription"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ImportProcessInputMsgType {

    @XmlElement(name = "ProcessDescription", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler processDescription;
    @XmlAttribute(name = "ProcessName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String processName;
    @XmlAttribute(name = "VersionName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String versionName;
    @XmlAttribute(name = "NativeType")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String nativeType;
    @XmlAttribute(name = "Domain")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String domain;
    @XmlAttribute(name = "Username")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String username;
    @XmlAttribute(name = "Last_update")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String lastUpdate;
    @XmlAttribute(name = "Creation_date")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String creationDate;
    @XmlAttribute(name = "Documentation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String documentation;

    /**
     * Gets the value of the processDescription property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public DataHandler getProcessDescription() {
        return processDescription;
    }

    /**
     * Sets the value of the processDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessDescription(DataHandler value) {
        this.processDescription = value;
    }

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setNativeType(String value) {
        this.nativeType = value;
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the lastUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the value of the lastUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setLastUpdate(String value) {
        this.lastUpdate = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCreationDate(String value) {
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:27:36+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setDocumentation(String value) {
        this.documentation = value;
    }

}
