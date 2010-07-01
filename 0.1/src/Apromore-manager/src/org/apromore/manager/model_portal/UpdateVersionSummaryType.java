
package org.apromore.manager.model_portal;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateVersionSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateVersionSummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ranking" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="preName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateVersionSummaryType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class UpdateVersionSummaryType {

    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer ranking;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String name;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String preName;

    /**
     * Gets the value of the ranking property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Integer getRanking() {
        return ranking;
    }

    /**
     * Sets the value of the ranking property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setRanking(Integer value) {
        this.ranking = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the preName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getPreName() {
        return preName;
    }

    /**
     * Sets the value of the preName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setPreName(String value) {
        this.preName = value;
    }

}
