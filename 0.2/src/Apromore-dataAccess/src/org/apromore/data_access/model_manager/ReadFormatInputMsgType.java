
package org.apromore.data_access.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadFormatInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadFormatInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="processId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadFormatInputMsgType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T10:43:47+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ReadFormatInputMsgType {

    @XmlAttribute(name = "processId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T10:43:47+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Integer processId;
    @XmlAttribute(name = "version")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T10:43:47+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String version;
    @XmlAttribute(name = "format")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T10:43:47+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String format;

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T10:43:47+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T10:43:47+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessId(Integer value) {
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T10:43:47+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T10:43:47+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T10:43:47+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-03T10:43:47+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setFormat(String value) {
        this.format = value;
    }

}
