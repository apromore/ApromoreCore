
package org.apromore.toolbox.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MergedSource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MergedSource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
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
@XmlType(name = "MergedSource")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:47:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class MergedSource {

    @XmlAttribute(name = "processId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:47:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected Integer processId;
    @XmlAttribute(name = "versionName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:47:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String versionName;

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:47:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:47:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:47:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:47:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setVersionName(String value) {
        this.versionName = value;
    }

}
