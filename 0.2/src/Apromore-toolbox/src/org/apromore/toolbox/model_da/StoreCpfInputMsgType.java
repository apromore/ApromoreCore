
package org.apromore.toolbox.model_da;

import javax.activation.DataHandler;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="domain" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "domain",
    "username",
    "cpf"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class StoreCpfInputMsgType {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String processName;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String version;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String domain;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String username;
    @XmlElement(name = "Cpf", required = true)
    @XmlMimeType("application/octet-stream")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected DataHandler cpf;

    /**
     * Gets the value of the processName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-19T05:29:30+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setCpf(DataHandler value) {
        this.cpf = value;
    }

}
