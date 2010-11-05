
package org.apromore.toolbox.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Cpf_idType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Cpf_idType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ProcessId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Cpf_idType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class CpfIdType {

    @XmlAttribute(name = "ProcessId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Integer processId;
    @XmlAttribute(name = "VersionName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String versionName;

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-09-18T01:19:39+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public void setVersionName(String value) {
        this.versionName = value;
    }

}
