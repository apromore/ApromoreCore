
package org.apromore.manager.model_portal;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateProcessInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateProcessInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Native" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="EditSession" type="{http://www.apromore.org/manager/model_portal}EditSessionType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="EditSessionCode" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="PreVersion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateProcessInputMsgType", propOrder = {
    "_native",
    "editSession"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class UpdateProcessInputMsgType {

    @XmlElement(name = "Native", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected DataHandler _native;
    @XmlElement(name = "EditSession", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected EditSessionType editSession;
    @XmlAttribute(name = "EditSessionCode")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected Integer editSessionCode;
    @XmlAttribute(name = "PreVersion")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String preVersion;

    /**
     * Gets the value of the native property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setEditSession(EditSessionType value) {
        this.editSession = value;
    }

    /**
     * Gets the value of the editSessionCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public Integer getEditSessionCode() {
        return editSessionCode;
    }

    /**
     * Sets the value of the editSessionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setEditSessionCode(Integer value) {
        this.editSessionCode = value;
    }

    /**
     * Gets the value of the preVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T01:47:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setPreVersion(String value) {
        this.preVersion = value;
    }

}
