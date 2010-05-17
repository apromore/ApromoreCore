
package org.apromore.data_access.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WriteEditSessionInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WriteEditSessionInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nativeType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="processId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="versionName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WriteEditSessionInputMsgType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class WriteEditSessionInputMsgType {

    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String username;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String nativeType;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer processId;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String versionName;

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the nativeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setNativeType(String value) {
        this.nativeType = value;
    }

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessId(Integer value) {
        this.processId = value;
    }

    /**
     * Gets the value of the versionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-17T05:46:40+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setVersionName(String value) {
        this.versionName = value;
    }

}
