
package org.apromore.manager.model_portal;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UpdateProcessSummaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateProcessSummaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UpdateVersionSummaries" type="{http://www.apromore.org/manager/model_portal}UpdateVersionSummaryType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="domain" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="owner" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateProcessSummaryType", propOrder = {
    "updateVersionSummaries"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class UpdateProcessSummaryType {

    @XmlElement(name = "UpdateVersionSummaries", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<UpdateVersionSummaryType> updateVersionSummaries;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String name;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer id;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String domain;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String owner;

    /**
     * Gets the value of the updateVersionSummaries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the updateVersionSummaries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUpdateVersionSummaries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UpdateVersionSummaryType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<UpdateVersionSummaryType> getUpdateVersionSummaries() {
        if (updateVersionSummaries == null) {
            updateVersionSummaries = new ArrayList<UpdateVersionSummaryType>();
        }
        return this.updateVersionSummaries;
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
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setId(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDomain(String value) {
        this.domain = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-01T06:04:37+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setOwner(String value) {
        this.owner = value;
    }

}
