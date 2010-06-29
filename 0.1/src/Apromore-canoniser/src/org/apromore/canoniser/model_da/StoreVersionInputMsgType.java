
package org.apromore.canoniser.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="Native" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="Cpf" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="Anf" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ProcessId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Domain" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoreVersionInputMsgType", propOrder = {
    "_native",
    "cpf",
    "anf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class StoreVersionInputMsgType {

    @XmlElement(name = "Native", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler _native;
    @XmlElement(name = "Cpf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler cpf;
    @XmlElement(name = "Anf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler anf;
    @XmlAttribute(name = "ProcessId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer processId;
    @XmlAttribute(name = "NativeType")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String nativeType;
    @XmlAttribute(name = "Domain")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String domain;

    /**
     * Gets the value of the native property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAnf(DataHandler value) {
        this.anf = value;
    }

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessId(Integer value) {
        this.processId = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T04:37:55+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDomain(String value) {
        this.domain = value;
    }

}
