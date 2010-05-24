
package org.apromore.canoniser.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="ProcessName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Domain" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="versionName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Native" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="Cpf" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="Anf" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoreNativeCpfInputMsgType", propOrder = {
    "processName",
    "domain",
    "nativeType",
    "username",
    "versionName",
    "_native",
    "cpf",
    "anf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class StoreNativeCpfInputMsgType {

    @XmlElement(name = "ProcessName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String processName;
    @XmlElement(name = "Domain", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String domain;
    @XmlElement(name = "NativeType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String nativeType;
    @XmlElement(name = "Username", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String username;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String versionName;
    @XmlElement(name = "Native", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler _native;
    @XmlElement(name = "Cpf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler cpf;
    @XmlElement(name = "Anf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler anf;

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the native property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-24T02:52:12+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAnf(DataHandler value) {
        this.anf = value;
    }

}
