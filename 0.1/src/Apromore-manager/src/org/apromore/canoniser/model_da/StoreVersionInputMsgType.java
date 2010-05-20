
package org.apromore.canoniser.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StoreVersionInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StoreVersionInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PreVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NewVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Domain" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "StoreVersionInputMsgType", propOrder = {
    "processId",
    "preVersion",
    "newVersion",
    "nativeType",
    "username",
    "domain",
    "_native",
    "cpf",
    "anf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class StoreVersionInputMsgType {

    @XmlElement(name = "ProcessId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected int processId;
    @XmlElement(name = "PreVersion", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String preVersion;
    @XmlElement(name = "NewVersion", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String newVersion;
    @XmlElement(name = "NativeType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String nativeType;
    @XmlElement(name = "Username", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String username;
    @XmlElement(name = "Domain", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String domain;
    @XmlElement(name = "Native", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler _native;
    @XmlElement(name = "Cpf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler cpf;
    @XmlElement(name = "Anf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler anf;

    /**
     * Gets the value of the processId property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public int getProcessId() {
        return processId;
    }

    /**
     * Sets the value of the processId property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessId(int value) {
        this.processId = value;
    }

    /**
     * Gets the value of the preVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getPreVersion() {
        return preVersion;
    }

    /**
     * Sets the value of the preVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setPreVersion(String value) {
        this.preVersion = value;
    }

    /**
     * Gets the value of the newVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getNewVersion() {
        return newVersion;
    }

    /**
     * Sets the value of the newVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setNewVersion(String value) {
        this.newVersion = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the native property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:24:45+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAnf(DataHandler value) {
        this.anf = value;
    }

}
