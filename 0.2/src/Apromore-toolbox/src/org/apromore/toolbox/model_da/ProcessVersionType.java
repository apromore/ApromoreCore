
package org.apromore.toolbox.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProcessVersionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessVersionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ProcessId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="VersionName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Score" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessVersionType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class ProcessVersionType {

    @XmlAttribute(name = "ProcessId")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected Integer processId;
    @XmlAttribute(name = "VersionName")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected String versionName;
    @XmlAttribute(name = "Score")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected Double score;

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setVersionName(String value) {
        this.versionName = value;
    }

    /**
     * Gets the value of the score property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public Double getScore() {
        return score;
    }

    /**
     * Sets the value of the score property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-20T02:51:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public void setScore(Double value) {
        this.score = value;
    }

}
