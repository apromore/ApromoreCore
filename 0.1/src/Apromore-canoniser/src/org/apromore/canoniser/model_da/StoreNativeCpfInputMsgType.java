
package org.apromore.canoniser.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for StoreNativeCpfInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StoreNativeCpfInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Native" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="Cpf" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="Anf" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ProcessName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Domain" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="versionName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="documentation" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="last_update" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="creation_date" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoreNativeCpfInputMsgType", propOrder = {
    "_native",
    "cpf",
    "anf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class StoreNativeCpfInputMsgType {

    @XmlElement(name = "Native", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler _native;
    @XmlElement(name = "Cpf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler cpf;
    @XmlElement(name = "Anf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler anf;
    @XmlAttribute(name = "ProcessName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String processName;
    @XmlAttribute(name = "Domain")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String domain;
    @XmlAttribute(name = "NativeType")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String nativeType;
    @XmlAttribute(name = "Username")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String username;
    @XmlAttribute(name = "versionName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String versionName;
    @XmlAttribute(name = "documentation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String documentation;
    @XmlAttribute(name = "last_update")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String lastUpdate;
    @XmlAttribute(name = "creation_date")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String creationDate;

    /**
     * Gets the value of the native property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public DataHandler getNative() {
        return _native;
    }

    /**
     * Sets the value of the native property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setNative(DataHandler value) {
        this._native = value;
    }

    /**
     * Gets the value of the cpf property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public DataHandler getCpf() {
        return cpf;
    }

    /**
     * Sets the value of the cpf property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCpf(DataHandler value) {
        this.cpf = value;
    }

    /**
     * Gets the value of the anf property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public DataHandler getAnf() {
        return anf;
    }

    /**
     * Sets the value of the anf property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setAnf(DataHandler value) {
        this.anf = value;
    }

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessName(String value) {
        this.processName = value;
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setNativeType(String value) {
        this.nativeType = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the versionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the documentation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setDocumentation(String value) {
        this.documentation = value;
    }

    /**
     * Gets the value of the lastUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCreationDate(String value) {
        this.creationDate = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-10-21T02:28:53+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
