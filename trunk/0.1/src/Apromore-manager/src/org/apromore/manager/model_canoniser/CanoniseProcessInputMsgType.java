
package org.apromore.manager.model_canoniser;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CanoniseProcessInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CanoniseProcessInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NativeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProcessDescription" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CanoniseProcessInputMsgType", propOrder = {
    "processName",
    "nativeType",
    "processDescription"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T05:13:34+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class CanoniseProcessInputMsgType {

    @XmlElement(name = "ProcessName", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T05:13:34+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String processName;
    @XmlElement(name = "NativeType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T05:13:34+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String nativeType;
    @XmlElement(name = "ProcessDescription", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T05:13:34+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler processDescription;

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T05:13:34+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T05:13:34+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessName(String value) {
        this.processName = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T05:13:34+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T05:13:34+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setNativeType(String value) {
        this.nativeType = value;
    }

    /**
     * Gets the value of the processDescription property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T05:13:34+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T05:13:34+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessDescription(DataHandler value) {
        this.processDescription = value;
    }

}
