
package org.apromore.portal.model_oryx;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadNativeOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadNativeOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/portal/model_oryx}ResultType"/>
 *         &lt;element name="Native" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EditionType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadNativeOutputMsgType", propOrder = {
    "result",
    "_native",
    "nativeType",
    "editionType"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ReadNativeOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected ResultType result;
    @XmlElement(name = "Native", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler _native;
    @XmlElement(name = "NativeType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String nativeType;
    @XmlElement(name = "EditionType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String editionType;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public ResultType getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setResult(ResultType value) {
        this.result = value;
    }

    /**
     * Gets the value of the native property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setNative(DataHandler value) {
        this._native = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setNativeType(String value) {
        this.nativeType = value;
    }

    /**
     * Gets the value of the editionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getEditionType() {
        return editionType;
    }

    /**
     * Sets the value of the editionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-01-19T05:05:59+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setEditionType(String value) {
        this.editionType = value;
    }

}
