
package org.apromore.toolbox.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;


/**
 * <p>Java class for StoreCpfInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StoreCpfInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Cpf" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StoreCpfInputMsgType", propOrder = {
    "processName",
    "version",
    "username",
    "cpf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class StoreCpfInputMsgType {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String processName;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String version;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String username;
    @XmlElement(name = "Cpf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected DataHandler cpf;

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setProcessName(String value) {
        this.processName = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the cpf property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setCpf(DataHandler value) {
        this.cpf = value;
    }

    /**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-22T05:52:30+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
