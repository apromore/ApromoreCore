
package org.apromore.manager.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadEditSessionOutputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadEditSessionOutputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.apromore.org/data_access/model_manager}ResultType"/>
 *         &lt;element name="Native" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="EditSession" type="{http://www.apromore.org/data_access/model_manager}EditSessionType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadEditSessionOutputMsgType", propOrder = {
    "result",
    "_native",
    "editSession"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T03:56:32+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ReadEditSessionOutputMsgType {

    @XmlElement(name = "Result", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T03:56:32+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ResultType result;
    @XmlElement(name = "Native", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T03:56:32+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataHandler _native;
    @XmlElement(name = "EditSession", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T03:56:32+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected EditSessionType editSession;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ResultType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T03:56:32+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T03:56:32+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T03:56:32+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T03:56:32+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setNative(DataHandler value) {
        this._native = value;
    }

    /**
     * Gets the value of the editSession property.
     * 
     * @return
     *     possible object is
     *     {@link EditSessionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T03:56:32+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public EditSessionType getEditSession() {
        return editSession;
    }

    /**
     * Sets the value of the editSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link EditSessionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-29T03:56:32+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setEditSession(EditSessionType value) {
        this.editSession = value;
    }

}
