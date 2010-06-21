
package org.apromore.canoniser.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StoreNativeInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StoreNativeInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Native" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoreNativeInputMsgType", propOrder = {
    "processId",
    "version",
    "nativeType",
    "_native"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class StoreNativeInputMsgType {

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected int processId;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String version;
    @XmlElement(name = "NativeType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String nativeType;
    @XmlElement(name = "Native", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler _native;

    /**
     * Gets the value of the processId property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public int getProcessId() {
        return processId;
    }

    /**
     * Sets the value of the processId property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessId(int value) {
        this.processId = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setNativeType(String value) {
        this.nativeType = value;
    }

    /**
     * Gets the value of the native property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:19:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setNative(DataHandler value) {
        this._native = value;
    }

}
